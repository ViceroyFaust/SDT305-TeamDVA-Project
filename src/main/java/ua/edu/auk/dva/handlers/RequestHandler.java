package ua.edu.auk.dva.handlers;

import ua.edu.auk.dva.Table;

import java.sql.SQLException;

public interface RequestHandler {
    Table handleRequest(String request, String[] args) throws SQLException;
}
