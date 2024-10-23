package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import model.AuthToken;
import model.User;
import requests.RegisterRequest;
import results.RegisterResult;

import java.util.UUID;

public class RegisterService {

    private final UserDAO userDAO = new UserDAO();
    private final AuthDAO authDAO = new AuthDAO();

    public RegisterResult register(RegisterRequest request) {
        RegisterResult result = new RegisterResult();

        // Check if user already exists
        User existingUser = userDAO.getUserByUsername(request.getUsername());
        if (existingUser != null) {
            result.setSuccess(false);
            result.setMessage("User already exists.");
            return result;
        }

        // Register the new user
        User newUser = new User(request.getUsername(), request.getPassword(), request.getEmail());
        userDAO.addUser(newUser);

        // Create AuthToken for the user
        String authToken = UUID.randomUUID().toString();
        AuthToken token = new AuthToken(authToken, newUser.getUsername());
        authDAO.addAuthToken(token);

        // Success
        result.setSuccess(true);
        result.setAuthToken(authToken);
        result.setUsername(newUser.getUsername());
        result.setMessage("Registration successful!");

        return result;
    }
}
