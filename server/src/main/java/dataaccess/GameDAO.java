package dataaccess;

import java.sql.*;

/**
 * Manages data access for game-related data.
 */
public class GameDAO {
    private final Connection conn;

    public GameDAO(Connection conn) {
        this.conn = conn;
    }

    public void createGame(String gameId) throws DataAccessException {
        String sql = "INSERT INTO Games (game_id) VALUES(?);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, gameId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating game in database");
        }
    }

    public void clearGames() throws DataAccessException {
        String sql = "DELETE FROM Games;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing games from the database");
        }
    }
}

