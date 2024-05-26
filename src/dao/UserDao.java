package dao;

import java.util.List;
import java.util.ArrayList;
import model.User;
import util.databaseManager;
import util.UserValidator;
import java.sql.*;

/**
 * User Data Access Object (DAO) to manage database operations related to users.
 */
public class UserDao {
    private Connection conn;

    /**
     * Constructor that initializes the database connection.
     */
    public UserDao() {
        this.conn = databaseManager.getConnection();
    }

    /**
     * Adds a new user to the database.
     * 
     * @param user The user to be added.
     * @return true if the user is added successfully, false otherwise.
     */
    public boolean addUser(User user) {
        if (!UserValidator.isValidName(user.getFirstName()) || !UserValidator.isValidName(user.getLastName()) || !UserValidator.isValidEmail(user.getEmail()) || !UserValidator.isValidAddress(user.getAddress()) || !UserValidator.isValidPhone(user.getPhone())) {
            System.out.println("Error: Invalid user data.");
            return false;
        }

        String sql = "INSERT INTO users (lastname, firstname, email, address, phone) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getLastName());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getAddress());
            stmt.setString(5, user.getPhone());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing user in the database.
     * 
     * @param user The user with updated information.
     * @return true if the user is updated successfully, false otherwise.
     */
    public boolean updateUser(User user) {
        if (!UserValidator.isValidName(user.getFirstName()) || !UserValidator.isValidName(user.getLastName()) || !UserValidator.isValidEmail(user.getEmail())) {
            System.out.println("Error: Invalid user data.");
            return false;
        }

        String sql = "UPDATE users SET lastname = ?, firstname = ?, email = ?, address = ?, phone = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getLastName());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getAddress());
            stmt.setString(5, user.getPhone());
            stmt.setInt(6, user.getId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a user from the database.
     * 
     * @param userId The ID of the user to be deleted.
     * @return true if the user is deleted successfully, false otherwise.
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all users from the database.
     * 
     * @return a list of all users.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, lastname, firstname, email, address, phone FROM users";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("lastname"),
                    rs.getString("firstname"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("phone")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}