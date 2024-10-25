package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthToken;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import results.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {

    private LoginService loginService;

    @BeforeEach
    public void setUp() {
        loginService = new LoginService();

        UserDAO userDAO = new UserDAO();
        userDAO.addUser(new User("testUser", "password", "email@test.com"));

        AuthDAO authDAO = new AuthDAO();
        authDAO.addAuthToken(new AuthToken("validToken", "testUser"));
    }

    @Test
    public void testLoginSuccess() {
        LoginRequest request = new LoginRequest("testUser", "password");

        LoginResult result = loginService.login(request);

        assertTrue(result.isSuccess());
        assertEquals("testUser", result.getUsername());
        assertNotNull(result.getAuthToken());
        assertEquals("Login successful", result.getMessage());
    }

    @Test
    public void testLoginFailureInvalidPassword() {
        LoginRequest request = new LoginRequest("testUser", "wrongPassword");


        LoginResult result = loginService.login(request);

        assertFalse(result.isSuccess());
        assertNull(result.getAuthToken());
        assertEquals("Error Invalid username or password", result.getMessage());
    }

    @Test
    public void testLoginFailureNonExistentUser() {

        LoginRequest request = new LoginRequest("nonExistentUser", "password");


        LoginResult result = loginService.login(request);

        assertFalse(result.isSuccess());
        assertNull(result.getAuthToken());
        assertEquals("Error Invalid username or password", result.getMessage());
    }
}

