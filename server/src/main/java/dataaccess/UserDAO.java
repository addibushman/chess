package dataaccess;

import model.User;

import java.util.HashMap;
import java.util.Map;

public class UserDAO {
    private static Map<String, User> users = new HashMap<>();

    static {
        users.put("testUser", new User("testUser", "testPass", "testUser@example.com"));
    }

    public User getUser(String username) {
        return users.get(username);
    }
}
