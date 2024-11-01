package dataaccess;

import model.AuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    public void testDeleteNonExistentAuthToken() {
        assertDoesNotThrow(() -> authDAO.deleteAuthToken("nonExistentToken"), "Deleting a non-existent token should not throw an exception");
    }

    @Test
    public void testGetAllAuthTokensSuccess() throws DataAccessException {
        AuthToken token1 = new AuthToken("token1", "user1");
        AuthToken token2 = new AuthToken("token2", "user2");
        authDAO.addAuthToken(token1);
        authDAO.addAuthToken(token2);

        List<AuthToken> tokens = assertDoesNotThrow(() -> authDAO.getAllAuthTokens());
        assertEquals(2, tokens.size(), "There should be two auth tokens in the database");

        assertTrue(tokens.stream().anyMatch(t -> "token1".equals(t.getToken()) && "user1".equals(t.getUsername())));
        assertTrue(tokens.stream().anyMatch(t -> "token2".equals(t.getToken()) && "user2".equals(t.getUsername())));
    }

    @Test
    public void testGetAllAuthTokensEmpty() throws DataAccessException {
        List<AuthToken> tokens = assertDoesNotThrow(() -> authDAO.getAllAuthTokens());
        assertTrue(tokens.isEmpty(), "There should be no auth tokens in an empty database");
    }

    @Test
    public void testClearAuthTokens() throws DataAccessException {
        AuthToken token1 = new AuthToken("token1", "user1");
        AuthToken token2 = new AuthToken("token2", "user2");
        authDAO.addAuthToken(token1);
        authDAO.addAuthToken(token2);

        assertDoesNotThrow(() -> authDAO.clear());

        List<AuthToken> tokens = assertDoesNotThrow(() -> authDAO.getAllAuthTokens());
        assertTrue(tokens.isEmpty(), "Auth tokens should be cleared from the database");
    }
}


