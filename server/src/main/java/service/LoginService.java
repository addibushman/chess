package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthToken;
import model.User;
import requests.LoginRequest;
import results.LoginResult;

import java.util.UUID;

public class LoginService {
    private final UserDAO userDAO = new UserDAO();
    private final AuthDAO authDAO = new AuthDAO();

    public LoginResult login(LoginRequest request) {
        // Get the user from the database
        User user = userDAO.getUserByUsername(request.getUsername());

        // Check if the user exists and the password is correct
        if (user != null && user.getPassword().equals(request.getPassword())) {
            // Create a new AuthToken
            String authToken = UUID.randomUUID().toString();
            AuthToken token = new AuthToken(authToken, user.getUsername());
            authDAO.addAuthToken(token);

            // Return a successful login result
            return new LoginResult(true, user.getUsername(), authToken, "Login successful");
        } else {
            // Return a failed login result
            return new LoginResult(false, null, null, "Error Invalid username or password");
        }
    }
}
