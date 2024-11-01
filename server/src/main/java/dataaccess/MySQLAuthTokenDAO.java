package dataaccess;

import model.AuthToken;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MySQLAuthTokenDAO {

    public void addAuthToken(AuthToken token) throws DataAccessException {
        String sql = "INSERT INTO auth_tokens (auth_token, username) VALUES (?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token.getToken());
            stmt.setString(2, token.getUsername());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error adding auth token to the database: " + e.getMessage());
        }
    }

    public AuthToken getAuthToken(String authToken) throws DataAccessException {
        String sql = "SELECT * FROM auth_tokens WHERE auth_token = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new AuthToken(
                        rs.getString("auth_token"),
                        rs.getString("username")
                );
            }
            return null;

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving auth token from the database: " + e.getMessage());
        }
    }

    public void deleteAuthToken(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth_tokens WHERE auth_token = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token from the database: " + e.getMessage());
        }
    }
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM auth_tokens";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing users table");
        }
    }

    public List<AuthToken> getAllAuthTokens() throws DataAccessException{
        String sql = "SELECT * FROM auth_tokens;";
        List<AuthToken> authTokens = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                AuthToken token = new AuthToken(
                        rs.getString("auth_token"),
                        rs.getString("username")
                );
                authTokens.add(token);
            }
            return authTokens;

        } catch (SQLException e) {
            throw new DataAccessException("Error listing users from the database: " + e.getMessage());
        }
    }

}

