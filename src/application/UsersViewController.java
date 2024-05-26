package application;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import dao.UserDao;
import dao.LoanDao;
import model.Loan;
import model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Comparator;

/**
 * Controller class for managing user-related operations in the application.
 */
public class UsersViewController {
    @FXML private TextField lastNameField; // TextField for entering the user's last name
    @FXML private TextField firstNameField; // TextField for entering the user's first name
    @FXML private TextField emailField; // TextField for entering the user's email
    @FXML private TextField addressField; // TextField for entering the user's address
    @FXML private TextField phoneField; // TextField for entering the user's phone number
    @FXML private TextField modLastNameField; // TextField for modifying the user's last name
    @FXML private TextField modFirstNameField; // TextField for modifying the user's first name
    @FXML private TextField modEmailField; // TextField for modifying the user's email
    @FXML private TextField modAddressField; // TextField for modifying the user's address
    @FXML private TextField modPhoneField; // TextField for modifying the user's phone number
    @FXML private ComboBox<User> modUserComboBox; // ComboBox for selecting a user to modify
    @FXML private ComboBox<User> searchUserComboBox; // ComboBox for selecting a user to search for
    @FXML private Label userResponseLabel; // Label to display responses for user-related actions
    @FXML private Label updateResponseLabel; // Label to display responses for update actions
    @FXML private TextArea userInfoTextArea; // TextArea to display detailed user information

    private UserDao userDao = new UserDao();
    private LoanDao loanDao = new LoanDao();
    
    /**
     * Default constructor.
     */
    public UsersViewController() {
        // Default constructor
    }

    /**
     * Called to initialize a controller after its root element has been completely processed.
     * Loads the users into the ComboBoxes.
     */
    public void initialize() {
        loadUsers();
    }

    /**
     * Loads users from the database and populates the ComboBoxes.
     */
    private void loadUsers() {
        List<User> users = userDao.getAllUsers();
        users.sort(Comparator.comparing(User::getLastName)
                .thenComparing(User::getFirstName));

        ObservableList<User> userObservableList = FXCollections.observableArrayList(users);
        modUserComboBox.setItems(userObservableList);
        searchUserComboBox.setItems(userObservableList);

        if (!users.isEmpty()) {
            modUserComboBox.getSelectionModel().selectFirst();
            searchUserComboBox.getSelectionModel().selectFirst();
        }
    }

    /**
     * Handles the action of adding a new user.
     */
    @FXML
    private void handleAddUserAction() {
        String lastName = lastNameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        if (validateUserData(firstName, lastName, email, address, phone)) {
            User newUser = new User(0, lastName, firstName, email, address, phone);
            boolean isAddedSuccessfully = userDao.addUser(newUser);  // Supposes this method returns true if the user is successfully added
            if (isAddedSuccessfully) {
                updateResponseLabel(userResponseLabel, "User successfully added!", true);
                clearUserFields();
                loadUsers();
            } else {
                updateResponseLabel(userResponseLabel, "Error adding user.", false);
            }
        } else {
            updateResponseLabel(userResponseLabel, "Please fill in all fields correctly.", false);
        }
    }

    /**
     * Validates user data before adding or updating.
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param email the user's email
     * @param address the user's address
     * @param phone the user's phone number
     * @return true if all fields are valid, false otherwise
     */
    private boolean validateUserData(String firstName, String lastName, String email, String address, String phone) {
        return !(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || address.isEmpty() || phone.isEmpty());
    }

    /**
     * Handles the action of updating an existing user.
     */
    @FXML
    private void handleUpdateUser() {
        User selectedUser = modUserComboBox.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            selectedUser.setFirstName(modFirstNameField.getText());
            selectedUser.setLastName(modLastNameField.getText());
            selectedUser.setEmail(modEmailField.getText());
            selectedUser.setAddress(modAddressField.getText());
            selectedUser.setPhone(modPhoneField.getText());

            boolean isUpdatedSuccessfully = userDao.updateUser(selectedUser);  // Supposes this method returns true if the update is successful
            if (isUpdatedSuccessfully) {
                updateResponseLabel(updateResponseLabel, "Changes saved successfully.", true);
                loadUsers();
            } else {
                updateResponseLabel(updateResponseLabel, "Error saving changes.", false);
                loadUsers();
            }
        }
    }

    /**
     * Handles the action of searching for a user.
     */
    @FXML
    private void handleSearchUserAction() {
        User selectedUser = searchUserComboBox.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            displayUserInfo(selectedUser);
        } else {
            userInfoTextArea.setText("Please select a user.");
        }
    }

    /**
     * Formats the return status of a loan.
     *
     * @param returned the return status
     * @return "Returned" if the book is returned, "Not Returned" otherwise
     */
    private String formatReturnStatus(int returned) {
        return returned == 1 ? "Returned" : "Not Returned";
    }

    /**
     * Displays detailed information about a user.
     *
     * @param user the user to display information about
     */
    private void displayUserInfo(User user) {
        StringBuilder userInfo = new StringBuilder();
        userInfo.append(String.format("Name: %s %s\nEmail: %s\nAddress: %s\nPhone: %s\n", 
            user.getFirstName(), user.getLastName(), user.getEmail(), user.getAddress(), user.getPhone()));
        List<Loan> loans = loanDao.getLoansByUser(user.getId());
        if (!loans.isEmpty()) {
            userInfo.append("Loans:\n");
            for (Loan loan : loans) {
                userInfo.append(String.format("ISBN: %s, Book: %s %s Loan Date: %s, Due Date: %s, %s\n",
                    loan.getBook().getIsbn(), loan.getBook().getTitle(), loan.getBook().getAuthor(), 
                    loan.getLoanDate().toString(), loan.getDueDate().toString(), formatReturnStatus(loan.getReturned())));
            }
        } else {
            userInfo.append("No current loans.");
        }
        userInfoTextArea.setText(userInfo.toString());
    }

    /**
     * Updates the response label with a message.
     *
     * @param label the label to update
     * @param message the message to display
     * @param isSuccess true if the action was successful, false otherwise
     */
    private void updateResponseLabel(Label label, String message, boolean isSuccess) {
        label.setText(message);
        // Apply style based on success or failure
        if (isSuccess) {
            label.setStyle("-fx-text-fill: green;");  // Green if successful
        } else {
            label.setStyle("-fx-text-fill: red;");    // Red if failed
        }
    }

    /**
     * Clears all fields related to adding a new user.
     */
    private void clearUserFields() {
        lastNameField.clear();
        firstNameField.clear();
        emailField.clear();
        addressField.clear();
        phoneField.clear();
    }
}
