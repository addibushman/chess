package dataaccess;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import model.User;


public class UserDAO {
    // In-memory storage for users
    private static HashMap<String, User> users = new HashMap<>();

    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public User getUserByUsername(String username) {
        return users.get(username);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values()); // Return a list of all users
    }

    public void clear() {
        users.clear(); // Clear all users from the in-memory store
    }
}
