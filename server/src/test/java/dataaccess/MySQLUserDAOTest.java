package dataaccess;

import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class MySQLUserDAOTest {
    private MySQLUserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new MySQLUserDAO();
        userDAO.clear();
    }

    @Test
    public void testAddUserSuccess() {
        User user = new User("testUser", "password", "test@mail.com");
        assertDoesNotThrow(() -> userDAO.addUser(user));

        User retrievedUser = assertDoesNotThrow(() -> userDAO.getUserByUsername("testUser"));
        assertNotNull(retrievedUser);
        assertEquals("test@mail.com", retrievedUser.getEmail());
    }

    @Test
    public void testAddUserDuplicate() {
        User user = new User("duplicateUser", "password", "test@mail.com");
        assertDoesNotThrow(() -> userDAO.addUser(user));

        assertThrows(DataAccessException.class, () -> userDAO.addUser(user), "Expected duplicate user exception");
    }

    @Test
    public void testGetUserByUsernameSuccess() {
        User user = new User("existingUser", "password", "existing@mail.com");
        assertDoesNotThrow(() -> userDAO.addUser(user));

        User retrievedUser = assertDoesNotThrow(() -> userDAO.getUserByUsername("existingUser"));
        assertNotNull(retrievedUser);
        assertEquals("existingUser", retrievedUser.getUsername());
        assertEquals("existing@mail.com", retrievedUser.getEmail());
    }

    @Test
    public void testGetUserByUsernameNonExistent() {
        User user = assertDoesNotThrow(() -> userDAO.getUserByUsername("nonExistentUser"));
        assertNull(user, "User should not exist");
    }

    @Test
    public void testClearSuccess() {
        User user = new User("userToClear", "password", "clear@mail.com");
        assertDoesNotThrow(() -> userDAO.addUser(user));

        assertDoesNotThrow(() -> userDAO.clear());
        List<User> allUsers = assertDoesNotThrow(() -> userDAO.getAllUsers());
        assertTrue(allUsers.isEmpty(), "Users table should be empty after clear");
    }

    @Test
    public void testClearEmptyTable() {
        assertDoesNotThrow(() -> userDAO.clear());


        List<User> allUsers = assertDoesNotThrow(() -> userDAO.getAllUsers());
        assertTrue(allUsers.isEmpty(), "Users table should remain empty after clear on empty table");
    }

    @Test
    public void testGetAllUsersSuccess() {
        User user1 = new User("user1", "password1", "user1@mail.com");
        User user2 = new User("user2", "password2", "user2@mail.com");
        assertDoesNotThrow(() -> userDAO.addUser(user1));
        assertDoesNotThrow(() -> userDAO.addUser(user2));

        List<User> allUsers = assertDoesNotThrow(() -> userDAO.getAllUsers());
        assertEquals(2, allUsers.size(), "getAllUsers should return 2 users");
    }

    @Test
    public void testGetAllUsersEmpty() {
        List<User> allUsers = assertDoesNotThrow(() -> userDAO.getAllUsers());
        assertTrue(allUsers.isEmpty(), "getAllUsers should return an empty list if no users exist");
    }
}
