package ua.edu.auk.dva.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.edu.auk.dva.Database;
import ua.edu.auk.dva.Table;
import ua.edu.auk.dva.View;

public class HandleDQL implements RequestHandler {

  private static final Logger logger = LogManager.getLogger(HandleDQL.class);

  private final Database database;
  private final View view;
  private final Map<String, SqlExceptionThrowingFunction<HandlerReturnModel>> functionMap = new HashMap<>();

  @FunctionalInterface
  interface SqlExceptionThrowingFunction<R> {

    R get() throws SQLException;
  }

  /**
   * Converts a ResultSet to a table of strings.
   *
   * @param results the results of a query
   * @param name    the name of the table
   * @return a table of strings
   * @throws SQLException if any exception occurs during this process
   */
  private HandlerReturnModel resultsToModel(ResultSet results, String name) throws SQLException {
    ResultSetMetaData metaData = results.getMetaData();
    int columns = metaData.getColumnCount();
    Table table = new Table(columns, name);

    // Add the column headers to the table
    for (int i = 1; i <= columns; i++) {
      table.add(metaData.getColumnLabel(i));
    }
    // Add the column values to the table
    while (results.next()) {
      for (int i = 1; i <= columns; i++) {
        table.add(results.getString(i));
      }
    }
    return new HandlerReturnModel(table);
  }

  /**
   * Converts a ResultSet to a table of strings.
   *
   * @param results the results of a query
   * @return a table of strings
   * @throws SQLException if any exception occurs during this process
   */
  private HandlerReturnModel resultsToModel(ResultSet results) throws SQLException {
    ResultSetMetaData metaData = results.getMetaData();
    int columns = metaData.getColumnCount();
    Table table = new Table(columns);

    // Add the column headers to the table
    for (int i = 1; i <= columns; i++) {
      table.add(metaData.getColumnLabel(i));
    }
    // Add the column values to the table
    while (results.next()) {
      for (int i = 1; i <= columns; i++) {
        table.add(results.getString(i));
      }
    }
    return new HandlerReturnModel(table);
  }

  /**
   * Validates whether a string conforms to a comma separated list of integers
   * (e.g.:`1,2,3,4,10,12`).
   *
   * @param input the String to validate
   * @throws IllegalArgumentException if the input is not valid
   */
  private void validateCommaSeparatedIntegers(String input) {
    if (!Pattern.matches("^[0-9]+(,[0-9]+)*$", input)) {
      logger.warn("Invalid comma separated integer input detected: {}", input);
      throw new IllegalArgumentException(
          "Input must be numbers separated by commas (ex. 1,2,3...)");
    }
  }

  private void fillMap() {
    functionMap.put("1", this::getEmployees);
    functionMap.put("2", this::getEmployeeByPosition);
    functionMap.put("3", this::getEmployeeStations);
    functionMap.put("4", this::getInstructorStudents);
    functionMap.put("5", this::getManagersStations);
    functionMap.put("6", this::getEmployeeSchedule);
    functionMap.put("7", this::getProductionStations);
    functionMap.put("8", this::getRestaurants);
  }

  public HandleDQL(Database database, View view) {
    this.database = database;
    this.view = view;
    fillMap();
  }

  @Override
  public HandlerReturnModel handleRequest(String request) throws SQLException {
    logger.info("Handling DQL request: {}", request);
    System.out.println("Handling DQL request: " + request);
    return functionMap.get(request).get();
  }

  /**
   * Fetch all employees from the database and return as a Table object.
   */
  public HandlerReturnModel getEmployeesNoArgs() throws SQLException {
    logger.info("Requesting for all employees.");
    Connection conn = this.database.getDatabase();
    String sql = "SELECT * FROM Employee";

    try (PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      return resultsToModel(rs, "Employees");
    }
  }

  /**
   * Fetch details of specific employees.
   *
   * @return Table containing employee details
   */
  public HandlerReturnModel getEmployees() throws SQLException {
    String request = view.prompt("Please enter the Employee IDs separated by commas (ex. 1,2,3,) " +
        "or nothing to output all employees: ");

    if (request.isEmpty()) {
      return getEmployeesNoArgs();
    }
    // Detect illegal patterns to prevent from running malicious code on the server
    validateCommaSeparatedIntegers(request);

    String[] employeeIds = request.split(",");

    Connection conn = database.getDatabase();
    String placeholders = String.join(",",
        Arrays.stream(employeeIds).map(id -> "?").toArray(String[]::new));

    String sql = """
            SELECT EmployeeId, FirstName, LastName, Salary, DateJoined, Position, RestaurantId
            FROM Employee
            WHERE EmployeeId IN (%s)
        """.formatted(placeholders);

    logger.info("Selecting employees with id: {}", request);
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      // Set values for the placeholders
      for (int i = 0; i < employeeIds.length; i++) {
        stmt.setInt(i + 1, Integer.parseInt(employeeIds[i]));
      }
      try (ResultSet rs = stmt.executeQuery()) {
        return resultsToModel(rs, "Employee(s)");
      }
    }
  }

  /**
   * Fetches the details of employees with a given position
   *
   * @return A table representing said employees
   * @throws SQLException if an error occurs with the database
   */
  public HandlerReturnModel getEmployeeByPosition() throws SQLException {
    String request = view.prompt("Please enter position: ");

    String sql = "SELECT * FROM Employee WHERE Position = ? ;";

    // No need to check for bad input because we aren't directly inserting data

    logger.info("Querying employees by position: {}", request);
    try (PreparedStatement statement = database.getDatabase().prepareStatement(sql)) {
      statement.setString(1, request);
      try (ResultSet results = statement.executeQuery()) {
        return resultsToModel(results, "Employee by Position");
      }
    }
  }

  /**
   * Fetch stations where employees are trained or manage.
   *
   * @return Table containing employee-station mapping
   */
  public HandlerReturnModel getEmployeeStations() throws SQLException {
    String input = view.prompt(
        "Please enter the Employee IDs separated by commas (ex. 1,2,3,): ");
    if (input.isEmpty()) {
      throw new IllegalArgumentException("You need to provide input");
    }
    // Detect illegal patterns to prevent from running malicious code on the server
    validateCommaSeparatedIntegers(input);

    String[] employeeIds = input.split(",");

    Connection conn = this.database.getDatabase();

    String placeholders = String.join(",",
        Arrays.stream(employeeIds).map(id -> "?").toArray(String[]::new));

    String sql = """
            SELECT E.EmployeeId, E.FirstName, E.LastName, T.StationName, 'Trained In' AS RelationType
            FROM Employee E
            JOIN TrainedIn T ON E.EmployeeId = T.EmployeeId
            WHERE E.EmployeeId IN (%s)
            UNION
            SELECT E.EmployeeId, E.FirstName, E.LastName, M.StationName, 'Manages' AS RelationType
            FROM Employee E
            JOIN Manages M ON E.EmployeeId = M.ManagerId
            WHERE E.EmployeeId IN (%s)
        """.formatted(placeholders, placeholders);

    logger.info("Requesting employee station by id: {}", input);
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      for (int i = 0; i < employeeIds.length; i++) {
        stmt.setInt(i + 1, Integer.parseInt(employeeIds[i]));
        stmt.setInt(i + 1 + employeeIds.length, Integer.parseInt(employeeIds[i]));
      }

      try (ResultSet rs = stmt.executeQuery()) {
        return resultsToModel(rs, "Employee Stations");
      }
    }
  }

  /**
   * Fetch students (employees) trained by specific instructors (trainers).
   *
   * @return Table containing instructor-student relationships
   */
  public HandlerReturnModel getInstructorStudents() throws SQLException {
    String input = view.prompt(
        "Please enter the Instructor IDs separated by commas (ex. 1,2,3,): ");

    // Detect illegal patterns to prevent from running malicious code on the server
    validateCommaSeparatedIntegers(input);

    String[] instructorIds = input.split(",");

    Connection conn = this.database.getDatabase();

    String placeholders = String.join(",",
        Arrays.stream(instructorIds).map(id -> "?").toArray(String[]::new));

    String sql = """
            SELECT T.TrainerId, I.FirstName AS InstructorFirstName, I.LastName AS InstructorLastName,
                   T.EmployeeId, E.FirstName AS StudentFirstName, E.LastName AS StudentLastName
            FROM Train T
            JOIN Employee I ON T.TrainerId = I.EmployeeId
            JOIN Employee E ON T.EmployeeId = E.EmployeeId
            WHERE T.TrainerId IN (%s)
        """.formatted(placeholders);
  logger.info("Requesting intructors' students by id: {}", input);
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      for (int i = 0; i < instructorIds.length; i++) {
        stmt.setInt(i + 1, Integer.parseInt(instructorIds[i]));
      }

      try (ResultSet rs = stmt.executeQuery()) {
        return resultsToModel(rs, "Instructor Students");
      }
    }
  }

  /**
   * Fetch stations managed by specific managers.
   *
   * @return Table containing manager-station relationships
   */
  public HandlerReturnModel getManagersStations() throws SQLException {
    String input = view.prompt(
        "Please enter the Manager IDs separated by commas (ex. 1,2,3,): ");
    // Validate the input before proceeding to conform to the list
    validateCommaSeparatedIntegers(input);

    String[] managerIds = input.split(",");

    Connection conn = database.getDatabase();

    String placeholders = String.join(",",
        Arrays.stream(managerIds).map(id -> "?").toArray(String[]::new));

    String sql = """
            SELECT M.ManagerId, E.FirstName AS ManagerFirstName, E.LastName AS ManagerLastName,
                   M.StationName, P.Category
            FROM Manages M
            JOIN Employee E ON M.ManagerId = E.EmployeeId
            JOIN ProductionStation P ON M.StationName = P.Name
            WHERE M.ManagerId IN (%s)
        """.formatted(placeholders);

    logger.info("Requesting managers' stations by id: {}", input);
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      for (int i = 0; i < managerIds.length; i++) {
        stmt.setInt(i + 1, Integer.parseInt(managerIds[i]));
      }

      try (ResultSet rs = stmt.executeQuery()) {
        return resultsToModel(rs, "Manager Stations");
      }
    }
  }

  /**
   * Fetch schedule for specific employees.
   *
   * @return Table containing employee schedule
   */
  public HandlerReturnModel getEmployeeSchedule() throws SQLException {
    String input = view.prompt(
        "Please enter the Employee IDs separated by commas (ex. 1,2,3,): ");
    validateCommaSeparatedIntegers(input);

    String[] employeeIds = input.split(",");

    Connection conn = this.database.getDatabase();

    String placeholders = String.join(",",
        Arrays.stream(employeeIds).map(id -> "?").toArray(String[]::new));

    String sql = """
            SELECT S.EmployeeId, E.FirstName, E.LastName, S.Date, S.StartTime, S.EndTime
            FROM Schedule S
            JOIN Employee E ON S.EmployeeId = E.EmployeeId
            WHERE S.EmployeeId IN (%s)
        """.formatted(placeholders);

    logger.info("Requesting employees' stations by id: {}", input);
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      for (int i = 0; i < employeeIds.length; i++) {
        stmt.setInt(i + 1, Integer.parseInt(employeeIds[i]));
      }

      try (ResultSet rs = stmt.executeQuery()) {
        return resultsToModel(rs, "Employee Schedule");
      }
    }
  }

  /**
   * Fetch all production stations from the database.
   *
   * @return Table containing all production stations
   */
  public HandlerReturnModel getProductionStations() throws SQLException {
    Connection conn = this.database.getDatabase();
    String sql = "SELECT Name, Category FROM ProductionStation";

    logger.info("Requesting production stations.");
    try (PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      return resultsToModel(rs, "Production Stations");
    }
  }

  /**
   * Fetch all restaurants from the database.
   *
   * @return Table containing all restaurants
   */
  public HandlerReturnModel getRestaurants() throws SQLException {
    Connection conn = this.database.getDatabase();
    String sql = "SELECT RestaurantId, OpeningTime, ClosingTime, DateOpened FROM Restaurant";

    logger.info("Requesting restaurants.");
    try (PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      return resultsToModel(rs, "Restaurants");
    }
  }
}
