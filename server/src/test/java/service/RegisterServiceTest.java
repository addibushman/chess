package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import model.UserData;
import model.AuthToken;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.RegisterRequest;
import results.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTest {

    private RegisterService registerService;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    @BeforeEach
    public void setUp() {
        registerService = new RegisterService();
        userDAO = new UserDAO();
        authDAO = new AuthDAO();


        UserData.getInstance().clearUserData();
        authDAO.clear();
    }

    @Test
    public void testRegisterSuccess() {

        RegisterRequest request = new RegisterRequest("newUser", "password123", "newuser@mail.com");


        RegisterResult result = registerService.register(request);


        assertTrue(result.isSuccess(), "Registration should succeed");
        assertNotNull(result.getAuthToken(), "AuthToken should be generated");
        assertEquals("newUser", result.getUsername(), "Username should match");
        assertEquals("Registration successful!", result.getMessage(), "Success message expected");


        User user = userDAO.getUserByUsername("newUser");
        assertNotNull(user, "User should be found in the database");
        assertEquals("newuser@mail.com", user.getEmail(), "User email should match");
        assertEquals("password123", user.getPassword(), "User password should match");


        AuthToken authToken = authDAO.getAuthToken(result.getAuthToken());
        assertNotNull(authToken, "AuthToken should be stored");
        assertEquals("newUser", authToken.getUsername(), "AuthToken should match user");
    }

    @Test
    public void testRegisterUserAlreadyExists() {
        RegisterRequest request1 = new RegisterRequest("existingUser", "password123", "existing@mail.com");
        registerService.register(request1);

        RegisterRequest request2 = new RegisterRequest("existingUser", "newpassword", "newemail@mail.com");


        RegisterResult result = registerService.register(request2);


        assertFalse(result.isSuccess(), "Registration should fail for an existing user");
        assertNull(result.getAuthToken(), "AuthToken should not be generated");
        assertNull(result.getUsername(), "Username should not be set");
        assertEquals("Error: Forbidden - User already exists", result.getMessage(), "User exists error message expected");


        User user = userDAO.getUserByUsername("existingUser");
        assertNotNull(user, "Existing user should still be in the database");
        assertEquals("existing@mail.com", user.getEmail(), "Email should match initial registration");
    }

    @Test
    public void testRegisterInvalidRequest() {

        RegisterRequest request = new RegisterRequest("", "password123", "invalidemail@mail.com");

        RegisterResult result = registerService.register(request);

        assertFalse(result.isSuccess(), "Registration should fail for invalid input");
        assertNull(result.getAuthToken(), "AuthToken should not be generated");
        assertNull(result.getUsername(), "Username should not be set");
        assertEquals("Error: Bad Request - Missing required fields", result.getMessage(), "Bad request message expected");

        User user = userDAO.getUserByUsername("");
        assertNull(user, "No user should be added for invalid request");
    }
}
