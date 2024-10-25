package dataaccess;

import dataaccess.GameData;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {

    // In-memory storage for games (replace with actual database in production)
    private final List<GameData> games;

    public GameDAO() {
        this.games = new ArrayList<>();
    }

    // Method to list all games
    public List<GameData> listGames()
            throws DataAccessException {
        try {
            // Retrieve all games from the in-memory list or a database
            return new ArrayList<>(games);  // Return a copy of the list to avoid modification
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

    // Clear all games (for clear service)
    public void clearGames() throws DataAccessException {
        try {
            games.clear();
        } catch (Exception e) {
            throw new DataAccessException("Error clearing games");
        }
    }
}

