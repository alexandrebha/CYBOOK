package model;

/**
 * Represents a detailed book with additional information such as title, author,
 * publication date, edition, collection, and availability.
 */
public class DetailedBook extends Book {

    private String title; // Title of the book
    private String author; // Author of the book
    private String publicationDate; // Publication date of the book
    private String edition; // Edition of the book
    private String collection; // Collection the book belongs to
    private String availability; // Availability status of the book

    /**
     * Constructor to initialize a detailed book with its attributes.
     *
     * @param isbn            The ISBN of the book.
     * @param title           The title of the book.
     * @param author          The author of the book.
     * @param publicationDate The publication date of the book.
     * @param edition         The edition of the book.
     * @param collection      The collection the book belongs to.
     * @param stock           The stock of the book.
     * @param availability    The availability status of the book.
     */
    public DetailedBook(String isbn, String title, String author, String publicationDate, String edition, String collection, int stock, String availability) {
        super(isbn, stock);
        this.title = title;
        this.author = author;
        this.publicationDate = publicationDate;
        this.edition = edition;
        this.collection = collection;
        this.availability = availability;
    }

    /**
     * Gets the collection the book belongs to.
     *
     * @return The collection of the book.
     */
    public String getCollection() {
        return collection;
    }

    /**
     * Sets the collection the book belongs to.
     *
     * @param collection The new collection of the book.
     */
    public void setCollection(String collection) {
        this.collection = collection;
    }

    /**
     * Gets the availability status of the book.
     *
     * @return The availability status of the book.
     */
    public String getAvailability() {
        return availability;
    }

    /**
     * Sets the availability status of the book.
     *
     * @param availability The new availability status of the book.
     */
    public void setAvailability(String availability) {
        this.availability = availability;
    }

    /**
     * Gets the edition of the book.
     *
     * @return The edition of the book.
     */
    public String getEdition() {
        return edition;
    }

    /**
     * Sets the edition of the book.
     *
     * @param edition The new edition of the book.
     */
    public void setEdition(String edition) {
        this.edition = edition;
    }

    /**
     * Gets the title of the book.
     *
     * @return The title of the book.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the book.
     *
     * @param title The new title of the book.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the author of the book.
     *
     * @return The author of the book.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of the book.
     *
     * @param author The new author of the book.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Gets the publication date of the book.
     *
     * @return The publication date of the book.
     */
    public String getPublicationDate() {
        return publicationDate;
    }

    /**
     * Sets the publication date of the book.
     *
     * @param publicationDate The new publication date of the book.
     */
    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }
}
