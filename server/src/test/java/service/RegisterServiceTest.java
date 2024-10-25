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
        UserData.getInstance().clearUserData(); // Clear in-memory user data before each test
        authDAO.clear(); // Clear in-memory auth data before each test
    }

    @Test
    public void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest("newUser", "password123", "newuser@mail.com");
        RegisterResult result = registerService.register(request);
        assertTrue(result.isSuccess(), "Registration should be successful");
        assertNotNull(result.getAuthToken(), "AuthToken should be generated");
        assertEquals("newUser", result.getUsername(), "Username should match the request");
        assertEquals("Registration successful!", result.getMessage(), "Message should indicate success");

        User user = userDAO.getUserByUsername("newUser");
        assertNotNull(user, "User should be added to the database");
        assertEquals("newuser@mail.com", user.getEmail(), "Email should match the request");
        assertEquals("password123", user.getPassword(), "Password should match the request");

        AuthToken authToken = authDAO.getAuthToken(result.getAuthToken());
        assertNotNull(authToken, "AuthToken should be stored in the database");
        assertEquals("newUser", authToken.getUsername(), "AuthToken should be associated with the user");
    }

    @Test
    public void testRegisterUserAlreadyExists() {
        RegisterRequest request1 = new RegisterRequest("existingUser", "password123", "existing@mail.com");
        registerService.register(request1); // First registration to create the user

        RegisterRequest request2 = new RegisterRequest("existingUser", "newpassword", "newemail@mail.com");

        RegisterResult result = registerService.register(request2); // Attempt to register the same user

        assertFalse(result.isSuccess(), "Registration should fail for an existing user");
        assertNull(result.getAuthToken(), "AuthToken should not be generated");
        assertNull(result.getUsername(), "Username should not be set in the result");
        assertEquals("Error: Forbidden - User already exists", result.getMessage(), "Message should indicate user exists");
    }

    @Test
    public void testRegisterInvalidRequest() {
        RegisterRequest request = new RegisterRequest(null, "password123", "invalidemail@mail.com");


        RegisterResult result = registerService.register(request);


        assertFalse(result.isSuccess(), "Registration should fail for an invalid request");
        assertNull(result.getAuthToken(), "AuthToken should not be generated");
        assertNull(result.getUsername(), "Username should not be set in the result");
        assertEquals("Error: Bad Request - Missing required fields", result.getMessage(), "Message should indicate bad request");
    }
}
