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

        // Validate that the second game was added despite having the same name
        assertDoesNotThrow(() -> {
            GameData retrievedGame = gameDAO.getGameByID("2"); // Adjust as needed
            assertNotNull(retrievedGame);
            assertEquals("DuplicateGame", retrievedGame.getGameName());
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
