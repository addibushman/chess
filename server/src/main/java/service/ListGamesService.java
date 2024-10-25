package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import dataaccess.GameData;
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
            AuthDAO authDAO = new AuthDAO();
            AuthToken authToken = authDAO.getAuthToken(request.getAuthToken());

            if (authToken == null) {
                return new ListGamesResult(false, "Invalid authToken", null);
            }

            // Get the list of games from GameDAO
            GameDAO gameDAO = new GameDAO();
            List<GameData> games = gameDAO.listGames();

            // Convert GameData list to String list
            List<String> gameStrings = convertGameDataToStrings(games);

            return new ListGamesResult(true, "Games retrieved successfully", gameStrings);

        } catch (DataAccessException e) {
            return new ListGamesResult(false, "Error accessing the database", null);
        }
    }

    private List<String> convertGameDataToStrings(List<GameData> games) {
        if (games == null) {
            return new ArrayList<>();
        }

        return games.stream()
                .map(game -> String.format("Game ID: %s, Name: %s, White Player: %s, Black Player: %s",
                        game.getGameID(),
                        game.getGameName(),
                        game.getWhitePlayer() != null ? game.getWhitePlayer() : "[EMPTY]",
                        game.getBlackPlayer() != null ? game.getBlackPlayer() : "[EMPTY]"))
                .collect(Collectors.toList());
    }
}