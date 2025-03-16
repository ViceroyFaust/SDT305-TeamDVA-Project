package ua.edu.auk.dva;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * An abstraction of the database connection
 */
public class Database implements AutoCloseable {

  private final Connection database;

  /**
   * A simple abstraction of a database connection
   *
   * @param URI  the URI/URL of the database
   * @param user the username of the database
   * @param pass the password of the user
   * @throws SQLException if an SQL error occurs
   */
  public Database(String URI, String user, String pass) throws SQLException {
    database = DriverManager.getConnection(URI, user, pass);
    database.setAutoCommit(false);
  }

  public Connection getDatabase() {
    return database;
  }

  @Override
  public void close() throws Exception {
    database.close();
  }
}
