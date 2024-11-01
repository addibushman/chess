package service;

import dataaccess.DataAccessException;
import model.AuthToken;
import requests.LogoutRequest;
import results.LogoutResult;

public class LogoutService {

    public LogoutResult logout(LogoutRequest request) {
        try {
            AuthToken authToken = DaoService.getInstance().getAuthDAO().getAuthToken(request.getAuthToken());

            if (authToken != null) {
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

