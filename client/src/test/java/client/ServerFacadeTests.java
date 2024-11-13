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
        AuthToken registerToken = facade.register("testUser", "testPass", "test@mail.com");
        assertNotNull(registerToken);
        assertTrue(registerToken.getToken().length() > 10);
        AuthToken loginToken = facade.login("testUser", "testPass");
        assertNotNull(loginToken);
        assertTrue(loginToken.getToken().length() > 10);
        facade.logout(loginToken);
        AuthToken reLoginToken = facade.login("testUser", "testPass");
        assertNotNull(reLoginToken);
        assertTrue(reLoginToken.getToken().length() > 10);
        assertEquals("testUser", reLoginToken.getUsername());
    }
    @Test
    void testLogoutWithInvalidToken() {
        Exception exception = assertThrows(Exception.class, () -> facade.logout(new AuthToken("invalidToken", "testUser")));
        assertTrue(exception.getMessage().contains("Logout failed"));
    }

    //create game tests
    @Test
    void testCreateGame() throws Exception {
        AuthToken registerToken = facade.register("testUser", "testPass", "test@mail.com");
        AuthToken loginToken = facade.login("testUser", "testPass");
        assertNotNull(loginToken);
        assertTrue(loginToken.getToken().length() > 10);
        String gameName = "Test Game";
        String gameID = facade.createGame(gameName, loginToken);
        assertNotNull(gameID);
        assertTrue(gameID.length() > 0);
        System.out.println("Game created with ID: " + gameID);
    }

    @Test
    void testCreateGameInvalidData() throws Exception {
        AuthToken registerToken = facade.register("testUser", "testPass", "test@mail.com");
        AuthToken loginToken = facade.login("testUser", "testPass");
        assertNotNull(loginToken);
        assertTrue(loginToken.getToken().length() > 10);
        Exception exception = assertThrows(Exception.class, () -> facade.createGame("", loginToken));
        assertTrue(exception.getMessage().contains("Failed to create game: Error: Game name cannot be empty"));
    }

}
