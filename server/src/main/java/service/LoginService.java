package service;

import dataaccess.DataAccessException;
import model.AuthToken;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import requests.LoginRequest;
import results.LoginResult;

import java.util.UUID;

public class LoginService {

    public LoginResult login(LoginRequest request) {
        try {
            User user = DaoService.getInstance().getUserDAO().getUserByUsername(request.getUsername());

            // Check if user exists and password matches the stored hash
            if (user != null && BCrypt.checkpw(request.getPassword(), user.getPassword())) {
                String authToken = UUID.randomUUID().toString();
                AuthToken token = new AuthToken(authToken, user.getUsername());
                DaoService.getInstance().getAuthDAO().addAuthToken(token);

                return new LoginResult(true, user.getUsername(), authToken, "Login successful");
            } else {
                return new LoginResult(false, null, null, "Error Invalid username or password");
            }

        } catch (DataAccessException e) {
            return new LoginResult(false, null, null, "Error accessing the database");
        }
    }
}
