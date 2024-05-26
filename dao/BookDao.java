package dao;

import model.Book;
import util.databaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDao {

    /**
     * Method to retrieve all books from the database.
     * @return a list of all books.
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT isbn, stock FROM books";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                books.add(new Book(
                    rs.getString("isbn"),
                    rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching books: " + e.getMessage());
        }
        return books;
    }

    /**
     * Method to update the stock of an existing book or add a new book if it doesn't exist.
     * @param isbn the ISBN of the book to be updated or added.
     */
    public void updateOrAddBook(String isbn) {
        String sqlCheck = "SELECT stock FROM books WHERE isbn = ?";
        String sqlUpdate = "UPDATE books SET stock = stock + 1 WHERE isbn = ?";
        String sqlInsert = "INSERT INTO books (isbn, stock) VALUES (?, 1)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(sqlCheck);
             PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate);
             PreparedStatement insertStmt = conn.prepareStatement(sqlInsert)) {
            
            checkStmt.setString(1, isbn);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Book exists, update the stock
                updateStmt.setString(1, isbn);
                updateStmt.executeUpdate();
            } else {
                // Book doesn't exist, insert a new entry
                insertStmt.setString(1, isbn);
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error processing book update or add: " + e.getMessage());
        }
    }

    /**
     * Method to find a book by its ISBN.
     * @param isbn the ISBN of the book to be found.
     * @return the Book object if found, null otherwise.
     */
    public Book findBookByISBN(String isbn) {
        String sql = "SELECT isbn, stock FROM books WHERE isbn = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Book(
                        rs.getString("isbn"),
                        rs.getInt("stock")
                    );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching book by ISBN: " + e.getMessage());
        }
        return null;
    }

    /**
     * Method to get the count of loans for a book by its ISBN in the last 30 days.
     * @param isbn the ISBN of the book.
     * @return the count of loans in the last 30 days.
     */
    public int getLoansCountForISBN(String isbn) {
        String sql = "SELECT COUNT(*) AS count FROM loans WHERE book_isbn = ? AND date_loaned >= CURDATE() - INTERVAL 30 DAY";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching loans count by ISBN: " + e.getMessage());
        }
        return 0;
    }
}
