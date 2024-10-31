package service;

import dataaccess.DataAccessException;
import model.AuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ListGamesRequest;
import results.ListGamesResult;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesServiceTest {

    private ListGamesService listGamesService;

    @BeforeEach
    public void setUp() throws DataAccessException {
        listGamesService = new ListGamesService();

        // Clear the database to ensure a fresh start for each test
        DaoService.getInstance().clear();

        // Add a valid auth token for testing
        AuthToken validToken = new AuthToken("validToken", "testUser");
        DaoService.getInstance().getAuthDAO().addAuthToken(validToken);
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
}
