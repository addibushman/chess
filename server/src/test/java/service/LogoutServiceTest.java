package service;

import dataaccess.MySQLAuthTokenDAO;
import dataaccess.DataAccessException;
import model.AuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.LogoutRequest;
import results.LogoutResult;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTest {

    private LogoutService logoutService;
    private MySQLAuthTokenDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        logoutService = new LogoutService();
        authDAO = new MySQLAuthTokenDAO();

        // Clear the auth token table and set up a valid token for testing
        authDAO.clear();
        AuthToken validToken = new AuthToken("validToken", "testUser");
        authDAO.addAuthToken(validToken);
    }

    @Test
    public void testLogoutSuccess() {
        // Prepare a valid logout request
        LogoutRequest request = new LogoutRequest("validToken");

        // Call the logout service
        LogoutResult result = logoutService.logout(request);

        // Check for successful logout
        assertTrue(result.isSuccess());
        assertEquals("Logout successful!", result.getMessage());

        // Ensure the token has been deleted from the database
        assertDoesNotThrow(() -> {
            AuthToken token = authDAO.getAuthToken("validToken");
            assertNull(token, "AuthToken should be deleted");
        });
    }

    @Test
    public void testLogoutFailureInvalidToken() {
        // Prepare an invalid logout request
        LogoutRequest request = new LogoutRequest("invalidToken");

        // Call the logout service
        LogoutResult result = logoutService.logout(request);

        // Validate that logout failed with an error message
        assertFalse(result.isSuccess());
        assertEquals("Error: Invalid authToken.", result.getMessage());
    }
}
