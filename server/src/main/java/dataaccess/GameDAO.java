package dataaccess;

import model.GameData;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {

    private final List<GameData> games;
    private int nextGameID = 1;

    public GameDAO() {
        this.games = new ArrayList<>();
    }

    public List<GameData> listGames() throws DataAccessException {
        try {
            return new ArrayList<>(games);  // Return a copy of the list
        } catch (Exception e) {
            throw new DataAccessException("Error retrieving games");
        }
    }

    public String createGame(GameData gameData) throws DataAccessException {
        try {

            String gameID = String.valueOf(nextGameID++);

            gameData.setGameID(gameID);
            games.add(gameData);
            return gameID;
        } catch (Exception e) {
            throw new DataAccessException("Error creating game");
        }
    }

    public void clear() throws DataAccessException {
        try {
            games.clear();
            nextGameID = 1;
        } catch (Exception e) {
            throw new DataAccessException("Error clearing games");
        }
    }

//    public GameData getGame(String gameID) throws DataAccessException {
//        for (GameData game : games) {
//            if (game.getGameID().equals(gameID)) {
//                return game;
//            }
//        }
//        throw new DataAccessException("Game not found");
//    }


    public void updateGame(GameData updatedGame) throws DataAccessException {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).getGameID().equals(updatedGame.getGameID())) {
                games.set(i, updatedGame);
                return;
            }
        }
        throw new DataAccessException("Error updating game");
    }
}


