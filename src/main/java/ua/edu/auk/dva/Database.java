package ua.edu.auk.dva;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * An abstraction of the database connection
 */
public class Database implements  AutoCloseable {
  private final Connection database;

  /**
   * Establishes a database connection using config.properties
   *
   * @throws SQLException if a connection error occurs
   */

  public Database() throws SQLException {
    Properties properties = new Properties();

    try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
      if (input == null) {
        throw new RuntimeException("config.properties not found in resources folder!");
      }
      properties.load(input);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load database config: " + e.getMessage());
    }

    String uri = properties.getProperty("db.url");
    String user = properties.getProperty("db.user");
    String pass = properties.getProperty("db.password");

    this.database = DriverManager.getConnection(uri, user, pass);
    this.database.setAutoCommit(false);
  }


  public Connection getDatabase() {
    return database;
  }

  @Override
  public void close() throws Exception {
    database.close();
  }
}
