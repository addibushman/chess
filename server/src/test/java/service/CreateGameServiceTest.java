package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import results.CreateGameResult;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTest {

    private CreateGameService createGameService;
    private AuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        createGameService = new CreateGameService();
        authDAO = new AuthDAO();


        AuthToken validToken = new AuthToken("validToken", "testUser");
        authDAO.addAuthToken(validToken);
    }

    @Test
    public void testCreateGameSuccess() {

        CreateGameRequest request = new CreateGameRequest("Test Game", "validToken");


        CreateGameResult result = createGameService.createGame(request);


        assertTrue(result.isSuccess());
        assertEquals("Game created successfully", result.getMessage());
        assertNotNull(result.getGameID(), "Game ID should not be null");
    }

    @Test
    public void testCreateGameInvalidAuthToken() {

        CreateGameRequest request = new CreateGameRequest("Test Game", "invalidToken");


        CreateGameResult result = createGameService.createGame(request);


        assertFalse(result.isSuccess());
        assertEquals("Error Invalid auth token", result.getMessage());
        assertNull(result.getGameID(), "Game ID should be null");
    }

    @Test
    public void testCreateGameNoGameName() {

        CreateGameRequest request = new CreateGameRequest("", "validToken");


        CreateGameResult result = createGameService.createGame(request);


        assertTrue(result.isSuccess());
        assertEquals("Game created successfully", result.getMessage());
        assertNotNull(result.getGameID(), "Game ID should not be null");
    }
}

