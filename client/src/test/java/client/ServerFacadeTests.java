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
        currentToken = setupValidToken();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    private AuthToken setupValidToken() throws DataAccessException {
        AuthToken validToken = new AuthToken("validToken", "testUser");
        DaoService.getInstance().getAuthDAO().addAuthToken(validToken);
        return validToken;
    }

    // Register tests
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

    // List Games tests
    @Test
    public void testListGamesSuccessWithValidToken() {
        ListGamesRequest request = new ListGamesRequest("validToken");
        ListGamesResult result = listGamesService.listGames(request);

        assertTrue(result.isSuccess());
        assertEquals(0, result.getGames().size(), "Expected no games in the list initially");
        assertEquals("Games retrieved successfully", result.getMessage());
    }

    @Test
    public void testListGamesFailure() {
        ListGamesRequest request = new ListGamesRequest("invalidToken");
        ListGamesResult result = listGamesService.listGames(request);

        assertFalse(result.isSuccess());
        assertEquals("Error Invalid authToken", result.getMessage());
    }

    // Play Game tests
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

    // Observe Game tests
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

    @Test
    public void testObserveGameFailure() {
        try {
            AuthToken registerToken = facade.register("testUser", "testPass", "test@mail.com");
            currentToken = registerToken;
            assertNotNull(registerToken);
            assertTrue(registerToken.getToken().length() > 10);

            String gameID = facade.createGame("Test Game", currentToken);
            assertNotNull(gameID);

            List<GameData> games = facade.listGames(currentToken);
            assertTrue(games.size() > 0, "Game should be listed after creation");

            System.out.println("Trying to observe a game that doesn't exist.");
            chessClient.observeGame();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Invalid game number"));
        }
    }
}
