package ua.edu.auk.dva.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import ua.edu.auk.dva.Database;
import ua.edu.auk.dva.Table;
import ua.edu.auk.dva.View;

public class HandleDQL implements RequestHandler {

  private final Database database;
  private final View view;

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

  private final Map<String, SqlExceptionThrowingFunction<HandlerReturnModel>> functionMap = new HashMap<>();

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
    System.out.println("Handling DQL request: " + request);
    return functionMap.get(request).get();
  }

  /**
   * Fetch all employees from the database and return as a Table object.
   */
  public HandlerReturnModel getEmployeesNoArgs() throws SQLException {
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

    String[] employeeIds = request.split(",");

    Connection conn = database.getDatabase();
    String placeholders = String.join(",",
        Arrays.stream(employeeIds).map(id -> "?").toArray(String[]::new));

    String sql = """
            SELECT EmployeeId, FirstName, LastName, Salary, DateJoined, Position, RestaurantId
            FROM Employee
            WHERE EmployeeId IN (%s)
        """.formatted(placeholders);

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      // Set values for the placeholders
      for (int i = 0; i < employeeIds.length; i++) {
        stmt.setInt(i + 1, Integer.parseInt(employeeIds[i]));
      }

      ResultSet rs = stmt.executeQuery();
      return resultsToModel(rs, "Employee(s)");
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

      try (PreparedStatement statement = database.getDatabase().prepareStatement(sql)) {
          statement.setString(1, request);
          ResultSet results = statement.executeQuery();
          return resultsToModel(results, "Employee by Position");
      }
  }

  /**
   * Fetch stations where employees are trained or manage.
   *
   * @return Table containing employee-station mapping
   */
  public HandlerReturnModel getEmployeeStations() throws SQLException {
    String[] employeeIds = view.prompt(
        "Please enter the Employee IDs separated by commas (ex. 1,2,3,): ").split(",");
    if (employeeIds.length == 0) {
      throw new IllegalArgumentException("Employee IDs array cannot be empty.");
    }

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

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      for (int i = 0; i < employeeIds.length; i++) {
        stmt.setInt(i + 1, Integer.parseInt(employeeIds[i]));
        stmt.setInt(i + 1 + employeeIds.length, Integer.parseInt(employeeIds[i]));
      }

      ResultSet rs = stmt.executeQuery();
      return resultsToModel(rs, "Employee Stations");
    }
  }

  /**
   * Fetch students (employees) trained by specific instructors (trainers).
   *
   * @return Table containing instructor-student relationships
   */
  public HandlerReturnModel getInstructorStudents() throws SQLException {
    String[] instructorIds = view.prompt(
        "Please enter the Instructor IDs separated by commas (ex. 1,2,3,): ").split(",");
    if (instructorIds.length == 0) {
      throw new IllegalArgumentException("Instructor IDs array cannot be empty.");
    }

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

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      for (int i = 0; i < instructorIds.length; i++) {
        stmt.setInt(i + 1, Integer.parseInt(instructorIds[i]));
      }

      ResultSet rs = stmt.executeQuery();
      return resultsToModel(rs, "Instructor Students");
    }
  }

  /**
   * Fetch stations managed by specific managers.
   *
   * @return Table containing manager-station relationships
   */
  public HandlerReturnModel getManagersStations() throws SQLException {
    String[] managerIds = view.prompt(
        "Please enter the Manager IDs separated by commas (ex. 1,2,3,): ").split(",");
    if (managerIds.length == 0) {
      throw new IllegalArgumentException("Manager IDs array cannot be empty.");
    }

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

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      for (int i = 0; i < managerIds.length; i++) {
        stmt.setInt(i + 1, Integer.parseInt(managerIds[i]));
      }

      ResultSet rs = stmt.executeQuery();
      return resultsToModel(rs, "Manager Stations");
    }
  }

  /**
   * Fetch schedule for specific employees.
   *
   * @return Table containing employee schedule
   */
  public HandlerReturnModel getEmployeeSchedule() throws SQLException {
    String[] employeeIds = view.prompt(
        "Please enter the Employee IDs separated by commas (ex. 1,2,3,): ").split(",");
    if (employeeIds.length == 0) {
      throw new IllegalArgumentException("Employee IDs array cannot be empty.");
    }

    Connection conn = this.database.getDatabase();

    String placeholders = String.join(",",
        Arrays.stream(employeeIds).map(id -> "?").toArray(String[]::new));

    String sql = """
            SELECT S.EmployeeId, E.FirstName, E.LastName, S.Date, S.StartTime, S.EndTime
            FROM Schedule S
            JOIN Employee E ON S.EmployeeId = E.EmployeeId
            WHERE S.EmployeeId IN (%s)
        """.formatted(placeholders);

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      for (int i = 0; i < employeeIds.length; i++) {
        stmt.setInt(i + 1, Integer.parseInt(employeeIds[i]));
      }

      ResultSet rs = stmt.executeQuery();
      return resultsToModel(rs, "Employee Schedule");
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

    try (PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      return resultsToModel(rs, "Restaurants");
    }
  }
}
