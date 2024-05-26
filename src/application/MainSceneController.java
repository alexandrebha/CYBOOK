/*package application;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import dao.BookDao;
import dao.UserDao;
import dao.LoanDao;
import api.BNFApiClient;
import org.w3c.dom.Document;
import util.UserValidator;
import model.Book;
import model.DetailedBook;
import model.User;
import model.Loan;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

public class MainSceneController {
    @FXML private TextField inputField;
    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField emailField;
    @FXML private TextField modLastNameField; // TextField pour modifier le nom de l'utilisateur
    @FXML private TextField modFirstNameField; // TextField pour modifier le prénom
    @FXML private TextField modEmailField; // TextField pour modifier l'email
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField dateField;
    @FXML private ComboBox<User> userComboBox;
    @FXML private ComboBox<Book> bookComboBox;
    @FXML private ComboBox<User> modUserComboBox;
    @FXML private ComboBox<User> returnUserComboBox;
    @FXML private ComboBox<Book> returnBookComboBox;
    @FXML private Label responseLabel;
    @FXML private Label updateResponseLabel;
    @FXML private Label userResponseLabel;
    @FXML private Label loanResponseLabel;
    @FXML private Label searchResponseLabel;
    @FXML private Label returnResponseLabel;
    @FXML private Label availabilityLabel;
    @FXML private ComboBox<String> topBooksComboBox;
    @FXML private TextArea lateLoansTextArea;
    @FXML private Label overdueBooksCountLabel;
    @FXML private ComboBox<User> searchUserComboBox;
    @FXML private TextArea userInfoTextArea;


    private BNFApiClient apiClient = new BNFApiClient();
    private BookDao bookDao = new BookDao();
    private UserDao userDao = new UserDao();
    private LoanDao loanDao = new LoanDao();
    
    
    public void initialize() {
        loadUsers();
        loadBooks();
        setupComboBoxListeners();
        loadTopBooks();
        updateOverdueBooksCount();
        setupUserSearch();
    }

    private void setupComboBoxListeners() {
        returnUserComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadBooksBorrowedBy(newSelection);
            }
        });
    }
    
    private void loadBooksBorrowedBy(User user) {
        List<Book> borrowedBooks = loanDao.getBorrowedBooksByUser(user.getId());
        returnBookComboBox.setItems(FXCollections.observableArrayList(borrowedBooks));
    }
    
    

    private void loadReturnComboBoxes() {
        returnUserComboBox.setItems(userComboBox.getItems());  // Réutilise les items si les mêmes utilisateurs sont applicables
        returnBookComboBox.setItems(bookComboBox.getItems());  // Réutilise les items si les mêmes livres sont applicables
    }

    private void handleUserSelection(User selectedUser) {
        if (selectedUser != null) {
            modFirstNameField.setText(selectedUser.getFirstName());
            modLastNameField.setText(selectedUser.getLastName());
            modEmailField.setText(selectedUser.getEmail());
        }
    }
    
    @FXML
    private void handleSearchUserAction() {
        User selectedUser = searchUserComboBox.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            displayUserInfo(selectedUser);
        } else {
            userInfoTextArea.setText("Veuillez sélectionner un utilisateur.");
        }
    }

   
    public void updateOverdueBooksCount() {
        int overdueBooksCount = loanDao.countOverdueLoans(); // Adapte cette ligne selon ton implémentation
        if( this.overdueBooksCountLabel==null){
     	   overdueBooksCountLabel.setText("0 en livre en retard");
        }
        else {
        overdueBooksCountLabel.setText(String.valueOf(overdueBooksCount)+" livre en retard");
        }
    }

    
    private void loadTopBooks() {
        List<Book> topBooks = loanDao.fetchTopBooks();
        ObservableList<String> bookTitles = FXCollections.observableArrayList();
        for (Book book : topBooks) {
            String title = book.getTitle();  // Assure-toi que getTitle fait bien ce qu'il faut
            if (title != null && !title.isEmpty()) {
                bookTitles.add(title);
            }
        }
        topBooksComboBox.setItems(bookTitles);
    }


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
        modUserComboBox.setItems(userObservableList);  // Assurez-vous que cette ComboBox est aussi mise à jour
        returnUserComboBox.setItems(userObservableList);  // Réutilisation pour la section de retour
        searchUserComboBox.setItems(userObservableList);
        if (!users.isEmpty()) {
            userComboBox.getSelectionModel().selectLast();
            modUserComboBox.getSelectionModel();  // Sélectionnez un utilisateur par défaut si nécessaire pour modification
            returnUserComboBox.getSelectionModel();  // Sélectionnez un utilisateur par défaut pour les retours
            searchUserComboBox.getSelectionModel();
        }
    }
    
    private void setupUserSearch() {
        searchUserComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayUserInfo(newSelection);
            }
        });
    }
    
    private String formatReturnStatus(int returned) {
        return returned == 1 ? "Retourné" : "Non Retourné";
    }

    

    private void displayUserInfo(User user) {
        StringBuilder userInfo = new StringBuilder();
        userInfo.append(String.format("Nom: %s %s\nEmail: %s\n", user.getFirstName(), user.getLastName(), user.getEmail()));
        List<Loan> loans = loanDao.getLoansByUser(user.getId());
        if (!loans.isEmpty()) {
            userInfo.append("Emprunts:\n");
            for (Loan loan : loans) {
                userInfo.append(String.format("ISBN: %s, Livre: %s %s Date Empruntée: %s, Date Due: %s, %s\n",
                    loan.getBook().getIsbn(),loan.getBook().getTitle(), loan.getBook().getAuthor(), loan.getLoanDate().toString(), loan.getDueDate().toString(), formatReturnStatus(loan.getReturned())));
            }
        } else {
            userInfo.append("Aucun emprunt en cours.");
        }
        userInfoTextArea.setText(userInfo.toString());
    }



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
    
    
    private void handleUserSelection() {
        User selectedUser = userComboBox.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            modFirstNameField.setText(selectedUser.getFirstName());
            modLastNameField.setText(selectedUser.getLastName());
            modEmailField.setText(selectedUser.getEmail());
        }
    }

    @FXML
    private void handleSubmitButtonAction() {
        String isbn = inputField.getText().trim();
        checkAndAddBook(isbn);
    }

    private void checkAndAddBook(String isbn) {
        Document bookData = apiClient.fetchBookDetailsByISBN(isbn);
        if (bookData != null && bookData.getElementsByTagNameNS("*", "record").getLength() > 0) {
            bookDao.updateOrAddBook(isbn);
            responseLabel.setText("ISBN ajouté/mis à jour dans la base de données locale.");
            loadBooks();
        } else {
            responseLabel.setText("ISBN non trouvé dans la BNF.");
        }
    }

    @FXML
    private void handleAddUserAction() {
        String lastName = lastNameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String email = emailField.getText().trim();

        if (!UserValidator.isValidName(firstName) || !UserValidator.isValidName(lastName) || !UserValidator.isValidEmail(email)) {
            userResponseLabel.setText("Erreur: Nom, prénom ou email invalide.");
            return;
        }

        User newUser = new User(0, lastName, firstName, email);
        if (userDao.addUser(newUser)) {
            userResponseLabel.setText("Utilisateur ajouté: " + firstName + " " + lastName);
            clearUserFields();
            loadUsers();
        } else {
            userResponseLabel.setText("Erreur lors de l'ajout de l'utilisateur.");
        }
    }

    @FXML
    private void handleSearchAction() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String date = dateField.getText().trim();

        // Construire la requête de recherche en fonction des champs remplis
        StringBuilder queryBuilder = new StringBuilder();
        if (!title.isEmpty()) {
            queryBuilder.append("(bib.title all \"").append(title).append("\")");
        }
        if (!author.isEmpty()) {
            if (queryBuilder.length() > 0) queryBuilder.append(" and ");
            queryBuilder.append("(bib.author all \"").append(author).append("\")");
        }
        if (!date.isEmpty()) {
            if (queryBuilder.length() > 0) queryBuilder.append(" and ");
            queryBuilder.append("(bib.date all \"").append(date).append("\")");
        }

        // Vérifiez si au moins un champ est rempli
        if (queryBuilder.length() > 0) {
            List<DetailedBook> books = apiClient.searchBooks(queryBuilder.toString());
            if (books.isEmpty()) {
                searchResponseLabel.setText("Aucun livre trouvé.");
                availabilityLabel.setText("");
            } else {
                // Affichage des résultats (vous pourriez vouloir utiliser un TableView ou un autre composant visuel pour les afficher)
                searchResponseLabel.setText("Livres trouvés: " + books.size());
                availabilityLabel.setText("Voir les détails dans le tableau des résultats.");  // Placeholder pour affichage des résultats
                // Ici, vous devriez remplir votre tableau ou liste des livres trouvés
            }
        } else {
            searchResponseLabel.setText("Veuillez remplir au moins un des champs de recherche.");
            availabilityLabel.setText("");
        }
    }


    private void checkBookAvailability(String isbn) {
        Book book = bookDao.findBookByISBN(isbn);
        if (book != null && book.getStock() > 0) {
            availabilityLabel.setText("Livre " + apiClient.fetchTitle(isbn) + " disponible.");
        } else {
            availabilityLabel.setText("Livre non disponible.");
        }
    }

    @FXML
    private void handleLoanAction() {
        User selectedUser = userComboBox.getSelectionModel().getSelectedItem();
        Book selectedBook = bookComboBox.getSelectionModel().getSelectedItem();

        if (selectedUser == null || selectedBook == null) {
            loanResponseLabel.setText("Veuillez sélectionner à la fois un utilisateur et un livre.");
            return;
        }

        // Vérifier le nombre d'emprunts actifs de l'utilisateur
        int activeLoansCount = loanDao.countActiveLoansByUser(selectedUser.getId());
        if (activeLoansCount >= 3) {
            loanResponseLabel.setText("Échec de l'emprunt: l'utilisateur a déjà emprunté trois livres.");
            return;
        }

        // Utilisation d'une nouvelle thread pour éviter de bloquer l'interface utilisateur lors de la requête HTTP
        new Thread(() -> {
            String title = apiClient.fetchTitle(selectedBook.getIsbn());
            // Mise à jour de l'interface utilisateur doit être faite sur le JavaFX Application Thread
            Platform.runLater(() -> {
                if (!"Titre inconnu".equals(title)) {
                    Loan newLoan = new Loan(0, selectedUser, selectedBook, new java.util.Date(), calculateDueDate(),0);
                    loanDao.addLoan(newLoan);
                    loanResponseLabel.setText("Emprunt réalisé pour " + selectedUser.getFirstName() + " " + selectedUser.getLastName() + " du livre \"" + title + "\"");
                    loadBooks(); // Recharger la liste des livres pour refléter les changements de stock
                } else {
                    loanResponseLabel.setText("Échec de l'emprunt: le titre du livre est inconnu.");
                }
            });
        }).start();
    }

    
    public void handleUpdateUser() {
        User selectedUser = modUserComboBox.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            selectedUser.setFirstName(modFirstNameField.getText());
            selectedUser.setLastName(modLastNameField.getText());
            selectedUser.setEmail(modEmailField.getText());
            boolean updated = userDao.updateUser(selectedUser);
            if (updated) {
                updateResponseLabel.setText("Mise à jour réussie.");
                loadUsers(); // Refresh user list
            } else {
                updateResponseLabel.setText("Erreur lors de la mise à jour.");
            }
        }
    }
    
    @FXML
    private void handleShowLateLoans() {
        List<Loan> lateLoans = loanDao.getLateLoans();
        StringBuilder displayText = new StringBuilder();
        if (lateLoans.isEmpty()) {
            displayText.append("Il n'y a pas d'emprunts en retard.");
        } else {
            for (Loan loan : lateLoans) {
                displayText.append(String.format("ID: %d, Emprunteur: %s %s, Livre ISBN: %s Titre : %s, Date due: %s\n",
                        loan.getId(), loan.getUser().getFirstName(), loan.getUser().getLastName(),
                        loan.getBook().getIsbn(), loan.getBook().getTitle(), loan.getDueDate().toString()));
            }
        }
        lateLoansTextArea.setText(displayText.toString());
    }
    
    @FXML
    private void handleReturnAction() {
        User selectedUser = returnUserComboBox.getSelectionModel().getSelectedItem();
        Book selectedBook = returnBookComboBox.getSelectionModel().getSelectedItem();

        if (selectedUser != null && selectedBook != null) {
            boolean returned = loanDao.returnLoan(selectedUser.getId(), selectedBook.getIsbn());
            if (returned) {
                returnResponseLabel.setText("Le livre a été rendu avec succès.");
                loadBooks();  // Actualiser la liste des livres pour mettre à jour le stock
                loadUsers();  // Vous pouvez également choisir de rafraîchir les utilisateurs si nécessaire
            } else {
                returnResponseLabel.setText("Erreur lors de la tentative de retour du livre.");
            }
        } else {
            returnResponseLabel.setText("Veuillez sélectionner un utilisateur et un livre.");
        }
    }
    


    private java.util.Date calculateDueDate() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.WEEK_OF_YEAR, 2);
        return calendar.getTime();
    }
    
    
    @FXML
    private void handleTopBooksAction() {
        List<Book> topBooks = loanDao.fetchTopBooks();
    }

    private void clearUserFields() {
        lastNameField.clear();
        firstNameField.clear();
        emailField.clear();
    }
    
    
    
}*/