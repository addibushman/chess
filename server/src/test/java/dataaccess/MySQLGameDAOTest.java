package dataaccess;

import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MySQLGameDAOTest {
    private MySQLGameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new MySQLGameDAO();
        gameDAO.clear();
    }

    @Test
    public void testAddGameSuccess() {
        GameData game = new GameData(null, "Game 1", "whitePlayer1", "blackPlayer1");
        assertDoesNotThrow(() -> gameDAO.createGame(game));

        assertDoesNotThrow(() -> {
            GameData retrievedGame = gameDAO.getGameByName("Game 1");
            assertNotNull(retrievedGame, "Game should have been added");
            assertEquals("Game 1", retrievedGame.getGameName());
            assertEquals("whitePlayer1", retrievedGame.getWhiteUsername());
            assertEquals("blackPlayer1", retrievedGame.getBlackUsername());
        });
    }

    @Test
    public void testAddDuplicateGameName() {
        GameData game1 = new GameData(null, "DuplicateGame", "whitePlayer1", "blackPlayer1");
        assertDoesNotThrow(() -> gameDAO.createGame(game1));

        GameData game2 = new GameData(null, "DuplicateGame", "whitePlayer2", "blackPlayer2");
        assertDoesNotThrow(() -> gameDAO.createGame(game2));

        assertDoesNotThrow(() -> {
            var gamesList = gameDAO.listGames();
            long duplicateCount = gamesList.stream()
                    .filter(g -> "DuplicateGame".equals(g.getGameName()))
                    .count();
            assertTrue(duplicateCount >= 2, "There should be at least two games with the name 'DuplicateGame'");
        });
    }

    @Test
    public void testGetGameByIDSuccess() {
        // Positive case: Successfully retrieve an existing game by ID
        GameData game = new GameData(null, "RetrieveGame", "whitePlayer", "blackPlayer");
        String gameID = assertDoesNotThrow(() -> gameDAO.createGame(game));

        assertDoesNotThrow(() -> {
            GameData retrievedGame = gameDAO.getGameByID(gameID);
            assertNotNull(retrievedGame, "Game should be retrieved by ID");
            assertEquals("RetrieveGame", retrievedGame.getGameName());
        });
    }

    @Test
    public void testGetNonExistentGameByID() {
        // Negative case: Attempt to retrieve a non-existent game by ID
        assertDoesNotThrow(() -> {
            GameData game = gameDAO.getGameByID("nonExistentGameID");
            assertNull(game, "Game should not exist");
        });
    }

    @Test
    public void testListGamesSuccess() {
        GameData game1 = new GameData(null, "ListGame1", "whitePlayer1", "blackPlayer1");
        GameData game2 = new GameData(null, "ListGame2", "whitePlayer2", "blackPlayer2");
        assertDoesNotThrow(() -> {
            gameDAO.createGame(game1);
            gameDAO.createGame(game2);
        });

        assertDoesNotThrow(() -> {
            var gamesList = gameDAO.listGames();
            assertEquals(2, gamesList.size(), "Two games should be listed");
        });
    }

    @Test
    public void testListGamesEmpty() {
        // Negative case: Verify listing games when no games exist
        assertDoesNotThrow(() -> {
            var gamesList = gameDAO.listGames();
            assertTrue(gamesList.isEmpty(), "Games list should be empty");
        });
    }

    @Test
    public void testUpdateGameSuccess() {
        // Positive case: Update an existing game's details
        GameData game = new GameData(null, "UpdateGame", "whitePlayer", "blackPlayer");
        String gameID = assertDoesNotThrow(() -> gameDAO.createGame(game));

        GameData updatedGame = new GameData(gameID, "UpdateGame", "updatedWhitePlayer", "updatedBlackPlayer");
        assertDoesNotThrow(() -> gameDAO.updateGame(updatedGame));

        assertDoesNotThrow(() -> {
            GameData retrievedGame = gameDAO.getGameByID(gameID);
            assertNotNull(retrievedGame, "Game should be retrieved after update");
            assertEquals("updatedWhitePlayer", retrievedGame.getWhiteUsername(), "White player username should be updated");
            assertEquals("updatedBlackPlayer", retrievedGame.getBlackUsername(), "Black player username should be updated");
        });
    }

    @Test
    public void testUpdateNonExistentGame() {
        // Negative case: Attempt to update a non-existent game
        GameData nonExistentGame = new GameData("nonExistentGameID", "NonExistentGame", "whitePlayer", "blackPlayer");
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(nonExistentGame), "Updating a non-existent game should throw exception");
    }

    @Test
    public void testClearGames() {
        // Positive case: Clear all games and verify no games remain
        GameData game1 = new GameData(null, "ClearGame1", "whitePlayer1", "blackPlayer1");
        GameData game2 = new GameData(null, "ClearGame2", "whitePlayer2", "blackPlayer2");
        assertDoesNotThrow(() -> {
            gameDAO.createGame(game1);
            gameDAO.createGame(game2);
        });

        assertDoesNotThrow(() -> gameDAO.clear());

        assertDoesNotThrow(() -> {
            var gamesList = gameDAO.listGames();
            assertTrue(gamesList.isEmpty(), "Games list should be empty after clearing");
        });
    }
}
