package service;

import dataaccess.AuthDAO;
import model.AuthToken;
import requests.LogoutRequest;
import results.LogoutResult;

public class LogoutService {
    private final AuthDAO authDAO = new AuthDAO();

    public LogoutResult logout(LogoutRequest request) {
        AuthToken authToken = authDAO.getAuthToken(request.getAuthToken());

        if (authToken != null) {
            authDAO.deleteAuthToken(request.getAuthToken());
            return new LogoutResult(true, "Logout successful!");
        } else {
            return new LogoutResult(false, "Error: Invalid authToken.");
        }
    }
}
