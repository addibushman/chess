package dataaccess;

import model.GameData;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLGameDAO {

    public void addGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO games (game_id, game_name, white_player_id, black_player_id) VALUES (?, ?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, game.getGameID());
            stmt.setString(2, game.getGameName());
            stmt.setString(3, game.getWhiteUsername());
            stmt.setString(4, game.getBlackUsername());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error adding game to the database: " + e.getMessage());
        }
    }
    public String createGame(GameData gameData) throws DataAccessException {
        String sql = "INSERT INTO games (game_name, white_player_id, black_player_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, gameData.getGameName());
            stmt.setString(2, gameData.getWhiteUsername());
            stmt.setString(3, gameData.getBlackUsername());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Creating game failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return String.valueOf(generatedKeys.getInt(1));
                } else {
                    throw new DataAccessException("Creating game failed, no ID obtained.");
                }
            }


        } catch (SQLException e) {
            throw new DataAccessException("Error creating game in the database");
        }
    }

    public GameData getGameByID(String gameID) throws DataAccessException {
        String sql = "SELECT * FROM games WHERE game_id = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gameID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new GameData(
                        rs.getString("game_id"),
                        rs.getString("game_name"),
                        rs.getString("white_player_id"),
                        rs.getString("black_player_id")
                );
            }
            return null;

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game from the database: " + e.getMessage());
        }
    }

    public List<GameData> listGames() throws DataAccessException {
        String sql = "SELECT * FROM games;";
        List<GameData> games = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                GameData game = new GameData(
                        rs.getString("game_id"),
                        rs.getString("game_name"),
                        rs.getString("white_player_id"),
                        rs.getString("black_player_id")
                );
                games.add(game);
            }
            return games;

        } catch (SQLException e) {
            throw new DataAccessException("Error listing games from the database: " + e.getMessage());
        }
    }
    public void updateGame(GameData gameData) throws DataAccessException {
        String sql = "UPDATE games SET white_player_id = ?, black_player_id = ? WHERE game_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gameData.getWhiteUsername());
            stmt.setString(2, gameData.getBlackUsername());
            stmt.setInt(3, Integer.parseInt(gameData.getGameID()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Updating game failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error updating game in the database");
        }
    }
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing users table");
        }
    }
    public GameData getGameByName(String gameName) throws DataAccessException {
        String sql = "SELECT * FROM games WHERE game_name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gameName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new GameData(
                            rs.getString("game_id"),
                            rs.getString("game_name"),
                            rs.getString("white_player_id"),
                            rs.getString("black_player_id")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game by name from the database: " + e.getMessage());
        }
    }

}

