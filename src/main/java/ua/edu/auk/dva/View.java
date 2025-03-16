package ua.edu.auk.dva;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * A class abstracting the user interface
 *
 * @author Danylo Rybchynskyi
 */
public class View {

  private final Scanner cli = new Scanner(System.in);

  /**
   * Gets a single line of user input from the command line
   *
   * @return the user's line of input
   */
  public String getUserInput() {
    return cli.nextLine();
  }

  /**
   * Gets a single line of user input from the command line with a custom prompt
   *
   * @param message the prompt message
   * @return the user's line of input
   */
  public String prompt(String message) {
    print(message);
    return getUserInput();
  }

  /**
   * Returns a map with variable names and their related input. Helpful for getting a lot of data
   * input from the user.
   *
   * @param values the variables that the user should fill
   * @return a map with variables and their related user input
   */
  public Map<String, String> multiPrompt(String[] values) {
    HashMap<String, String> inputVariables = new HashMap<>();
    String userInput;
    for (String key : values) {
      userInput = prompt(key + ": ");
      inputVariables.put(key, userInput);
    }
    return inputVariables;
  }

  /**
   * Prompts the user for the database URI
   *
   * @return the user input
   */
  public String promptDatabaseURI() {
    return prompt("Please input the database URI (e.g. \"jdbc:mysql://localhost/mydb\"): ");
  }

  /**
   * Prompts the user for the database username
   *
   * @return the user input
   */
  public String promptDatabaseUser() {
    return prompt("Please input the database username: ");
  }

  /**
   * Gets the password of the database from the user. Tries to mask the password using console. If
   * the console cannot be instantiated, falls back to the Scanner method, which exposes the user
   * input to the console.
   *
   * @return the user password
   */
  public String promptDatabasePass() {
    Console console = System.console();
    String prompt = "Please input the database password: ";
    String input;
    if (console != null) {
      char[] pass = console.readPassword(prompt);
      input = new String(pass);
      java.util.Arrays.fill(pass, ' ');
    } else {
      print("Failed to get console, falling back to Scanner\n");
      input = prompt(prompt);
    }
    return input;
  }

  /**
   * Prints the main program menu
   */
  public void printMainMenu() {
    print("""
        1. View Database
        2. Modify Database
        3. Exit
        """);
  }


  /**
   * Prints the DQL (query) menu
   */
  public void printQueryMenu() {
    print("""
        1. View Employees
        2. View Employees by Position
        3. View Employee's Stations
        4. View Instructor's Students
        5. View Manager's Stations
        6. View Employee Schedule
        7. View Production Stations
        8. View Restaurants
        0. Exit Submenu
        """);
  }

  /**
   * Prints the DML (modify) menu
   */
  public void printModifyMenu() {
    print("""
        1. Add Employee
        2. Add Restaurant
        3. Add Production Station
        4. Add Schedule
        5. Update Employee Station
        6. Update Employee Training
        7. Update Manager
        0. Exit Submenu
        """);
  }

  /**
   * Prints the contents of the table to screen. If the table's name is "unnamed" then the name will
   * not be printed alongside the table
   */
  public void printTable(Table table) {
    if (!table.getName().equals("unnamed")) {
      print(table.getName() + "\n");
    }

    for (int i = 0; i < table.getRows(); i++) {
      for (int j = 0; j < table.getCols() - 1; j++) {
        System.out.printf("%24s", table.get(i, j));
      }
      System.out.printf("%24s%n", table.get(i, table.getCols() - 1));
    }
  }

  /**
   * Prints a message. Beware, it does not automatically append a newline at the end of the message
   *
   * @param message the message to print
   */
  public void print(String message) {
    System.out.print(message);
  }
}
