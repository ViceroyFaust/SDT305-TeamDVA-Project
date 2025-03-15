package ua.edu.auk.dva.handlers;

public class HandleDML implements RequestHandler {
    @Override
    public void handleRequest(String request, String[] args) {
        System.out.println("Handling DML request: " + request);
    }
}
