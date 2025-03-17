package ua.edu.auk.dva.handlers;

import java.sql.SQLException;
import ua.edu.auk.dva.Table;

public interface RequestHandler {

  HandlerReturnModel handleRequest(String request) throws SQLException;
}
