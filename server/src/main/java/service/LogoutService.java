package service;

import dataaccess.DataAccessException;
import model.AuthToken;
import requests.LogoutRequest;
import results.LogoutResult;
import service.DaoService;

public class LogoutService {

    public LogoutResult logout(LogoutRequest request) {
        try {
            // Validate the auth token from the database
            AuthToken authToken = DaoService.getInstance().getAuthDAO().getAuthToken(request.getAuthToken());

            if (authToken != null) {
                // Delete the auth token from the database
                DaoService.getInstance().getAuthDAO().deleteAuthToken(request.getAuthToken());
                return new LogoutResult(true, "Logout successful!");
            } else {
                return new LogoutResult(false, "Error: Invalid authToken.");
            }

        } catch (DataAccessException e) {
            return new LogoutResult(false, "Error accessing the database.");
        }
    }
}

