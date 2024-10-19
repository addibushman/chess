package dataaccess;

import java.sql.*;

/**
 * Manages data access for users (like registration and login)
 */
public class UserDAO {
    private final Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    public void addUser(String username, String password) throws DataAccessException {
        String sql = "INSERT INTO Users (username, password) VALUES(?,?);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting user into database");
        }
    }

    public String findPasswordByUsername(String username) throws DataAccessException {
        String password = null;
        ResultSet rs = null;
        String sql = "SELECT password FROM Users WHERE username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                password = rs.getString("password");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding user in the database");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return password;
    }

    public void clearUsers() throws DataAccessException {
        String sql = "DELETE FROM Users;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing users from the database");
        }
    }
}
