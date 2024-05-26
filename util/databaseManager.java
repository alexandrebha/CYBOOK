package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing database connections.
 * It provides a method to get a connection to the MySQL database.
 */
public class databaseManager {
    static {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load JDBC driver");
            e.printStackTrace();
        }
    }

    /**
     * Gets a connection to the MySQL database.
     *
     * @return A Connection object to the MySQL database, or null if a connection could not be established.
     */
    public static Connection getConnection() {
        try {
            // Establish a connection to the database
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/cybook", "root", "alex");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
