package dataaccess;

import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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

        assertDoesNotThrow(() -> {
            User retrievedUser = userDAO.getUserByUsername("testUser");
            assertNotNull(retrievedUser);
            assertEquals("test@mail.com", retrievedUser.getEmail());
        });
    }

    @Test
    public void testAddUserDuplicate() {
        User user = new User("duplicateUser", "password", "test@mail.com");
        assertDoesNotThrow(() -> userDAO.addUser(user));

        assertThrows(DataAccessException.class, () -> userDAO.addUser(user), "Expected duplicate user exception");
    }

    @Test
    public void testGetNonExistentUser() {
        assertDoesNotThrow(() -> {
            User user = userDAO.getUserByUsername("nonExistentUser");
            assertNull(user, "User should not exist");
        });
    }
}
