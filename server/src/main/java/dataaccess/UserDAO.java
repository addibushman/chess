package dataaccess;

import model.User;

public class UserDAO {

    private final UserData userData = UserData.getInstance();

    public void addUser(User user) {
        userData.addUser(user);
    }

    public User getUserByUsername(String username) {
        return userData.getUserByUsername(username);
    }

    public void clearUsers() {
        userData.clearUserData();
    }
}
