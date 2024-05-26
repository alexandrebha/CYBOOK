package model;

import java.util.Date;

/**
 * Represents a loan in the library system, containing information about the user, book, 
 * loan date, due date, and return status.
 */
public class Loan {
    private int id; // Unique identifier for the loan
    private User user; // User who borrowed the book
    private Book book; // Book that was borrowed
    private Date loanDate; // Date when the book was borrowed
    private Date dueDate; // Expected return date
    private int returned; // Return status: 0 for not returned, 1 for returned

    /**
     * Constructs a Loan object with the specified details.
     *
     * @param id       Unique identifier for the loan.
     * @param user     User who borrowed the book.
     * @param book     Book that was borrowed.
     * @param loanDate Date when the book was borrowed.
     * @param dueDate  Expected return date.
     * @param returned Return status: 0 for not returned, 1 for returned.
     */
    public Loan(int id, User user, Book book, Date loanDate, Date dueDate, int returned) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returned = returned;
    }

    /**
     * Gets the unique identifier for the loan.
     *
     * @return The unique identifier for the loan.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the loan.
     *
     * @param id The unique identifier for the loan.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the user who borrowed the book.
     *
     * @return The user who borrowed the book.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who borrowed the book.
     *
     * @param user The user who borrowed the book.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the book that was borrowed.
     *
     * @return The book that was borrowed.
     */
    public Book getBook() {
        return book;
    }

    /**
     * Sets the book that was borrowed.
     *
     * @param book The book that was borrowed.
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Gets the date when the book was borrowed.
     *
     * @return The date when the book was borrowed.
     */
    public Date getLoanDate() {
        return loanDate;
    }

    /**
     * Sets the date when the book was borrowed.
     *
     * @param loanDate The date when the book was borrowed.
     */
    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    /**
     * Gets the expected return date.
     *
     * @return The expected return date.
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Sets the expected return date.
     *
     * @param dueDate The expected return date.
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the return status of the book.
     *
     * @return The return status: 0 for not returned, 1 for returned.
     */
    public int getReturned() {
        return returned;
    }

    /**
     * Sets the return status of the book.
     *
     * @param returned The return status: 0 for not returned, 1 for returned.
     */
    public void setReturned(int returned) {
        this.returned = returned;
    }
}
