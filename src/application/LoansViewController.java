package application;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import api.BNFApiClient;
import dao.BookDao;
import dao.LoanDao;
import dao.UserDao;
import model.Book;
import model.Loan;
import model.User;

import java.util.Date;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LoansViewController {
    @FXML private ComboBox<User> userComboBox; // ComboBox for selecting a user for loaning a book
    @FXML private ComboBox<Book> bookComboBox; // ComboBox for selecting a book to loan
    @FXML private ComboBox<User> returnUserComboBox; // ComboBox for selecting a user to return a book
    @FXML private ComboBox<Book> returnBookComboBox; // ComboBox for selecting a book to return
    @FXML private ListView<String> lateLoansListView; // ListView to display late loans
    @FXML private Label loanResponseLabel; // Label for displaying loan response messages
    @FXML private Label returnResponseLabel; // Label for displaying return response messages
    @FXML private Label overdueBooksCountLabel; // Label for displaying count of overdue books

    private BNFApiClient apiClient = new BNFApiClient();
    private BookDao bookDao = new BookDao();
    private UserDao userDao = new UserDao();
    private LoanDao loanDao = new LoanDao();

    /**
     * Initialize method to load initial data and setup listeners.
     */
    public void initialize() {
        loadUsers();
        loadBooks();
        setupComboBoxListeners();
        updateOverdueBooksCount();
    }

    /**
     * Setup listeners for ComboBox selections.
     */
    private void setupComboBoxListeners() {
        returnUserComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadBooksBorrowedBy(newSelection);
            }
        });
    }

    /**
     * Load books borrowed by a specific user.
     *
     * @param user The user whose borrowed books are to be loaded
     */
    private void loadBooksBorrowedBy(User user) {
        List<Book> borrowedBooks = loanDao.getBorrowedBooksByUser(user.getId());
        returnBookComboBox.setItems(FXCollections.observableArrayList(borrowedBooks));
    }

    /**
     * Load users from the database.
     */
    private void loadUsers() {
        List<User> users = userDao.getAllUsers();
        users.sort((u1, u2) -> {
            int lastNameCompare = u1.getLastName().compareToIgnoreCase(u2.getLastName());
            if (lastNameCompare == 0) {
                return u1.getFirstName().compareToIgnoreCase(u2.getFirstName());
            }
            return lastNameCompare;
        });
        ObservableList<User> userObservableList = FXCollections.observableArrayList(users);
        userComboBox.setItems(userObservableList);
        returnUserComboBox.setItems(userObservableList);
        if (!users.isEmpty()) {
            userComboBox.getSelectionModel().selectLast();
            returnUserComboBox.getSelectionModel().selectLast();
        }
    }

    /**
     * Load books from the database.
     */
    private void loadBooks() {
        List<Book> books = bookDao.getAllBooks();
        books = books.stream()
                     .filter(book -> book.getStock() > 0)
                     .sorted(Comparator.comparing(Book::getIsbn))
                     .collect(Collectors.toList());
        bookComboBox.setItems(FXCollections.observableArrayList(books));
        if (!books.isEmpty()) {
            bookComboBox.getSelectionModel().selectFirst();
        }
    }

    /**
     * Handle the action of loaning a book to a user.
     */
    @FXML
    private void handleLoanAction() {
        User selectedUser = userComboBox.getSelectionModel().getSelectedItem();
        Book selectedBook = bookComboBox.getSelectionModel().getSelectedItem();

        // Check that both user and book are selected
        if (selectedUser == null || selectedBook == null) {
            updateResponseLabel(loanResponseLabel, "Please select both a user and a book.", false);
            return;
        }

        // Check the number of active loans for the user
        int activeLoansCount = loanDao.countActiveLoansByUser(selectedUser.getId());
        if (activeLoansCount >= 3) {
            updateResponseLabel(loanResponseLabel, "Loan failed: The user has already borrowed three books.", false);
            return;
        }

        // Use a new thread to avoid blocking the UI during the HTTP request
        new Thread(() -> {
            String title = apiClient.fetchTitle(selectedBook.getIsbn());
            // Update the UI on the JavaFX Application Thread
            Platform.runLater(() -> {
                if (!"Unknow Title".equals(title)) {
                    Loan newLoan = new Loan(0, selectedUser, selectedBook, new java.util.Date(), calculateDueDate(), 0);
                    loanDao.addLoan(newLoan);
                    updateResponseLabel(loanResponseLabel, "Loan made for " + selectedUser.getFirstName() + " " + selectedUser.getLastName() + " of the book \"" + title + "\"", true);
                    loadBooks(); // Reload the book list to reflect stock changes
                    loadBooksBorrowedBy(selectedUser); // Reload the list of books borrowed by the user
                    updateOverdueBooksCount(); // Update the late book counter
                } else {
                    updateResponseLabel(loanResponseLabel, "Loan failed: The book title is unknown.", false);
                }
            });
        }).start();
    }

    /**
     * Handle the action of returning a book.
     */
    @FXML
    private void handleReturnAction() {
        User selectedUser = returnUserComboBox.getSelectionModel().getSelectedItem();
        Book selectedBook = returnBookComboBox.getSelectionModel().getSelectedItem();

        if (selectedUser != null && selectedBook != null) {
            boolean returned = loanDao.returnLoan(selectedUser.getId(), selectedBook.getIsbn());
            if (returned) {
                returnResponseLabel.setText("The book was successfully returned.");
                loadBooks();  // Refresh the book list to update the stock
                loadUsers();  // Refresh users if necessary
            }

            boolean isReturnSuccessfully = loanDao.returnLoan(selectedUser.getId(), selectedBook.getIsbn());
            if (isReturnSuccessfully) {
                updateResponseLabel(returnResponseLabel, "The book was successfully returned.", true);
            }

        } else {
            returnResponseLabel.setText("Please select a user and a book.");
            updateResponseLabel(returnResponseLabel, "Please select a user and a book.", false);
        }
        updateOverdueBooksCount();
    }

    /**
     * Handle the action of showing late loans.
     */
    @FXML
    private void handleShowLateLoans() {
        List<Loan> lateLoans = loanDao.getLateLoans();
        ObservableList<String> formattedLoans = FXCollections.observableArrayList();

        // Add column titles
        formattedLoans.add(String.format("%-10s %-25s %-15s %-50s %-15s %-15s", "LOAN ID", "Borrower", "ISBN", "Title", "Return Date", "Days Late"));
        // Add data
        lateLoans.forEach(loan -> formattedLoans.add(String.format(
                "%-10d %-25s %-15s %-50s %-15s %-15d",
                loan.getId(),
                loan.getUser().getFirstName() + " " + loan.getUser().getLastName(),
                loan.getBook().getIsbn(),
                loan.getBook().getTitle(),
                loan.getDueDate().toString(),
                calculateDaysLate(loan.getDueDate()))
        ));
        lateLoansListView.setItems(formattedLoans);
    }

    /**
     * Calculate the due date for a loan.
     *
     * @return The due date for the loan
     */
    private java.util.Date calculateDueDate() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.WEEK_OF_YEAR, 2);
        return calendar.getTime();
    }

    /**
     * Calculate the number of days a loan is late.
     *
     * @param dueDate The due date of the loan
     * @return The number of days the loan is late
     */
    private int calculateDaysLate(Date dueDate) {
        java.util.Date currentDate = new java.util.Date();
        long diffInMillies = currentDate.getTime() - dueDate.getTime();
        long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);
        return (int) diffInDays;
    }

    /**
     * Update the overdue books count.
     */
    public void updateOverdueBooksCount() {
        // First, check if the label is initialized
        if (overdueBooksCountLabel == null) {
            System.out.println("The 'overdueBooksCountLabel' is not initialized.");
            return;  // Exit the method if the label is not available
        }

        int overdueBooksCount = loanDao.countOverdueLoans(); // Get the number of overdue loans
        overdueBooksCountLabel.setText(String.valueOf(overdueBooksCount) + " book(s) overdue");
    }

    /**
     * Update the response label with a message and style.
     *
     * @param label The label to be updated
     * @param message The message to be displayed
     * @param isSuccess Indicates if the operation was successful
     */
    private void updateResponseLabel(Label label, String message, boolean isSuccess) {
        label.setText(message);
        // Apply style based on the result
        if (isSuccess) {
            label.setStyle("-fx-text-fill: green;");  // Green if successful
        } else {
            label.setStyle("-fx-text-fill: red;");    // Red if failed
        }
    }
}
