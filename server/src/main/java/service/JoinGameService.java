package service;

import dataaccess.DataAccessException;
import model.AuthToken;
import model.GameData;
import requests.JoinGameRequest;
import results.JoinGameResult;

public class JoinGameService {

    public JoinGameResult joinGame(JoinGameRequest request) {
        try {
            // Validate the auth token
            AuthToken authToken = DaoService.getInstance().getAuthDAO().getAuthToken(request.getAuthToken());
            if (authToken == null) {
                return new JoinGameResult(false, "Error Invalid auth token");
            }

            GameData game = DaoService.getInstance().getGameDAO().getGame(request.getGameID());
            if (game == null) {
                return new JoinGameResult(false, "Game not found");
            }

            String playerColor = request.getPlayerColor();
            if ("WHITE".equalsIgnoreCase(playerColor) && game.getWhiteUsername() == null) {
                game.setWhiteUsername(authToken.getUsername());
            } else if ("BLACK".equalsIgnoreCase(playerColor) && game.getBlackUsername() == null) {
                game.setBlackUsername(authToken.getUsername());
            } else if ("WHITE".equalsIgnoreCase(playerColor) && game.getWhiteUsername() != null) {
                return new JoinGameResult(false, "Error Color already taken");
            } else if ("BLACK".equalsIgnoreCase(playerColor) && game.getBlackUsername() != null) {
                return new JoinGameResult(false, "Error Color already taken");
            } else {
                return new JoinGameResult(false, "Error Invalid");
            }

            DaoService.getInstance().getGameDAO().updateGame(game);

            return new JoinGameResult(true, "Successfully joined the game");

        } catch (DataAccessException e) {
            return new JoinGameResult(false, "Error joining the game");
        }
    }
}
