package dataaccess;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import model.User;


public class UserDAO {

    private static HashMap<String, User> users = new HashMap<>();

    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public User getUserByUsername(String username) {
        return users.get(username);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public void clear() {
        users.clear();
    }
}
