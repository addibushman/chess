package dataaccess;

import model.GameData;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLUserDAO {

    public void addUser(User user) throws DataAccessException {
        String sql = "INSERT INTO USERS (username, hashed_password, email) VALUES (?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

//            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error adding user to the database: ");
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
            throw new DataAccessException("Error retrieving user from the database: ");
        }
    }
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing users table");
        }
    }
    public List<User> getAllUsers() throws DataAccessException{
        String sql = "SELECT * FROM users;";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
                users.add(user);
            }
            return users;

        } catch (SQLException e) {
            throw new DataAccessException("Error listing users from the database: " + e.getMessage());
        }
    }
}
