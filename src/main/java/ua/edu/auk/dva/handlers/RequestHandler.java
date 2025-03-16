package ua.edu.auk.dva.handlers;

import java.sql.SQLException;
import ua.edu.auk.dva.Table;

public interface RequestHandler {

  Table handleRequest(String request, String[] args) throws SQLException;
}
