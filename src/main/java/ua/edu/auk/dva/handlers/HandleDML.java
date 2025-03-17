package ua.edu.auk.dva.handlers;


import java.sql.SQLException;
import ua.edu.auk.dva.Database;
import ua.edu.auk.dva.Table;
import ua.edu.auk.dva.View;

public class HandleDML implements RequestHandler {

  private final Database database;
  private final View view;

  public HandleDML(Database db, View view) {
    this.database = db;
    this.view = view;
  }

  @Override
  public HandlerReturnModel handleRequest(String request, String[] args) throws SQLException {
    System.out.println("Handling DML request: " + request);
    return new HandlerReturnModel(false);
  }
}
