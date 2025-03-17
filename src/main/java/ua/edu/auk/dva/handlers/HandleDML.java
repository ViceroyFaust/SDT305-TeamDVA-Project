package ua.edu.auk.dva.handlers;

import ua.edu.auk.dva.Database;

import java.sql.*;

import static java.sql.Types.DATE;
import static java.sql.Types.INTEGER;


public class HandleDML {
    /**
     * Adds a new employee to the database.
     * @param database Database connection
     * @param employeeData String array containing:
     * @return true if employee was added successfully, false otherwise
     */
    public static boolean addEmployee(Database database, String[] employeeData) {
        if (employeeData == null || employeeData.length != 7) {
            System.err.println("Data must contain exactly 7 elements.");
            return false;
        }

        String salaryValue = employeeData[3].isEmpty() ? "NULL" : employeeData[3];
        String dateValue = employeeData[4].isEmpty() ? "NULL" : "'" + employeeData[4] + "'";

        String sql = "INSERT INTO Employee (EmployeeId, FirstName, LastName, Salary, DateJoined, Position, RestaurantId) VALUES (" +
                employeeData[0] + ", '" +
                employeeData[1] + "', '" +
                employeeData[2] + "', " +
                salaryValue + ", " +
                dateValue + ", '" +
                employeeData[5] + "', " +
                employeeData[6] + ")";

        try (Connection conn = database.getDatabase();
             Statement stmt = conn.createStatement()) {
            int rowsInserted = stmt.executeUpdate(sql);

            if (rowsInserted > 0) {
                conn.commit();
                System.out.println("Inserted successfully.");
            } else {
                System.err.println("Insertion failed.");
            }
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            return false;
        }
    }


    /**
     * Adds a new production station to the database.
     * @param database Database connection
     * @param stationData String array containing data.
     * @return true if station was added successfully, false otherwise
     */
    public static boolean addProductionStation(Database database, String[] stationData) {
        if (stationData == null || stationData.length != 2) {
            System.err.println("Production station data must contain exactly 2 elements.");
            return false;
        }

        String sql = "INSERT INTO ProductionStation (Name, Category) VALUES ('" +
                stationData[0] + "', '" + stationData[1] + "')";

        try (Connection conn = database.getDatabase();
             Statement stmt = conn.createStatement()) {
            int rowsInserted = stmt.executeUpdate(sql);

            if (rowsInserted > 0) {
                conn.commit();
                System.out.println("Station inserted successfully.");
                return true;
            } else {
                System.err.println("Insertion failed.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            return false;
        }
    }
    /**
     * Updates a manager's assigned production station.
     * @param database Database connection
     * @param managerData String array containing data.
     * @return true if the update was successful, false otherwise
     */
    public static boolean updateManager(Database database, String[] managerData) {
        if (managerData == null || managerData.length != 2) {
            System.err.println("Data must contain exactly 2 elements.");
            return false;
        }

        String checkStationSQL = "SELECT Name FROM ProductionStation WHERE Name = '" + managerData[1] + "'";
        String updateStationSQL = "UPDATE Manages SET StationName = '" + managerData[1] + "' WHERE ManagerId = " + managerData[0];

        try (Connection conn = database.getDatabase();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(checkStationSQL);

            if (!rs.next()) {
                System.err.println("The station '" + managerData[1] + "' does not exist in ProductionStation.");
                return false;
            }

            int rowsUpdated = stmt.executeUpdate(updateStationSQL);

            if (rowsUpdated > 0) {
                conn.commit();
                System.out.println("Manager updated successfully.");
                return true;
            } else {
                System.err.println("Update failed.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            return false;
        }
    }
}
