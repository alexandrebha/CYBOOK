package application;

import dao.LoanDao;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * Controller class for the main view of the application.
 */
public class MainViewController {

    @FXML private Label overdueBooksCountLabel;
    @FXML private StackPane contentArea;
    @FXML private LoanDao loanDao = new LoanDao();
    
    /**
     * Constructeur par défaut.
     */
    public MainViewController() {
        // Default constructor
    }

    /**
     * Shows the Loans view.
     */
    public void showLoansView() {
        loadView("LoansView.fxml");
    }

    /**
     * Shows the Users view.
     */
    public void showUsersView() {
        loadView("UsersView.fxml");
    }

    /**
     * Shows the Books view.
     */
    public void showBooksView() {
        loadView("BooksView.fxml");
    }

    /**
     * Initialize method, called automatically after the FXML file is loaded.
     * Updates the overdue books count.
     */
    public void initialize() {
        updateOverdueBooksCount();
    }

    /**
     * Loads a view and sets it to the content area.
     * 
     * @param fxml the FXML file to load
     */
    private void loadView(String fxml) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxml));
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the label with the count of overdue books.
     * Checks if the label is initialized before updating.
     */
    public void updateOverdueBooksCount() {
        // Check if the label is initialized
        if (overdueBooksCountLabel == null) {
            System.out.println("The 'overdueBooksCountLabel' is not initialized.");
            return;  // Exit the method if the label is not available
        }

        // Get the number of overdue loans
        int overdueBooksCount = loanDao.countOverdueLoans();
        if(overdueBooksCount != 0) {
            overdueBooksCountLabel.setText(String.valueOf(overdueBooksCount) + " ⚠");
        }
    }

    /**
     * Handles the Quit action.
     * Exits the application.
     */
    @FXML
    private void handleQuit() {
        System.exit(0);
    }
}
