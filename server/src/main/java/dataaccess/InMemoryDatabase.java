package dataaccess;

import model.User;
import model.AuthToken;

import java.util.HashMap;
import java.util.Map;

/**
 * Simulates an in-memory database for storing users and auth tokens.
 */
public class InMemoryDatabase {


    private Map<String, User> usersTable = new HashMap<>();


    private Map<String, AuthToken> authTokensTable = new HashMap<>();


    private static InMemoryDatabase instance;


    private InMemoryDatabase() {}

    /**
     * Gets the singleton instance of the InMemoryDatabase.
     *
     * @return InMemoryDatabase instance
     */
    public static InMemoryDatabase getInstance() {
        if (instance == null) {
            instance = new InMemoryDatabase();
        }
        return instance;
    }

    /**
     * Adds a user to the in-memory database.
     *
     * @param user The user to be added
     */
    public void addUser(User user) {
        usersTable.put(user.getUsername(), user);
    }

    /**
     * Retrieves a user by username.
     *
     * @param username The username of the user
     * @return The User object, or null if no user is found
     */
    public User getUserByUsername(String username) {
        return usersTable.get(username);
    }

    /**
     * Adds an authentication token to the in-memory database.
     *
     * @param authToken The AuthToken to be added
     */
    public void addAuthToken(AuthToken authToken) {
        authTokensTable.put(authToken.getToken(), authToken);
    }

    /**
     * Retrieves an authentication token by token string.
     *
     * @param token The token string
     * @return The AuthToken object, or null if no token is found
     */
    public AuthToken getAuthToken(String token) {
        return authTokensTable.get(token);
    }

    /**
     * Clears all data in the database (for testing purposes).
     */
    public void clearDatabase() {
        usersTable.clear();
        authTokensTable.clear();
    }
}

