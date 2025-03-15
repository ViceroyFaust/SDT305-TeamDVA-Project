package ua.edu.auk.dva;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        // Try-with-resources
        try (Database db = new Database()) {
            // Retrieve the database connection object
            Connection connection = db.getDatabase();
            if (connection != null && !connection.isClosed()) {
                System.out.println("Connection to MySQL (dva_database) successful!");
            } else {
                System.out.println("Failed to establish a connection.");
            }
        } catch (SQLException e) {
            // Handle SQL-specific exceptions
            System.err.println("Database connection failed: " + e.getMessage());
        }catch (Exception e){
            // Handle any other exceptions
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }
}