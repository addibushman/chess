package service;

import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import dataaccess.User;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        userDAO = new UserDAO();
    }

    /**
     * Registers a new user.
     *
     * @param user the user to register
     * @throws DataAccessException if there is an error during the database operation
     */
    public void registerUser(User user) throws DataAccessException {
        try {
            // Call DAO to register the user, passing the fields separately
            userDAO.addUser(user.getUsername(), user.getPassword(), user.getEmail());
        } catch (DataAccessException e) {
            throw new DataAccessException("Error registering the user: " + e.getMessage());
        }
    }

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return the user if found, or null if not
     * @throws DataAccessException if there is an error during the database operation
     */
    public User findUser(String username) throws DataAccessException {
        try {
            return userDAO.find(username);
        } catch (DataAccessException e) {
            throw new DataAccessException("Error finding the user: " + e.getMessage());
        }
    }
}
