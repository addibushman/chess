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
        // Create and add "Game 1"
        GameData game = new GameData(null, "Game 1", "whitePlayer1", "blackPlayer1");
        assertDoesNotThrow(() -> gameDAO.createGame(game));

        // Retrieve the game by name to verify it was added
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
        // Add the first game
        GameData game1 = new GameData(null, "DuplicateGame", "whitePlayer1", "blackPlayer1");
        assertDoesNotThrow(() -> gameDAO.createGame(game1));

        // Add a second game with the same name, which should not throw an exception
        GameData game2 = new GameData(null, "DuplicateGame", "whitePlayer2", "blackPlayer2");
        assertDoesNotThrow(() -> gameDAO.createGame(game2));

        // Retrieve both games by name to confirm they both exist, regardless of ID
        assertDoesNotThrow(() -> {
            // Check if at least two games with the name "DuplicateGame" exist
            var gamesList = gameDAO.listGames(); // Fetch all games and filter by name
            long duplicateCount = gamesList.stream()
                    .filter(g -> "DuplicateGame".equals(g.getGameName()))
                    .count();
            assertTrue(duplicateCount >= 2, "There should be at least two games with the name 'DuplicateGame'");
        });
    }


    @Test
    public void testGetNonExistentGame() {
        assertDoesNotThrow(() -> {
            GameData game = gameDAO.getGameByID("nonExistentGameID");
            assertNull(game, "Game should not exist");
        });
    }
}
