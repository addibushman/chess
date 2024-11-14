package client;

import dataaccess.DataAccessException;
import model.AuthToken;
import model.GameData;
import org.junit.jupiter.api.*;
import requests.ListGamesRequest;
import results.ListGamesResult;
import server.Server;
import service.DaoService;
import service.ListGamesService;
import ui.ChessClient;
import ui.ServerFacade;

import org.junit.jupiter.api.Test;



import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private ListGamesService listGamesService;
    private ChessClient chessClient;
    private AuthToken currentToken;


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

    @BeforeEach
    public void setUp() throws DataAccessException {
        listGamesService = new ListGamesService();
        DaoService.getInstance().clear();

        AuthToken validToken = new AuthToken("validToken", "testUser");
        DaoService.getInstance().getAuthDAO().addAuthToken(validToken);
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


    @Test
    public void testListGamesSuccess() {
        ListGamesRequest request = new ListGamesRequest("validToken");
        ListGamesResult result = listGamesService.listGames(request);
        assertTrue(result.isSuccess());
        assertEquals(0, result.getGames().size(), "Expected no games in the list initially");
        assertEquals("Games retrieved successfully", result.getMessage());
    }

    @Test
    public void testListGamesFailureInvalidToken() {
        ListGamesRequest request = new ListGamesRequest("invalidToken");
        ListGamesResult result = listGamesService.listGames(request);

        assertFalse(result.isSuccess());
        assertEquals("Error Invalid authToken", result.getMessage());
    }

    //join game tests next

    @Test
    public void testPlayGameSuccess() {
        try {
            AuthToken registerToken = facade.register("testUser", "testPass", "test@mail.com");
            currentToken = registerToken;
            assertNotNull(registerToken);
            assertTrue(registerToken.getToken().length() > 10);
            String gameID = facade.createGame("Test Game", currentToken);
            assertNotNull(gameID);
            chessClient.playGame();
            System.out.println("Successfully joined the game as " + currentToken.getUsername());
        } catch (Exception e) {
            fail("playGame should not have thrown an exception: " + e.getMessage());
        }
    }

    @Test
    public void testPlayGameFailure() {
        try {
            AuthToken registerToken = facade.register("testUser", "testPass", "test@mail.com");
            currentToken = registerToken;

            assertNotNull(registerToken);
            assertTrue(registerToken.getToken().length() > 10);
            String gameID = facade.createGame("Test Game", currentToken);
            assertNotNull(gameID);
            List<GameData> games = facade.listGames(currentToken);
            assertTrue(games.size() > 0, "Game should be listed after creation");
            chessClient.playGame();
            assertTrue(true);
        } catch (Exception e) {
            fail("playGame should not have thrown an exception: " + e.getMessage());
        }
    }
//observe game tests
@Test
public void testObserveGameSuccess() {
    try {
        AuthToken registerToken = facade.register("testUser", "testPass", "test@mail.com");
        currentToken = registerToken;
        assertNotNull(registerToken);
        assertTrue(registerToken.getToken().length() > 10);
        String gameID = facade.createGame("Test Game", currentToken);
        assertNotNull(gameID);
        List<GameData> games = facade.listGames(currentToken);
        assertTrue(games.size() > 0, "Game should be listed after creation");
        chessClient.observeGame();
        System.out.println("Successfully joined the game as " + currentToken.getUsername());
    } catch (Exception e) {
        fail("observeGame should not have thrown an exception: " + e.getMessage());
    }
}
}
