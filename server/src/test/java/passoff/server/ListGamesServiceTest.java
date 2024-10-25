package passoff.server;

import dataaccess.AuthDAO;
import model.AuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ListGamesRequest;
import results.ListGamesResult;
import service.ListGamesService;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesServiceTest {

    private ListGamesService listGamesService;

    @BeforeEach
    public void setUp() {
        // Initialize the ListGamesService
        listGamesService = new ListGamesService();

        // Populate the in-memory database with some data for testing
        AuthDAO authDAO = new AuthDAO();
        authDAO.addAuthToken(new AuthToken("validToken", "testUser"));

        // Assume GameDAO is populated by the service layer
    }

    @Test
    public void testListGamesSuccess() {
        // Create a valid request
        ListGamesRequest request = new ListGamesRequest("validToken");

        // Call the listGames service
        ListGamesResult result = listGamesService.listGames(request);

        // Verify that the result indicates success
        assertTrue(result.isSuccess());
        assertEquals(3, result.getGames().size());
        assertEquals("Games retrieved successfully", result.getMessage());
    }

    @Test
    public void testListGamesFailureInvalidToken() {
        // Create a request with an invalid authToken
        ListGamesRequest request = new ListGamesRequest("invalidToken");

        // Call the listGames service
        ListGamesResult result = listGamesService.listGames(request);

        // Verify that the result indicates failure
        assertFalse(result.isSuccess());
        assertEquals("Invalid authToken", result.getMessage());
    }
}

