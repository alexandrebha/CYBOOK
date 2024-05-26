package dao;

import model.Loan;
import model.User;
import model.Book;
import util.databaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import api.BNFApiClient;

public class LoanDao {
    private BNFApiClient apiClient = new BNFApiClient();

    /**
     * Method to retrieve all loans from the database.
     * @return a list of all loans.
     */
    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.id, l.loanDate, l.dueDate, u.*, b.* FROM loans l " +
                     "JOIN users u ON l.userId = u.id " +
                     "JOIN books b ON l.bookId = b.id";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = new User(
                    rs.getInt("u.id"),
                    rs.getString("u.lastName"),
                    rs.getString("u.firstName"),
                    rs.getString("u.email"),
                    rs.getString("address"),
                    rs.getString("phone")
                );
                Book book = new Book(
                        rs.getString("isbn"),
                        rs.getInt("stock")
                    );
                Loan loan = new Loan(
                    rs.getInt("l.id"),
                    user,
                    book,
                    rs.getDate("l.loanDate"),
                    rs.getDate("l.dueDate"),
                    rs.getInt("returned")
                );
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching loans: " + e.getMessage());
        }
        return loans;
    }

    /**
     * Method to add a new loan to the database.
     * @param loan the loan to be added.
     */
    public void addLoan(Loan loan) {
        String sqlLoan = "INSERT INTO loans (user_id, book_isbn, date_loaned, due_date) VALUES (?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE books SET stock = stock - 1 WHERE isbn = ? AND stock > 0";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmtLoan = conn.prepareStatement(sqlLoan);
             PreparedStatement pstmtUpdateStock = conn.prepareStatement(sqlUpdateStock)) {

            // Add the loan
            pstmtLoan.setInt(1, loan.getUser().getId());
            pstmtLoan.setString(2, loan.getBook().getIsbn());
            pstmtLoan.setDate(3, new java.sql.Date(loan.getLoanDate().getTime()));
            pstmtLoan.setDate(4, new java.sql.Date(loan.getDueDate().getTime()));
            pstmtLoan.executeUpdate();

            // Decrement the book stock
            pstmtUpdateStock.setString(1, loan.getBook().getIsbn());
            int affectedRows = pstmtUpdateStock.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("No stock to decrement for book with ID: " + loan.getBook().getIsbn());
            }
        } catch (SQLException e) {
            System.err.println("Error adding loan and updating stock: " + e.getMessage());
        }
    }

    /**
     * Method to delete a loan from the database.
     * @param loanId the ID of the loan to be deleted.
     */
    public void deleteLoan(int loanId) {
        String sql = "DELETE FROM loans WHERE id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, loanId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting loan: " + e.getMessage());
        }
    }

    /**
     * Method to get the list of books borrowed by a specific user.
     * @param userId the ID of the user.
     * @return a list of books borrowed by the user.
     */
    public List<Book> getBorrowedBooksByUser(int userId) {
        List<Book> borrowedBooks = new ArrayList<>();
        String sql = "SELECT b.isbn, b.stock FROM loans l JOIN books b ON l.book_isbn = b.isbn WHERE l.user_id = ? AND l.returned = 0";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                borrowedBooks.add(new Book(rs.getString("isbn"), rs.getInt("stock")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching books borrowed by user: " + e.getMessage());
        }
        return borrowedBooks;
    }

    /**
     * Method to get the list of users with current loans.
     * @return a list of users with current loans.
     */
    public List<User> getUsersWithCurrentLoans() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT DISTINCT u.* FROM users u JOIN loans l ON u.id = l.user_id WHERE l.returned = 0";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("lastName"), rs.getString("firstName"), rs.getString("email"), rs.getString("address"), rs.getString("phone")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users with current loans: " + e.getMessage());
        }
        return users;
    }

    /**
     * Method to get the list of late loans.
     * @return a list of late loans.
     */
    public List<Loan> getLateLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.id as loanId, l.date_loaned, l.due_date, l.returned, " +
                     "u.id as userId, u.firstName, u.lastName, u.email, u.address, u.phone, " +
                     "b.isbn as bookIsbn, b.stock " +
                     "FROM loans l " +
                     "JOIN users u ON l.user_id = u.id " +
                     "JOIN books b ON l.book_isbn = b.isbn " +
                     "WHERE l.due_date < CURDATE() AND l.returned = 0";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = new User(
                    rs.getInt("userId"),
                    rs.getString("lastName"),
                    rs.getString("firstName"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("phone")
                );
                Book book = new Book(
                    rs.getString("bookIsbn"),
                    rs.getInt("stock")
                );
                Loan loan = new Loan(
                    rs.getInt("loanId"),
                    user,
                    book,
                    rs.getDate("date_loaned"),
                    rs.getDate("due_date"),
                    rs.getInt("returned")
                );
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching late loans: " + e.getMessage());
        }
        return loans;
    }

    /**
     * Method to count the number of active loans for a specific user.
     * @param userId the ID of the user.
     * @return the count of active loans.
     */
    public int countActiveLoansByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM loans WHERE user_id = ? AND returned = 0";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // Return the count of active loans
            }
        } catch (SQLException e) {
            System.err.println("Error counting active loans: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Method to count the number of overdue loans.
     * @return the count of overdue loans.
     */
    public int countOverdueLoans() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM loans WHERE due_date < CURRENT_DATE AND returned=0";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Method to return a loan by updating the returned status.
     * @param userId the ID of the user.
     * @param isbn the ISBN of the book.
     * @return true if the loan was successfully returned, false otherwise.
     */
    public boolean returnLoan(int userId, String isbn) {
        String sqlReturnLoan = "UPDATE loans SET returned = 1 "
                             + "WHERE user_id = ? AND book_isbn = ? AND returned = 0 "
                             + "ORDER BY due_date DESC "
                             + "LIMIT 1";

        String sqlUpdateStock = "UPDATE books SET stock = stock + 1 WHERE isbn = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmtReturnLoan = conn.prepareStatement(sqlReturnLoan);
             PreparedStatement pstmtUpdateStock = conn.prepareStatement(sqlUpdateStock)) {

            // Marquer le prêt comme retourné
            pstmtReturnLoan.setInt(1, userId);
            pstmtReturnLoan.setString(2, isbn);
            int affectedRowsLoan = pstmtReturnLoan.executeUpdate();

            if (affectedRowsLoan > 0) {
                // Incrémenter le stock du livre
                pstmtUpdateStock.setString(1, isbn);
                int affectedRowsStock = pstmtUpdateStock.executeUpdate();
                if (affectedRowsStock > 0) {
                    return true;
                } else {
                    System.err.println("Erreur lors de l'incrémentation du stock pour le livre avec l'ISBN: " + isbn);
                    return false;
                }
            } else {
                System.err.println("Aucun prêt trouvé à retourner pour l'utilisateur avec ID: " + userId + " et le livre avec l'ISBN: " + isbn);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du retour du livre et de la mise à jour du stock: " + e.getMessage());
            return false;
        }
    }

    /**
     * Method to fetch the top borrowed books from the last 30 days.
     * @return a list of top borrowed books.
     */
    public List<Book> fetchTopBooks() {
        List<Book> topBooks = new ArrayList<>();
        String sql = "SELECT book_isbn, COUNT(*) AS count FROM loans "
                   + "WHERE date_loaned >= CURDATE() - INTERVAL 30 DAY "
                   + "GROUP BY book_isbn ORDER BY count DESC LIMIT 3";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String isbn = rs.getString("book_isbn");
                int stock = fetchStockForISBN(isbn); // Ensure this method is properly implemented
                int loansCount = rs.getInt("count");
                Book book = new Book(isbn, stock);
                book.setLoansCount(loansCount); // Set the loan count
                if (book.getTitle() != null && book.getAuthor() != null) {
                    topBooks.add(book);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching top borrowed books from the last 30 days: " + e.getMessage());
        }
        return topBooks;
    }

    /**
     * Fetch stock from the database for a given ISBN.
     * @param isbn the ISBN of the book.
     * @return the stock of the book.
     */
    private int fetchStockForISBN(String isbn) {
        String sql = "SELECT stock FROM books WHERE isbn = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("stock");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching stock for ISBN: " + isbn + " - " + e.getMessage());
        }
        return 0; // Return default stock as 0 if not found or error
    }

    /**
     * Method to get the list of loans by a specific user.
     * @param userId the ID of the user.
     * @return a list of loans by the user.
     */
    public List<Loan> getLoansByUser(int userId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.id, l.book_isbn, l.date_loaned, l.due_date, l.returned, b.isbn, b.stock, u.lastName, u.firstName, u.email, u.address, u.phone " +
                     "FROM loans l " +
                     "JOIN books b ON l.book_isbn = b.isbn " +
                     "JOIN users u ON l.user_id = u.id " +
                     "WHERE l.user_id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                User user = new User(userId, rs.getString("lastName"), rs.getString("firstName"), rs.getString("email"), rs.getString("address"),rs.getString("phone"));
                Book book = new Book(rs.getString("isbn"), rs.getInt("stock"));
                int returned = rs.getInt("returned");
                Loan loan = new Loan(rs.getInt("id"), user, book, rs.getDate("date_loaned"), rs.getDate("due_date"), returned);
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching loans for user: " + e.getMessage());
        }
        return loans;
    }
}
