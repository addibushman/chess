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

        logoutService = new LogoutService();


        AuthDAO authDAO = new AuthDAO();
        authDAO.addAuthToken(new AuthToken("validToken", "testUser"));
    }

    @Test
    public void testLogoutSuccess() {

        LogoutRequest request = new LogoutRequest("validToken");


        LogoutResult result = logoutService.logout(request);


        assertTrue(result.isSuccess());
        assertEquals("Logout successful!", result.getMessage());
    }

    @Test
    public void testLogoutFailureInvalidToken() {

        LogoutRequest request = new LogoutRequest("invalidToken");


        LogoutResult result = logoutService.logout(request);


        assertFalse(result.isSuccess());
        assertEquals("Error: Invalid authToken.", result.getMessage());
    }
}

