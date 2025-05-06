package ua.edu.auk.dva;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.edu.auk.dva.handlers.HandleDML;
import ua.edu.auk.dva.handlers.HandleDQL;
import ua.edu.auk.dva.handlers.HandlerReturnModel;
import ua.edu.auk.dva.handlers.RequestHandler;

public class Main {

  private static final Logger logger = LogManager.getLogger(Main.class);

  public static void main(String[] args) {
    View view = new View();
    String dbURI = view.promptDatabaseURI();
    String dbUser = view.promptDatabaseUser();
    String dbPass = view.promptDatabasePass();
    logger.info("{} is attempting to connect to {} .", dbUser, dbURI);
    // Try-with-resources
    try (Database db = new Database(dbURI, dbUser, dbPass)) {
      // Retrieve the database connection object
      Connection connection = db.getDatabase();
      if (connection != null && !connection.isClosed()) {
        logger.info("{} successfully connected to {} .", dbUser, dbURI);
        view.print("Successfully connected to " + dbURI + "!\n");
        mainMenu(view, db);
      } else {
        logger.fatal("Connection failed to {}", dbURI);
        view.print("Failed to connect to the database.");
      }
    } catch (SQLException e) {
      // Handle SQL-specific exceptions
      logger.fatal("Database connection failed: {}", e.getMessage());
      System.err.println("Database connection failed: " + e.getMessage());
    } catch (Exception e) {
      // Handle any other exceptions
      logger.fatal("A non-SQL exception occurred: {}", e.getMessage());
      System.err.println("Database connection failed: " + e.getMessage());
    }
  }

  private static void mainMenu(View view, Database db) {
    HandleDQL dqlHandler = new HandleDQL(db, view);
    HandleDML dmlHandler = new HandleDML(db, view);
    logger.info("Entering main menu.");
    while (true) {
      view.printMainMenu();
      String userChoice = view.getUserInput();
      switch (userChoice) {
        case "1":
          logger.info("Using DQL Handler.");
          passToHandler(dqlHandler, view::printQueryMenu, view, db);
          break;
        case "2":
          logger.info("Using DML Handler.");
          passToHandler(dmlHandler, view::printModifyMenu, view, db);
          break;
        case "3":
          logger.info("Exiting the application.");
          System.out.println("Exiting...");
          return;
        default:
          logger.info("Invalid input for the main menu: \"{}\"", userChoice);
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
          logger.info("Exiting handler {} .", handler.getClass().getName());
          return;
        }
        HandlerReturnModel returnModel = handler.handleRequest(choice);
        if (!returnModel.isSuccess()) {
          logger.info("Unsuccessful handler request.");
          view.print("Failed to proceed with the request");
          continue;
        }

        if (returnModel.getTable() != null) {
          view.printTable(returnModel.getTable());
        } else {
          view.print("Operation succeeded");
        }

      } catch (IllegalArgumentException e) {
        logger.warn("Illegal argument provided to handler: {}", e.getMessage());
        System.out.println("Invalid Input: " + e.getMessage());
      } catch (Exception e) {
        logger.error("An unexpected error has occurred: {}", e.getMessage());
        System.out.println("An error occurred: " + e.getMessage());
      }
    }
  }

}