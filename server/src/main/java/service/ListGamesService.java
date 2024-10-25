package service;

import dataaccess.DataAccessException;
import model.GameData;
import model.AuthToken;
import requests.ListGamesRequest;
import results.ListGamesResult;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ListGamesService {

    public ListGamesResult listGames(ListGamesRequest request) {
        try {
            // Validate the auth token
            AuthToken authToken = DaoService.getInstance().getAuthDAO().getAuthToken(request.getAuthToken());

            if (authToken == null) {
                return new ListGamesResult(false, "Error Invalid authToken", null);
            }

            // Get the list of games from GameDAO
            List<GameData> games = DaoService.getInstance().getGameDAO().listGames();

            // Convert GameData list to String list
//            List<String> gameStrings = convertGameDataToStrings(games);

            return new ListGamesResult(true, "Games retrieved successfully", games);

        } catch (DataAccessException e) {
            return new ListGamesResult(false, "Error accessing the database", null);
        }
    }

}