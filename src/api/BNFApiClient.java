package api;

import java.io.File;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import model.Book;
import model.DetailedBook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import dao.BookDao;

public class BNFApiClient {
    private BookDao bookDao = new BookDao();
    private final HttpClient client = HttpClient.newHttpClient();

    /**
     * Method to fetch book details by ISBN.
     * @param isbn The ISBN of the book.
     * @return Document containing the book details.
     */
    public Document fetchBookDetailsByISBN(String isbn) {
        String query = "bib.isbn adj \"" + isbn + "\"";
        return fetchDetails(query);
    }

    /**
     * Method to search for books with detailed information using a query string.
     * @param query The search query string.
     * @return List of DetailedBook objects containing the search results.
     */
    public List<DetailedBook> searchBooks(String query) {
        Document doc = fetchDetails(query);
        if (doc != null) {
            return extractBooks(doc);
        }
        return new ArrayList<>();
    }

    /**
     * Method to send a request to the BNF API and return the response as a Document.
     * @param query The query string.
     * @return Document containing the API response.
     */
    private Document fetchDetails(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
            String url = "http://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=" + encodedQuery;

            // Create HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                                             .uri(URI.create(url))
                                             .header("Accept", "application/xml")
                                             .build();

            // Send HTTP request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Parse the XML response
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true); // Important for XML documents with namespaces
                DocumentBuilder builder = factory.newDocumentBuilder();
                return builder.parse(new InputSource(new StringReader(response.body())));
            } else {
                System.err.println("Request failed: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Method to check the availability of a book by ISBN.
     * @param isbn The ISBN of the book.
     * @return String indicating the availability status of the book.
     */
    private String checkAvailability(String isbn) {
        Book book = bookDao.findBookByISBN(isbn);
        if (book != null) {
            int stock = book.getStock();
            if (stock > 0) {
                return "In stock (" + stock + ")";
            } else {
                return "Out of stock";
            }
        } else {
            return "Not available";
        }
    }

    /**
     * Method to extract books from the XML Document and create DetailedBook objects.
     * @param doc The XML Document containing book details.
     * @return List of DetailedBook objects extracted from the XML Document.
     */
    private List<DetailedBook> extractBooks(Document doc) {
        List<DetailedBook> books = new ArrayList<>();
        NodeList recordNodes = doc.getElementsByTagNameNS("*", "record");
        for (int i = 0; i < recordNodes.getLength(); i++) {
            Element record = (Element) recordNodes.item(i);
            String isbn = extractField(record, "010", 'a');
            String title = extractField(record, "200", 'a');
            String author = extractField(record, "700", 'a');
            String publicationDate = extractField(record, "210", 'd');
            String edition = extractField(record, "205", 'a');
            String collection = extractField(record, "225", 'a');
            String availability = checkAvailability(isbn);

            if (title != null && !title.isBlank()) { // Include books with a title even if ISBN is missing
                books.add(new DetailedBook(isbn, title, author, publicationDate, edition, collection, 0, availability));
            }
        }
        return books;
    }

    /**
     * Method to extract specific data fields from the UNIMARC data field.
     * @param record The XML element representing the record.
     * @param tag The tag of the data field.
     * @param subfieldCode The code of the subfield.
     * @return String containing the extracted data.
     */
    private String extractField(Element record, String tag, char subfieldCode) {
        NodeList dataFields = record.getElementsByTagNameNS("*", "datafield");
        for (int i = 0; i < dataFields.getLength(); i++) {
            Element field = (Element) dataFields.item(i);
            if (field.getAttribute("tag").equals(tag)) {
                NodeList subFields = field.getElementsByTagNameNS("*", "subfield");
                for (int j = 0; j < subFields.getLength(); j++) {
                    Element subField = (Element) subFields.item(j);
                    if (subField.getAttribute("code").charAt(0) == subfieldCode) {
                        return subField.getTextContent().trim();
                    }
                }
            }
        }
        return ""; // Return an empty string if the information is not found
    }

    /**
     * Method to extract ISBN from the XML Document.
     * @param doc The XML Document containing the ISBN.
     * @return String containing the ISBN.
     */
    private String extractISBN(Document doc) {
        NodeList nodes = doc.getElementsByTagNameNS("*", "subfield");
        for (int i = 0; i < nodes.getLength(); i++) {
            if ("a".equals(nodes.item(i).getAttributes().getNamedItem("code").getNodeValue())) {
                return nodes.item(i).getTextContent();
            }
        }
        return null;
    }

    /**
     * Method to extract title from the XML Document.
     * @param doc The XML Document containing the title.
     * @return String containing the title.
     */
    private String extractTitle(Document doc) {
        NodeList nodes = doc.getElementsByTagNameNS("*", "subfield");
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getParentNode().getAttributes().getNamedItem("tag").getNodeValue().equals("200")
                && nodes.item(i).getAttributes().getNamedItem("code").getNodeValue().equals("a")) {
                return nodes.item(i).getTextContent();
            }
        }
        return null;
    }

    /**
     * Method to extract author from the XML Document.
     * @param doc The XML Document containing the author.
     * @return String containing the author.
     */
    private String extractAuthor(Document doc) {
        NodeList nodes = doc.getElementsByTagNameNS("*", "subfield");
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getParentNode().getAttributes().getNamedItem("tag").getNodeValue().equals("700")
                && nodes.item(i).getAttributes().getNamedItem("code").getNodeValue().equals("a")) {
                return nodes.item(i).getTextContent();
            }
        }
        return null;
    }

    /**
     * Method to fetch title by ISBN.
     * @param isbn The ISBN of the book.
     * @return String containing the title of the book.
     */
    public String fetchTitle(String isbn) {
        Document bookData = fetchBookDetailsByISBN(isbn);
        if (bookData != null) {
            return extractTitle(bookData);
        } else {
            return "Titre non disponible";
        }
    }

    /**
     * Method to fetch author by ISBN.
     * @param isbn The ISBN of the book.
     * @return String containing the author of the book.
     */
    public String fetchAuthor(String isbn) {
        Document bookData = fetchBookDetailsByISBN(isbn);
        if (bookData != null) {
            return extractAuthor(bookData);
        } else {
            return "Auteur non disponible";
        }
    }

    /**
     * New method to read a local XML file.
     * @param filePath The path to the local XML file.
     * @return Document containing the parsed XML data.
     */
    public Document readLocalXMLFile(String filePath) {
        try {
            File file = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true); // Important for XML documents with namespaces
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
