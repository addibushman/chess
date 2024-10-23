package service;

import dataaccess.GameDAO;
import dataaccess.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Service class that handles game creation and game logic.
 */
public class GameService {

    private static final String DB_URL = "jdbc:sqlite:your_database.db";

    public boolean createGame(String gameId) throws DataAccessException {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
            conn.setAutoCommit(false);

            GameDAO gameDAO = new GameDAO(conn);
            gameDAO.createGame(gameId);

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    throw new DataAccessException("Error rolling back transaction: " + rollbackEx.getMessage());
                }
            }
            throw new DataAccessException("Error creating the game: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    throw new DataAccessException("Error closing the connection: " + closeEx.getMessage());
                }
            }
        }
    }
}

