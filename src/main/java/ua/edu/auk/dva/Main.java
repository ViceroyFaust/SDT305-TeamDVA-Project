package ua.edu.auk.dva;

import java.sql.Connection;
import java.sql.SQLException;
import ua.edu.auk.dva.handlers.HandleDML;
import ua.edu.auk.dva.handlers.HandleDQL;
import ua.edu.auk.dva.handlers.HandlerReturnModel;
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
    HandleDML dmlHandler = new HandleDML(db, view);
    while (true) {
      view.printMainMenu();
      String userChoice = view.getUserInput();
      switch (userChoice) {
        case "1":
          passToHandler(dqlHandler, view::printQueryMenu, view, db);
          break;
        case "2":
          passToHandler(dmlHandler, view::printModifyMenu, view, db);
          break;
        case "3":
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
        HandlerReturnModel returnModel = handler.handleRequest(choice);
        if (!returnModel.isSuccess()) {
          view.print("Failed to proceed with the request");
          continue;
        }

        if (returnModel.getTable() != null) {
          view.printTable(returnModel.getTable());
        } else {
          view.print("Operation succeeded");
        }

      } catch (IllegalArgumentException e) {
        System.out.println("Invalid Input: " + e.getMessage());
      } catch (Exception e) {
        System.out.println("An error occurred: " + e.getMessage());
      }
    }
  }

}