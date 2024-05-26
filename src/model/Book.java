package model;

import api.BNFApiClient;

/**
 * Represents a book with its ISBN, stock, and loan count.
 */
public class Book {
    private BNFApiClient apiClient = new BNFApiClient();
    protected String isbn; // ISBN of the book
    protected int stock; // Stock of the book
    protected int loansCount; // Number of times the book has been loaned

    /**
     * Constructor to initialize a book with its ISBN and stock.
     *
     * @param isbn  The ISBN of the book.
     * @param stock The stock of the book.
     */
    public Book(String isbn, int stock) {
        this.isbn = isbn;
        this.stock = stock;
    }

    /**
     * Gets the ISBN of the book.
     *
     * @return The ISBN of the book.
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the ISBN of the book.
     *
     * @param isbn The new ISBN of the book.
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets the title of the book by fetching it from the BNF API.
     *
     * @return The title of the book.
     */
    public String getTitle() {
        return apiClient.fetchTitle(this.isbn);
    }

    /**
     * Gets the author of the book by fetching it from the BNF API.
     *
     * @return The author of the book.
     */
    public String getAuthor() {
        return apiClient.fetchAuthor(this.isbn);
    }

    /**
     * Sets the author of the book. This method actually sets the ISBN, which is likely a mistake.
     *
     * @param author The author of the book.
     */
    public void setAuthor(String author) {
        this.isbn = author; // This seems incorrect as it sets ISBN instead of author
    }

    /**
     * Gets the stock of the book.
     *
     * @return The stock of the book.
     */
    public int getStock() {
        return stock;
    }

    /**
     * Sets the stock of the book.
     *
     * @param stock The new stock of the book.
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Gets the loan count of the book.
     *
     * @return The number of times the book has been loaned.
     */
    public int getLoansCount() {
        return loansCount;
    }

    /**
     * Sets the loan count of the book.
     *
     * @param loansCount The new loan count of the book.
     */
    public void setLoansCount(int loansCount) {
        this.loansCount = loansCount;
    }

    /**
     * Returns the string representation of the book.
     *
     * @return The ISBN of the book.
     */
    @Override
    public String toString() {
        return isbn;
    }
}
