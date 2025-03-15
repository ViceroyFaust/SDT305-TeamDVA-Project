package ua.edu.auk.dva.handlers;

import ua.edu.auk.dva.Database;
import ua.edu.auk.dva.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class HandleDQL implements  RequestHandler{
    @Override
    public void handleRequest(String request, String[] args) {
        System.out.println("Handling DQL request: " + request);
    }
    /**
     * Fetch all employees from the database and return as a Table object.
     */
    public static Table getEmployees(Database database) throws SQLException {
        Connection conn = database.getDatabase();
        String sql = "SELECT EmployeeId, FirstName, LastName, Position, Salary, DateJoined, RestaurantId FROM Employee";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            Table table = new Table(6, "Employees");
            while (rs.next()) {
                table.add(String.valueOf(rs.getInt("EmployeeId")));
                table.add(rs.getString("FirstName"));
                table.add(rs.getString("LastName"));
                table.add(rs.getString("Position"));
                table.add(String.valueOf(rs.getInt("Salary")));
                table.add(String.valueOf(rs.getDate("DateJoined")));
                table.add(String.valueOf(rs.getInt("RestaurantId")));
            }
            return table;
        }
    }

    /**
     * Fetch stations where employees are trained or manage.
     * @param database Database connection
     * @param employeeIds List of Employee IDs
     * @return Table containing employee-station mapping
     */
    public static Table getEmployeeStations(Database database, String[] employeeIds) throws SQLException {
        if (employeeIds == null || employeeIds.length == 0) {
            throw new IllegalArgumentException("Employee IDs array cannot be empty.");
        }

        Connection conn = database.getDatabase();

        String placeholders = String.join(",", Arrays.stream(employeeIds).map(id -> "?").toArray(String[]::new));

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
            Table table = new Table(5, "Employee Stations");

            while (rs.next()) {
                table.add(String.valueOf(rs.getInt("EmployeeId")));
                table.add(rs.getString("FirstName"));
                table.add(rs.getString("LastName"));
                table.add(rs.getString("StationName"));
                table.add(rs.getString("RelationType"));
            }
            return table;
        }
    }

    /**
     * Fetch students (employees) trained by specific instructors (trainers).
     * @param database Database connection
     * @param instructorIds List of Instructor (Trainer) IDs
     * @return Table containing instructor-student relationships
     */
    public static Table getInstructorStudents(Database database, String[] instructorIds) throws SQLException {
        if (instructorIds == null || instructorIds.length == 0) {
            throw new IllegalArgumentException("Instructor IDs array cannot be empty.");
        }

        Connection conn = database.getDatabase();

        String placeholders = String.join(",", Arrays.stream(instructorIds).map(id -> "?").toArray(String[]::new));

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
            Table table = new Table(6, "Instructor-Student Assignments");

            while (rs.next()) {
                table.add(String.valueOf(rs.getInt("TrainerId")));
                table.add(rs.getString("InstructorFirstName"));
                table.add(rs.getString("InstructorLastName"));
                table.add(String.valueOf(rs.getInt("EmployeeId")));
                table.add(rs.getString("StudentFirstName"));
                table.add(rs.getString("StudentLastName"));
            }
            return table;
        }
    }
    /**
     * Fetch stations managed by specific managers.
     * @param database Database connection
     * @param managerIds List of Manager IDs
     * @return Table containing manager-station relationships
     */
    public static Table getManagersStations(Database database, String[] managerIds) throws SQLException {
        if (managerIds == null || managerIds.length == 0) {
            throw new IllegalArgumentException("Manager IDs array cannot be empty.");
        }

        Connection conn = database.getDatabase();

        String placeholders = String.join(",", Arrays.stream(managerIds).map(id -> "?").toArray(String[]::new));

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
            Table table = new Table(5, "Manager-Station Assignments");

            while (rs.next()) {
                table.add(String.valueOf(rs.getInt("ManagerId")));
                table.add(rs.getString("ManagerFirstName"));
                table.add(rs.getString("ManagerLastName"));
                table.add(rs.getString("StationName"));
                table.add(rs.getString("Category"));
            }
            return table;
        }
    }
    /**
     * Fetch schedule for specific employees.
     * @param database Database connection
     * @param employeeIds List of Employee IDs
     * @return Table containing employee schedule
     */
    public static Table getEmployeeSchedule(Database database, String[] employeeIds) throws SQLException {
        if (employeeIds == null || employeeIds.length == 0) {
            throw new IllegalArgumentException("Employee IDs array cannot be empty.");
        }

        Connection conn = database.getDatabase();

        String placeholders = String.join(",", Arrays.stream(employeeIds).map(id -> "?").toArray(String[]::new));

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
            Table table = new Table(6, "Employee Schedule");

            while (rs.next()) {
                table.add(String.valueOf(rs.getInt("EmployeeId")));
                table.add(rs.getString("FirstName"));
                table.add(rs.getString("LastName"));
                table.add(String.valueOf(rs.getDate("Date")));
                table.add(String.valueOf(rs.getTime("StartTime")));
                table.add(String.valueOf(rs.getTime("EndTime")));
            }
            return table;
        }
    }
    /**
     * Fetch all production stations from the database.
     * @param database Database connection
     * @return Table containing all production stations
     */
    public static Table getProductionStations(Database database) throws SQLException {
        Connection conn = database.getDatabase();
        String sql = "SELECT Name, Category FROM ProductionStation";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            Table table = new Table(2, "Production Stations");

            while (rs.next()) {
                table.add(rs.getString("Name"));
                table.add(rs.getString("Category"));
            }
            return table;
        }
    }
    /**
     * Fetch all restaurants from the database.
     * @param database Database connection
     * @return Table containing all restaurants
     */
    public static Table getRestaurants(Database database) throws SQLException {
        Connection conn = database.getDatabase();
        String sql = "SELECT RestaurantId, OpeningTime, ClosingTime, DateOpened FROM Restaurant";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            Table table = new Table(4, "Restaurants");

            while (rs.next()) {
                table.add(String.valueOf(rs.getInt("RestaurantId")));
                table.add(String.valueOf(rs.getTime("OpeningTime")));
                table.add(String.valueOf(rs.getTime("ClosingTime")));
                table.add(String.valueOf(rs.getDate("DateOpened")));
            }
            return table;
        }
    }
}
