package ua.edu.auk.dva.handlers;


import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
  private final Map<String, HandleDQL.SqlExceptionThrowingFunction<HandlerReturnModel>> functionMap = new HashMap<>();
  private void fillMap() {
    functionMap.put("1", this::addEmployee);
    functionMap.put("2", this::addProductionStation);
    functionMap.put("3", this::updateManager);
  }

  public HandleDML(Database db, View view) {
    this.database = db;
    this.view = view;
    fillMap();
  }

  @Override
  public HandlerReturnModel handleRequest(String request, String[] args) throws SQLException {
    System.out.println("Handling DML request: " + request);
    return functionMap.get(request).get();
  }

  public HandlerReturnModel addEmployee() {
    view.print("Please enter the following data for the new employee:\n");
    Map<String, String> employeeData = view.multiPrompt(new String[]{"Employee ID", "First name", "Last name", "Salary", "Date joined (YYYY-MM-DD)", "Position", "Restaurant ID"});

    String salaryValue = employeeData.get("Salary").isEmpty() ? "NULL" : employeeData.get("Salary");
    String dateValue = employeeData.get("Date joined (YYYY-MM-DD)").isEmpty() ? "NULL" : "'" + employeeData.get("Date joined (YYYY-MM-DD)") + "'";

    String sql = "INSERT INTO Employee (EmployeeId, FirstName, LastName, Salary, DateJoined, Position, RestaurantId) VALUES (" +
            employeeData.get("Employee ID") + ", '" +
            employeeData.get("First name") + "', '" +
            employeeData.get("Last name") + "', " +
            salaryValue + ", " +
            dateValue + ", '" +
            employeeData.get("Position") + "', " +
            employeeData.get("Restaurant ID") + ")";
    try {
      Connection conn = database.getDatabase();
      Statement stmt = conn.createStatement();

      int rowsInserted = stmt.executeUpdate(sql);

      if (rowsInserted > 0) {
        conn.commit();
        System.out.println("Inserted successfully.");
      } else {
        System.err.println("Insertion failed.");
      }
      if(rowsInserted > 0) {
        return new HandlerReturnModel(true);
      } else {
        return new HandlerReturnModel(false);
      }

    } catch (SQLException e) {
      System.err.println("SQL Error: " + e.getMessage());
      return new HandlerReturnModel(false);
    }
  }

  public HandlerReturnModel addProductionStation() {
    Map<String, String> stationData = view.multiPrompt(new String[]{"Name", "Category"});
    String sql = "INSERT INTO ProductionStation (Name, Category) VALUES ('" +
            stationData.get("Name") + "', '" + stationData.get("Category") + "')";

    try{
      Connection conn = database.getDatabase();
      Statement stmt = conn.createStatement();
      int rowsInserted = stmt.executeUpdate(sql);

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

    String checkStationSQL = "SELECT Name FROM ProductionStation WHERE Name = '" + managerData.get("Station Name") + "'";
    String updateStationSQL = "UPDATE Manages SET StationName = '" + managerData.get("Station Name") + "' WHERE ManagerId = " + managerData.get("Manager ID");

    try {
      Connection conn = database.getDatabase();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(checkStationSQL);

      if (!rs.next()) {
        System.err.println("The station '" + managerData.get("Station Name") + "' does not exist in ProductionStation.");
        return new HandlerReturnModel(false);
      }

      int rowsUpdated = stmt.executeUpdate(updateStationSQL);

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
