package passoff.server;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import results.ClearResult;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import service.ClearService;
import model.User;
import model.AuthToken;

public class ClearServiceTest {

    private ClearService clearService;

    @BeforeEach
    public void setUp() throws dataaccess.DataAccessException {
        clearService = new ClearService();

        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();

        userDAO.addUser(new User("testUser1", "password1", "email1@test.com"));
        userDAO.addUser(new User("testUser2", "password2", "email2@test.com"));
        authDAO.addAuthToken(new AuthToken("token1", "testUser1"));
        authDAO.addAuthToken(new AuthToken("token2", "testUser2"));
    }

    @Test
    public void testClearSuccess() throws dataaccess.DataAccessException {
        ClearResult result = clearService.clear();

        assertTrue(result.isSuccess());
        assertEquals("Database cleared successfully!", result.getMessage());

        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        assertEquals(0, userDAO.getAllUsers().size());
        assertEquals(0, authDAO.getAllAuthTokens().size());
    }
}
