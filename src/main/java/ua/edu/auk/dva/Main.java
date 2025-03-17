package ua.edu.auk.dva;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        // Try-with-resources
        try (Database db = new Database()) {
            System.out.println("Connected to MySQL (dva_database) successfully!");
            String[] employeeIds = {"1","2","3","4","5","6","7","8","9","10"};
            Table restaurants = HandlerDQL.getRestaurants(db);

            String[] newEmployee = {"1005", "Sophia", "Ivanova", "47000", "2024-03-16", "Trainer", "1"};

            String[] newStation = {"Sasuage Station", "Kitchen"};

            String[] managerData = {"3", "Sasuage Station"};

            boolean success = HandlerDML.updateManager(db, managerData);

            if (success) {
                System.out.println("Station updated successfully!");
            } else {
                System.out.println("Failed to update station.");
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