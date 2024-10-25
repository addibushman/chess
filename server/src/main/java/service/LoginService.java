package service;

import model.AuthToken;
import model.User;
import requests.LoginRequest;
import results.LoginResult;

import java.util.UUID;

public class LoginService {

    public LoginResult login(LoginRequest request) {
        User user = DaoService.getInstance().getUserDAO().getUserByUsername(request.getUsername());

        if (user != null && user.getPassword().equals(request.getPassword())) {
            String authToken = UUID.randomUUID().toString();
            AuthToken token = new AuthToken(authToken, user.getUsername());
            DaoService.getInstance().getAuthDAO().addAuthToken(token);

            return new LoginResult(true, user.getUsername(), authToken, "Login successful");
        } else {
            return new LoginResult(false, null, null, "Error Invalid username or password");
        }
    }
}
