package dataaccess;

import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class MySQLUserDAO {

    public void addUser(User user) throws DataAccessException {
        String sql = "INSERT INTO USERS (username, hashed_password, email) VALUES (?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            stmt.setString(1, user.getUsername());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.getEmail());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error adding user to the database: " + e.getMessage());
        }
    }

    public User getUserByUsername(String username) throws DataAccessException {
        String sql = "SELECT * FROM USERS WHERE username = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("username"),
                        rs.getString("hashed_password"),
                        rs.getString("email")
                );
            }
            return null;

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving user from the database: " + e.getMessage());
        }
    }
}
