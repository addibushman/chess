package service;

import dataaccess.DataAccessException;
import model.AuthToken;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import requests.LoginRequest;
import results.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {

    private LoginService loginService;

    @BeforeEach
    public void setUp() throws DataAccessException {
        loginService = new LoginService();

        // Clear the database to ensure a fresh state for each test
        DaoService.getInstance().clear();

        // Add a test user with a bcrypt-hashed password
        String hashedPassword = BCrypt.hashpw("password", BCrypt.gensalt());
        User testUser = new User("testUser", hashedPassword, "email@test.com");
        DaoService.getInstance().getUserDAO().addUser(testUser);

        // Add a valid auth token for testing
        AuthToken validToken = new AuthToken("validToken", "testUser");
        DaoService.getInstance().getAuthDAO().addAuthToken(validToken);
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
