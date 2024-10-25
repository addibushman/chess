package service;

import dataaccess.DataAccessException;
import model.AuthToken;
import model.GameData;
import requests.CreateGameRequest;
import results.CreateGameResult;

public class CreateGameService {

    public CreateGameResult createGame(CreateGameRequest request) {
        try {
            // Validate auth token
            AuthToken authToken = DaoService.getInstance().getAuthDAO().getAuthToken(request.getAuthToken());
            if (authToken == null) {
                return new CreateGameResult(false, "Error Invalid auth token", null);
            }

            // Create new game data with String-based game ID
            GameData newGame = new GameData(null, request.getGameName(), null, null);
            String gameID = DaoService.getInstance().getGameDAO().createGame(newGame);

            return new CreateGameResult(true, "Game created successfully", gameID);

        } catch (DataAccessException e) {
            return new CreateGameResult(false, "Error creating game", null);
        }
    }
}
