package service;

import model.AuthToken;
import model.User;
import requests.RegisterRequest;
import results.RegisterResult;

import java.util.UUID;
import java.util.logging.Logger;

public class RegisterService {
    private static final Logger LOGGER = Logger.getLogger(RegisterService.class.getName());

    public RegisterResult register(RegisterRequest request) {
        RegisterResult result = new RegisterResult();

        // Validate required fields
        if (!isValidRequest(request)) {
            LOGGER.warning("Bad request: Missing required fields");
            result.setSuccess(false);
            result.setMessage("Error: Bad Request - Missing required fields");
            result.setAuthToken(null);
            result.setUsername(null);
            return result;
        }

        // Check if user already exists
        User existingUser = DaoService.getInstance().getUserDAO().getUserByUsername(request.getUsername());
        if (existingUser != null) {
            // Log that the user already exists
            LOGGER.info("User already exists: " + request.getUsername());

            // User already exists, return forbidden response
            result.setSuccess(false);
            result.setMessage("Error: Forbidden - User already exists");
            result.setAuthToken(null);
            result.setUsername(null);
            return result;
        }

        // Register the new user
        User newUser = new User(request.getUsername(), request.getPassword(), request.getEmail());
        DaoService.getInstance().getUserDAO().addUser(newUser);

        // Create AuthToken for the user
        String authToken = UUID.randomUUID().toString();
        AuthToken token = new AuthToken(authToken, newUser.getUsername());
        DaoService.getInstance().getAuthDAO().addAuthToken(token);

        // Success
        result.setSuccess(true);
        result.setAuthToken(authToken);
        result.setUsername(newUser.getUsername());
        result.setMessage("Registration successful!");

        LOGGER.info("Registration successful for user: " + request.getUsername());

        return result;
    }

    private boolean isValidRequest(RegisterRequest request) {
        return request != null &&
                request.getUsername() != null && !request.getUsername().trim().isEmpty() &&
                request.getPassword() != null && !request.getPassword().trim().isEmpty() &&
                request.getEmail() != null && !request.getEmail().trim().isEmpty();
    }
}
