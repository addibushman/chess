package service;

import dataaccess.MySQLAuthTokenDAO;  // Use the implementation directly
import dataaccess.DataAccessException;
import model.AuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.LogoutRequest;
import results.LogoutResult;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTest {

    private LogoutService logoutService;
    private MySQLAuthTokenDAO authDAO;  // Use specific implementation

    @BeforeEach
    public void setUp() throws DataAccessException {
        logoutService = new LogoutService();

        // Use MySQLAuthTokenDAO explicitly
        authDAO = new MySQLAuthTokenDAO();

        // Clear the database and set up a valid token
        authDAO.clear();
        AuthToken validToken = new AuthToken("validToken", "testUser");
        authDAO.addAuthToken(validToken);
    }

    @Test
    public void testLogoutSuccess() {
        // Prepare the logout request with a valid token
        LogoutRequest request = new LogoutRequest("validToken");

        // Call the logout service
        LogoutResult result = logoutService.logout(request);

        // Validate that the logout was successful
        assertTrue(result.isSuccess());
        assertEquals("Logout successful!", result.getMessage());

        // Check if the token has been removed from the database
        try {
            assertNull(authDAO.getAuthToken("validToken"));
        } catch (DataAccessException e) {
            fail("Exception occurred while checking token removal: " + e.getMessage());
        }
    }

    @Test
    public void testLogoutFailureInvalidToken() {
        // Prepare the logout request with an invalid token
        LogoutRequest request = new LogoutRequest("invalidToken");

        // Call the logout service
        LogoutResult result = logoutService.logout(request);

        // Validate that the logout failed
        assertFalse(result.isSuccess());
        assertEquals("Error: Invalid authToken.", result.getMessage());
    }
}
