package service;

import model.AuthToken;
import model.User;
import requests.LoginRequest;
import results.LoginResult;

import java.util.UUID;

public class LoginService {

    public LoginResult login(LoginRequest request) {
        // Get the user from the database
        User user = DaoService.getInstance().getUserDAO().getUserByUsername(request.getUsername());

        // Check if the user exists and the password is correct
        if (user != null && user.getPassword().equals(request.getPassword())) {
            // Create a new AuthToken
            String authToken = UUID.randomUUID().toString();
            AuthToken token = new AuthToken(authToken, user.getUsername());
            DaoService.getInstance().getAuthDAO().addAuthToken(token);

            // Return a successful login result
            return new LoginResult(true, user.getUsername(), authToken, "Login successful");
        } else {
            // Return a failed login result
            return new LoginResult(false, null, null, "Error Invalid username or password");
        }
    }
}
