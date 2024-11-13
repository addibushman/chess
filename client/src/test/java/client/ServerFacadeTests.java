package client;

import model.AuthToken;
import org.junit.jupiter.api.*;
import server.Server;
import service.DaoService;
import ui.ServerFacade;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }
    @BeforeEach
    void clearDatabase() throws Exception {
        DaoService.getInstance().getUserDAO().clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

//Register tests
    @Test
    void testRegister() throws Exception {
        AuthToken token = facade.register("testUser", "testPass", "test@mail.com");
        assertNotNull(token);
        assertTrue(token.getToken().length() > 10);
    }

    @Test
    void testRegisterInvalidData() {
        assertThrows(Exception.class, () -> facade.register("", "testPass", "test@mail.com"));
        assertThrows(Exception.class, () -> facade.register("testUser", "", "test@mail.com"));
        assertThrows(Exception.class, () -> facade.register("testUser", "testPass", ""));
    }

    @Test
    void testRegisterDuplicateUser() throws Exception {
        facade.register("duplicateUser", "testPass", "test@mail.com");

        Exception exception = assertThrows(Exception.class, () -> facade.register("duplicateUser", "testPass", "test@mail.com"));

        assertTrue(exception.getMessage().contains("Registration failed"));
    }
//next will do Login tests
@Test
void testRegisterAndLogin() throws Exception {
    // Step 1: Register the user
    AuthToken registerToken = facade.register("testUser", "testPass", "test@mail.com");

    assertNotNull(registerToken);
    assertTrue(registerToken.getToken().length() > 10);

    AuthToken loginToken = facade.login("testUser", "testPass");

    assertNotNull(loginToken);
    assertTrue(loginToken.getToken().length() > 10);
    assertEquals("testUser", loginToken.getUsername());
}

    @Test
    void testLoginInvalidCredentials() throws Exception {
        AuthToken registerToken = facade.register("testUser", "testPass", "test@mail.com");

        assertNotNull(registerToken);
        assertTrue(registerToken.getToken().length() > 10);

        Exception exception = assertThrows(Exception.class, () -> facade.login("testUser", "wrongPassword"));
        assertTrue(exception.getMessage().contains("Login failed"));

        exception = assertThrows(Exception.class, () -> facade.login("wrongUser", "testPass"));
        assertTrue(exception.getMessage().contains("Login failed"));
    }

    //logout tests
    @Test
    void testLogout() throws Exception {
        // Step 1: Register the user
        AuthToken registerToken = facade.register("testUser", "testPass", "test@mail.com");

        // Ensure registration was successful
        assertNotNull(registerToken);
        assertTrue(registerToken.getToken().length() > 10);

        // Step 2: Login with the same credentials
        AuthToken loginToken = facade.login("testUser", "testPass");

        // Ensure login was successful
        assertNotNull(loginToken);
        assertTrue(loginToken.getToken().length() > 10);

        // Step 3: Log out with the generated authToken
        facade.logout(loginToken);

        // Step 4: After logout, we should still be able to log in again
        // Attempt to log in again with the same credentials (this should be successful)
        AuthToken reLoginToken = facade.login("testUser", "testPass");

        // Ensure the re-login was successful
        assertNotNull(reLoginToken);
        assertTrue(reLoginToken.getToken().length() > 10);

        // Ensure the username is still correct after re-login
        assertEquals("testUser", reLoginToken.getUsername());
    }


    @Test
    void testLogoutWithInvalidToken() {
        // Simulate logging out with an invalid token
        Exception exception = assertThrows(Exception.class, () -> facade.logout(new AuthToken("invalidToken", "testUser")));
        assertTrue(exception.getMessage().contains("Logout failed"));
    }
}
