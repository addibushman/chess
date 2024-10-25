package dataaccess;

import dataaccess.GameData;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {

    // In-memory storage for games (replace with actual database in production)
    private final List<GameData> games;
    private int nextGameID = 1;

    public GameDAO() {
        this.games = new ArrayList<>();
    }

    // Method to list all games
    public List<GameData> listGames() throws DataAccessException {
        try {
            return new ArrayList<>(games);  // Return a copy of the list
        } catch (Exception e) {
            throw new DataAccessException("Error retrieving games");
        }
    }

    // Method to add a new game
    public void addGame(GameData game) throws DataAccessException {
        try {
            games.add(game);
        } catch (Exception e) {
            throw new DataAccessException("Error adding game");
        }
    }

    // Method to create a new game
    public String createGame(GameData gameData) throws DataAccessException {
        try {
            // Convert the nextGameID to String and set it as game ID
            String gameID = String.valueOf(nextGameID++);
//            gameData = new GameData(gameID, gameData.getGameName(), null, null);
            gameData.setGameID(gameID);
            games.add(gameData);
            return gameID;
        } catch (Exception e) {
            throw new DataAccessException("Error creating game");
        }
    }

    // Clear all games (for clear service)
    public void clearGames() throws DataAccessException {
        try {
            games.clear();
            nextGameID = 1; // Reset game ID counter
        } catch (Exception e) {
            throw new DataAccessException("Error clearing games");
        }
    }
}


