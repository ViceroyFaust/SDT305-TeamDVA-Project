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

            // Print out restaurants,testing restaurant query
            System.out.println("\n🍽️ Restaurants:");
            for (int i = 0; i < restaurants.getRows(); i++) {
                System.out.println("🏢 Restaurant ID: " + restaurants.get(i, 0) +
                        " | Opens: " + restaurants.get(i, 1) +
                        " | Closes: " + restaurants.get(i, 2) +
                        " | Opened: " + restaurants.get(i, 3));
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