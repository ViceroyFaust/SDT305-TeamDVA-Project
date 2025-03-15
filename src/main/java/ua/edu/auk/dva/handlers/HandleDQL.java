package ua.edu.auk.dva.handlers;

public class HandleDQL implements  RequestHandler{
    @Override
    public void handleRequest(String request, String[] args) {
        System.out.println("Handling DQL request: " + request);
    }
}
