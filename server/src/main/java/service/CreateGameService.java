package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthToken;
import dataaccess.GameData;
import requests.CreateGameRequest;
import results.CreateGameResult;

public class CreateGameService {

    public CreateGameResult createGame(CreateGameRequest request) {
        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();

        try {
            // Validate auth token
            AuthToken authToken = authDAO.getAuthToken(request.getAuthToken());
            if (authToken == null) {
                return new CreateGameResult(false, "Error Invalid auth token", null);
            }

            // Create new game data with String-based game ID
            GameData newGame = new GameData(null, request.getGameName(), null, null);
            String gameID = gameDAO.createGame(newGame);

            return new CreateGameResult(true, "Game created successfully", gameID);

        } catch (DataAccessException e) {
            return new CreateGameResult(false, "Error creating game", null);
        }
    }
}
