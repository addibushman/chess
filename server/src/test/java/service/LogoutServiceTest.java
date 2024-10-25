package service;

import dataaccess.AuthDAO;
import model.AuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.LogoutRequest;
import results.LogoutResult;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTest {

    private LogoutService logoutService;

    @BeforeEach
    public void setUp() {
        // Initialize the LogoutService
        logoutService = new LogoutService();

        // Populate the in-memory database with some data for testing
        AuthDAO authDAO = new AuthDAO();
        authDAO.addAuthToken(new AuthToken("validToken", "testUser"));
    }

    @Test
    public void testLogoutSuccess() {
        // Create a valid logout request
        LogoutRequest request = new LogoutRequest("validToken");

        // Call the logout service
        LogoutResult result = logoutService.logout(request);

        // Verify that the result indicates success
        assertTrue(result.isSuccess());
        assertEquals("Logout successful!", result.getMessage());
    }

    @Test
    public void testLogoutFailureInvalidToken() {
        // Create a logout request with an invalid token
        LogoutRequest request = new LogoutRequest("invalidToken");

        // Call the logout service
        LogoutResult result = logoutService.logout(request);

        // Verify that the result indicates failure
        assertFalse(result.isSuccess());
        assertEquals("Error: Invalid authToken.", result.getMessage());
    }
}

