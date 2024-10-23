package dataaccess;

import model.User;

import java.util.HashMap;
import java.util.Map;

public class UserData {
    private Map<String, User> usersTable = new HashMap<>();

    private static UserData instance;

    private UserData() {}

    public static UserData getInstance() {
        if (instance == null) {
            instance = new UserData();
        }
        return instance;
    }

    public void addUser(User user) {
        usersTable.put(user.getUsername(), user);
    }

    public User getUserByUsername(String username) {
        return usersTable.get(username);
    }

    public void clearUserData() {
        usersTable.clear();
    }
}

