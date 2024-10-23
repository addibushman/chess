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

    public UserDAO() {
        this.conn = null;
    }


    public void addUser(String username, String password, String email) throws DataAccessException {
        String sql = "INSERT INTO Users (username, password, email) VALUES(?,?,?);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email); // Include email in the insertion
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting user into database");
        }
    }

    public User find(String username) throws DataAccessException {
        User user = null;
        ResultSet rs = null;
        String sql = "SELECT username, password, email FROM Users WHERE username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                String testUsername = rs.getString("username");
                String testPassword = rs.getString("password");
                String testEmail = rs.getString("email");
                user = new User(testUsername, testPassword, testEmail);
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
        return user;
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
