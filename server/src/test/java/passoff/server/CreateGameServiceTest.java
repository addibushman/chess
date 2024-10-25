package passoff.server;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import results.CreateGameResult;
import service.CreateGameService;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTest {

    private CreateGameService createGameService;
    private AuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        createGameService = new CreateGameService();
        authDAO = new AuthDAO();

        // Add a valid auth token for testing
        AuthToken validToken = new AuthToken("validToken", "testUser");
        authDAO.addAuthToken(validToken);
    }

    @Test
    public void testCreateGameSuccess() {
        // Create a request with a valid auth token and game name
        CreateGameRequest request = new CreateGameRequest("Test Game", "validToken");

        // Call the createGame service
        CreateGameResult result = createGameService.createGame(request);

        // Assert the result is successful
        assertTrue(result.isSuccess());
        assertEquals("Game created successfully", result.getMessage());
        assertNotNull(result.getGameID(), "Game ID should not be null");
    }

    @Test
    public void testCreateGameInvalidAuthToken() {
        // Create a request with an invalid auth token
        CreateGameRequest request = new CreateGameRequest("Test Game", "invalidToken");

        // Call the createGame service
        CreateGameResult result = createGameService.createGame(request);

        // Assert the result is unsuccessful
        assertFalse(result.isSuccess());
        assertEquals("Error Invalid auth token", result.getMessage());
        assertNull(result.getGameID(), "Game ID should be null");
    }

    @Test
    public void testCreateGameNoGameName() {
        // Create a request with no game name
        CreateGameRequest request = new CreateGameRequest("", "validToken");

        // Call the createGame service
        CreateGameResult result = createGameService.createGame(request);

        // Assert the result is successful (allowing empty names)
        assertTrue(result.isSuccess());
        assertEquals("Game created successfully", result.getMessage());
        assertNotNull(result.getGameID(), "Game ID should not be null");
    }
}

