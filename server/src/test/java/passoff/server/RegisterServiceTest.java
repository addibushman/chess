package passoff.server;

import dataaccess.UserData;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.RegisterRequest;
import results.RegisterResult;
import service.RegisterService;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTest {

    private RegisterService registerService;

    @BeforeEach
    public void setUp() {
        registerService = new RegisterService();

        UserData.getInstance().clearUserData();
    }

    @Test
    public void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest("newUser", "password123", "newuser@mail.com");
        RegisterResult result = registerService.register(request);

        assertTrue(result.isSuccess());
        assertNotNull(result.getAuthToken());
        assertEquals("newUser", result.getUsername());
        assertEquals("Registration successful!", result.getMessage());

        User user = UserData.getInstance().getUserByUsername("newUser");
        assertNotNull(user);
        assertEquals("newuser@mail.com", user.getEmail());
    }

    @Test
    public void testRegisterUserAlreadyExists() {
        RegisterRequest request1 = new RegisterRequest("existingUser", "password123", "existing@mail.com");
        registerService.register(request1); // First registration succeeds

        RegisterRequest request2 = new RegisterRequest("existingUser", "newpassword", "newemail@mail.com");
        RegisterResult result = registerService.register(request2); // Second registration should fail

        assertFalse(result.isSuccess());
        assertEquals("User already exists.", result.getMessage());
    }
}

