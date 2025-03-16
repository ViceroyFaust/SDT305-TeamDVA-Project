package ua.edu.auk.dva;

import java.sql.Connection;
import java.sql.SQLException;
import ua.edu.auk.dva.handlers.HandleDQL;
import ua.edu.auk.dva.handlers.RequestHandler;

public class Main {

  public static void main(String[] args) {
    View view = new View();
    String dbURI = view.promptDatabaseURI();
    String dbUser = view.promptDatabaseUser();
    String dbPass = view.promptDatabasePass();
    // Try-with-resources
    try (Database db = new Database(dbURI, dbUser, dbPass)) {
      // Retrieve the database connection object
      Connection connection = db.getDatabase();
      if (connection != null && !connection.isClosed()) {
        System.out.println("Connection to MySQL (dva_database) successful!");
        mainMenu(view, db);
      } else {
        System.out.println("Failed to establish a connection.");
      }
    } catch (SQLException e) {
      // Handle SQL-specific exceptions
      System.err.println("Database connection failed: " + e.getMessage());
    } catch (Exception e) {
      // Handle any other exceptions
      System.err.println("Database connection failed: " + e.getMessage());
    }
  }

  private static void mainMenu(View view, Database db) {
    HandleDQL dqlHandler = new HandleDQL(db, view);
    while (true) {
      view.printMainMenu();
      String userChoice = view.getUserInput();
      switch (userChoice) {
        case "1":
          passToHandler(dqlHandler, view::printQueryMenu, view, db);
          break;
        case "2":
          System.out.println("Exiting...");
          return;
        default:
          System.out.println("Invalid input. Please try again.");
      }
    }
  }

  private static void passToHandler(RequestHandler handler, Runnable printSubMenu, View view,
      Database db) {
    while (true) {
      try {
        printSubMenu.run();
        String choice = view.getUserInput();
          if (choice.equals("0")) {
              return;
          }
        Table table = handler.handleRequest(choice, new String[]{});
        if (table == null) {
          view.print("Failed to proceed with the request");
          return;
        }
        view.printTable(table);
      } catch (Exception e) {
        System.out.println("An error occurred: " + e.getMessage());
      }
    }
  }

}