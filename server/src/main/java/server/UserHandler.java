package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.UserService;
import dataaccess.DataAccessException;
import dataaccess.User;

import java.io.IOException;
import java.io.OutputStream;

public class UserHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            // Handle user registration logic
            UserService userService = new UserService();
            //these are just placeholders for now right?
            String username = "testUser";
            String password = "testPass";
            String email = "testEmail";

            User newUser = new User(username, password, email); // Create a User object

            try {
                // Pass the User object to the UserService
                userService.registerUser(newUser);
                exchange.sendResponseHeaders(200, 0); // Success response
            } catch (DataAccessException e) {
                exchange.sendResponseHeaders(500, 0); // Internal server error
            }

            OutputStream os = exchange.getResponseBody();
            os.close();
        }
    }
}
