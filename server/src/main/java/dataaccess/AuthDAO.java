package dataaccess;

import java.sql.*;

/**
 * Manages data access for authentication tokens (e.g., for session management).
 */
public class AuthDAO {
    private final Connection conn;

    public AuthDAO(Connection conn) {
        this.conn = conn;
    }

    public void addAuthToken(String authToken, String username) throws DataAccessException {
        String sql = "INSERT INTO AuthTokens (authToken, username) VALUES(?,?);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting auth token into database");
        }
    }

    public String findUsernameByAuthToken(String authToken) throws DataAccessException {
        String username = null;
        ResultSet rs = null;
        String sql = "SELECT username FROM AuthTokens WHERE authToken = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            rs = stmt.executeQuery();
            if (rs.next()) {
                username = rs.getString("username");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding auth token in the database");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return username;
    }

    public void clearAuthTokens() throws DataAccessException {
        String sql = "DELETE FROM AuthTokens;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing auth tokens from the database");
        }
    }
}
