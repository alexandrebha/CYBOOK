package application;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import javafx.scene.control.cell.PropertyValueFactory;

import org.w3c.dom.Document;

import dao.BookDao;
import dao.LoanDao;
import api.BNFApiClient;
import model.Book;
import model.DetailedBook;
/**
 * Controller class for managing book-related operations in the application.
 */
public class BooksViewController {
    // FXML annotations for UI elements
    @FXML private TextField titleField; // TextField for entering the book title
    @FXML private TextField authorField; // TextField for entering the book author
    @FXML private TextField isbnField; // TextField for entering the book ISBN
    @FXML private TextField inputField; // TextField for adding books by ISBN
    @FXML private Label responseLabel; // Response after adding a book
    @FXML private Label searchResponseLabel; // Response after search
    @FXML private ComboBox<String> topBooksComboBox; // ComboBox to display top borrowed books
    @FXML private ComboBox<Book> bookComboBox; // ComboBox for selecting a book
    @FXML private TableView<DetailedBook> booksTable; // TableView to display book details
    @FXML private TableColumn<DetailedBook, String> titleColumn; // Column for book title
    @FXML private TableColumn<DetailedBook, String> authorColumn; // Column for book author
    @FXML private TableColumn<DetailedBook, String> isbnColumn; // Column for book ISBN
    @FXML private TableColumn<DetailedBook, String> editionColumn; // Column for book edition
    @FXML private TableColumn<DetailedBook, String> dateColumn; // Column for publication date
    @FXML private TableColumn<DetailedBook, String> collectionColumn; // Column for book collection
    @FXML private TableColumn<DetailedBook, String> availabilityColumn; // Column for book availability
    @FXML private TableView<Book> topBooksTable; // TableView to display top books
    @FXML private TableColumn<Book, String> topTitleColumn; // Column for top book title
    @FXML private TableColumn<Book, String> topAuthorColumn; // Column for top book author
    @FXML private TableColumn<Book, String> topIsbnColumn; // Column for top book ISBN
    @FXML private TableColumn<Book, Integer> topLoansColumn; // Column for top book loans count

    // Instances of API client and DAOs
    private BNFApiClient apiClient = new BNFApiClient();
    private BookDao bookDao = new BookDao();
    private LoanDao loanDao = new LoanDao();
    
    /**
     * Default constructor.
     */
    public BooksViewController() {
        // Default constructor
    }

    /**
     * Initialize method called after FXML is loaded.
     * It sets up the TableView columns and loads the books and top borrowed books from the database.
     */
    public void initialize() {
        long startTime = System.currentTimeMillis();
        
        loadBooks();
        long loadBooksTime = System.currentTimeMillis();
        System.out.println("Time taken to loadBooks: " + (loadBooksTime - startTime) + "ms");
        
        loadTopBooks();
        long loadTopBooksTime = System.currentTimeMillis();
        System.out.println("Time taken to loadTopBooks: " + (loadTopBooksTime - loadBooksTime) + "ms");
        
        // Set cell value factories for TableView columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        editionColumn.setCellValueFactory(new PropertyValueFactory<>("edition"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("publicationDate"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));

        topTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        topAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        topIsbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        topLoansColumn.setCellValueFactory(new PropertyValueFactory<>("loansCount"));

        long endTime = System.currentTimeMillis();
        System.out.println("Total time taken for initialize: " + (endTime - startTime) + "ms");
    }

    /**
     * Load books from the database and filter only the books that are in stock.
     */
    private void loadBooks() {
        long startTime = System.currentTimeMillis();
        List<Book> books = bookDao.getAllBooks();
        books = books.stream()
                     .filter(book -> book.getStock() > 0)
                     .sorted(Comparator.comparing(Book::getIsbn))
                     .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken for loadBooks: " + (endTime - startTime) + "ms");
    }

    /**
     * Load top borrowed books from the database.
     */
    private void loadTopBooks() {
        long startTime = System.currentTimeMillis();
        List<Book> topBooks = loanDao.fetchTopBooks();
        ObservableList<Book> topBooksObservableList = FXCollections.observableArrayList(topBooks);
        Platform.runLater(() -> {
            topBooksTable.setItems(topBooksObservableList);
        });
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken for loadTopBooks: " + (endTime - startTime) + "ms");
    }

    /**
     * Handle search action triggered by the search button.
     * It prepares the query based on the filled fields and displays the search results in the TableView.
     */
    @FXML
    private void handleSearchAction() {
        long startTime = System.currentTimeMillis();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String isbn = isbnField.getText().trim();

        // Prepare the query based on filled fields
        List<String> conditions = new ArrayList<>();
        if (!title.isEmpty()) conditions.add("bib.title = \"" + title + "\"");
        if (!author.isEmpty()) conditions.add("bib.author = \"" + author + "\"");
        if (!isbn.isEmpty()) conditions.add("bib.isbn = \"" + isbn + "\"");

        if (conditions.isEmpty()) {
            searchResponseLabel.setText("Please fill in at least one of the search fields.");
            updateResponse(searchResponseLabel, "Please fill in at least one of the search fields.", false);
            return;
        }
        String query = "(" + String.join(") and (", conditions) + ")";
        List<DetailedBook> books = apiClient.searchBooks(query);
        if (books.isEmpty()) {
            searchResponseLabel.setText("No books found.");
            updateResponse(searchResponseLabel, "No books found.", false);

        } else {
            searchResponseLabel.setText("Books found: " + books.size());
            updateResponse(searchResponseLabel, "Books found: " + books.size(), true);
            displayBooks(books);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken for HandleSearchAction: " + (endTime - startTime) + "ms");
    }

    /**
     * Display books in the TableView.
     *
     * @param books List of books to be displayed
     */
    public void displayBooks(List<DetailedBook> books) {
        booksTable.setItems(FXCollections.observableArrayList(books));
    }


    /**
     * Handle the action of submitting a new book by ISBN.
     */
    @FXML
    private void handleSubmitButtonAction() {
        String isbn = inputField.getText().trim();
        checkAndAddBook(isbn);
    }
    
    /**
     * Check if the book exists and add it to the database.
     *
     * @param isbn The ISBN of the book to be added
     */
    private void checkAndAddBook(String isbn) {
        if (isbn.isEmpty()) {
            updateResponse(responseLabel, "Please enter an ISBN.", false);
            return;
        }
        Document bookData = apiClient.fetchBookDetailsByISBN(isbn);
        if (bookData != null && bookData.getElementsByTagNameNS("*", "record").getLength() > 0) {
            bookDao.updateOrAddBook(isbn);
            responseLabel.setText("ISBN added/updated in the local database.");
            updateResponse(responseLabel, "ISBN added/updated in the local database.", true);
            loadBooks();
        } else {
            responseLabel.setText("ISBN not found in BNF.");
            updateResponse(responseLabel, "ISBN not found in BNF.", false);
        }
    }
    
    /**
     * Update the response label with the given message and style.
     *
     * @param label The label to be updated
     * @param message The message to be displayed
     * @param isSuccess Indicates if the operation was successful
     */
    private void updateResponse(Label label, String message, boolean isSuccess) {
        label.setText(message);
        label.setStyle("-fx-text-fill: " + (isSuccess ? "green" : "red") + ";");
    }
}
