package service;

import dataaccess.DataAccessException;
import model.GameData;
import model.AuthToken;
import requests.ListGamesRequest;
import results.ListGamesResult;

import java.util.List;

public class ListGamesService {

    public ListGamesResult listGames(ListGamesRequest request) {
        try {
            AuthToken authToken = DaoService.getInstance().getAuthDAO().getAuthToken(request.getAuthToken());
            if (authToken == null) {
                return new ListGamesResult(false, "Error Invalid authToken", null);
            }

            List<GameData> games = DaoService.getInstance().getGameDAO().listGames();
            return new ListGamesResult(true, "Games retrieved successfully", games);

        } catch (DataAccessException e) {
            return new ListGamesResult(false, "Error accessing the database", null);
        }
    }
}
