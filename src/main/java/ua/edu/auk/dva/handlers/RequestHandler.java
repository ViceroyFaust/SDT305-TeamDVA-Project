package ua.edu.auk.dva.handlers;

import java.sql.SQLException;

public interface RequestHandler {

  HandlerReturnModel handleRequest(String request) throws SQLException;
}
