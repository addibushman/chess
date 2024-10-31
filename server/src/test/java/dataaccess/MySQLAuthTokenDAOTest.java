package dataaccess;

import model.AuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLAuthTokenDAOTest {
    private MySQLAuthTokenDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new MySQLAuthTokenDAO();
        authDAO.clear();
    }

    @Test
    public void testAddAuthTokenSuccess() {
        AuthToken token = new AuthToken("validToken", "testUser");
        assertDoesNotThrow(() -> authDAO.addAuthToken(token));

        AuthToken retrievedToken = assertDoesNotThrow(() -> authDAO.getAuthToken("validToken"));
        assertNotNull(retrievedToken);
        assertEquals("testUser", retrievedToken.getUsername());
    }

    @Test
    public void testAddDuplicateAuthToken() {
        AuthToken token = new AuthToken("duplicateToken", "testUser");
        assertDoesNotThrow(() -> authDAO.addAuthToken(token));

        assertThrows(DataAccessException.class, () -> authDAO.addAuthToken(token), "Expected duplicate token exception");
    }

    @Test
    public void testGetNonExistentAuthToken() {
        assertDoesNotThrow(() -> {
            AuthToken token = authDAO.getAuthToken("nonExistentToken");
            assertNull(token, "AuthToken should not exist");
        });
    }

    @Test
    public void testDeleteAuthTokenSuccess() {
        AuthToken token = new AuthToken("validToken", "testUser");
        assertDoesNotThrow(() -> authDAO.addAuthToken(token));

        assertDoesNotThrow(() -> authDAO.deleteAuthToken("validToken"));

        AuthToken deletedToken = assertDoesNotThrow(() -> authDAO.getAuthToken("validToken"));
        assertNull(deletedToken, "AuthToken should be deleted");
    }
}

