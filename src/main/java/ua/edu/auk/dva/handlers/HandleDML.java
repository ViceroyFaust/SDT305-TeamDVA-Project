package ua.edu.auk.dva.handlers;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import ua.edu.auk.dva.Database;
import ua.edu.auk.dva.View;

public class HandleDML implements RequestHandler {

  private final Database database;
  private final View view;

  @FunctionalInterface
  interface SqlExceptionThrowingFunction<R> {

    R get() throws SQLException;
  }

  private final Map<String, SqlExceptionThrowingFunction<HandlerReturnModel>> functionMap = new HashMap<>();

  private void fillMap() {
    functionMap.put("1", this::addEmployee);
    functionMap.put("2", this::addProductionStation);
    functionMap.put("3", this::updateManager);
  }

  private void validateMap(Map<String, String> input) {
    for (String key : input.keySet()) {
      if (input.get(key).isBlank()) {
        throw new IllegalArgumentException(
            "Cannot have blank data! Declare null explicitly as NULL!");
      }
    }
  }

  public HandleDML(Database db, View view) {
    this.database = db;
    this.view = view;
    fillMap();
  }

  @Override
  public HandlerReturnModel handleRequest(String request) throws SQLException {
    System.out.println("Handling DML request: " + request);
    return functionMap.get(request).get();
  }

  public HandlerReturnModel addEmployee() {
    view.print("Please enter the following data for the new employee:\n");
    Map<String, String> employeeData = view.multiPrompt(
        new String[]{"Employee ID", "First name", "Last name", "Salary", "Date joined (YYYY-MM-DD)",
            "Position", "Restaurant ID"});

    // Validate improper input
    validateMap(employeeData);
    String sql =
        """
            INSERT INTO Employee (EmployeeId, FirstName, LastName, Salary, DateJoined, Position, RestaurantId)
            VALUES ( ?, ?, ?, ?, ?, ?, ? );
            """;
    // We do not close the connection until the end of the program loop, since we want to continue
    // using the database until the user is done. We use a prepared statement to prevent SQL injection
    Connection conn = database.getDatabase();
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      int id = Integer.parseInt(employeeData.get("Employee ID"));
      int salary = Integer.parseInt(employeeData.get("Salary"));
      Date date = Date.valueOf(employeeData.get("Date joined (YYYY-MM-DD)"));

      stmt.setInt(1, id);
      stmt.setString(2, employeeData.get("First name"));
      stmt.setString(3, employeeData.get("Last name"));
      stmt.setInt(4, salary);
      stmt.setDate(5, date);
      stmt.setString(6, employeeData.get("Position"));
      stmt.setInt(7, Integer.parseInt(employeeData.get("Restaurant ID")));

      int rowsInserted = stmt.executeUpdate();

      if (rowsInserted > 0) {
        conn.commit();
        System.out.println("Inserted successfully.");
        return new HandlerReturnModel(true);
      } else {
        System.err.println("Insertion failed.");
        return new HandlerReturnModel(false);
      }
    } catch (SQLException e) {
      System.err.println("SQL Error: " + e.getMessage());
      return new HandlerReturnModel(false);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failure to parse input!");
    }
  }

  public HandlerReturnModel addProductionStation() {
    Map<String, String> stationData = view.multiPrompt(new String[]{"Name", "Category"});
    validateMap(stationData);
    String sql =
        """
            INSERT INTO ProductionStation (Name, Category)
            VALUES ( ?, ? );
            """;

    Connection conn = database.getDatabase();
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, stationData.get("Name"));
      stmt.setString(2, stationData.get("Category"));

      int rowsInserted = stmt.executeUpdate();

      if (rowsInserted > 0) {
        conn.commit();
        System.out.println("Station inserted successfully.");
        return new HandlerReturnModel(true);
      } else {
        System.err.println("Insertion failed.");
        return new HandlerReturnModel(false);
      }

    } catch (SQLException e) {
      System.err.println("SQL Error: " + e.getMessage());
      return new HandlerReturnModel(false);
    }
  }


  public HandlerReturnModel updateManager() {
    Map<String, String> managerData = view.multiPrompt(new String[]{"Manager ID", "Station Name"});
    validateMap(managerData);
    String updateStationSQL = "REPLACE INTO Manages (ManagerId, StationName) VALUES ( ?, ? ) ;";

    Connection conn = database.getDatabase();

    try (PreparedStatement stmt = conn.prepareStatement(updateStationSQL)) {
      stmt.setInt(1, Integer.parseInt(managerData.get("Manager ID")));
      stmt.setString(2, managerData.get("Station Name"));

      int rowsUpdated = stmt.executeUpdate();

      if (rowsUpdated > 0) {
        conn.commit();
        System.out.println("Manager updated successfully.");
        return new HandlerReturnModel(true);
      } else {
        System.err.println("Update failed.");
        return new HandlerReturnModel(false);
      }

    } catch (SQLException e) {
      System.err.println("SQL Error: " + e.getMessage());
      return new HandlerReturnModel(false);
    }
  }

}
