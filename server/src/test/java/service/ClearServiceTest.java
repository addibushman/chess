package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import model.User;
import model.AuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import results.ClearResult;
import org.junit.jupiter.api.Assertions;

public class ClearServiceTest {

    private ClearService clearService;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        clearService = new ClearService();
        userDAO = new UserDAO();
        authDAO = new AuthDAO();

        userDAO.addUser(new User("testUser1", "password1", "email1@test.com"));
        userDAO.addUser(new User("testUser2", "password2", "email2@test.com"));
        authDAO.addAuthToken(new AuthToken("token1", "testUser1"));
        authDAO.addAuthToken(new AuthToken("token2", "testUser2"));
    }

    @Test
    public void testClearSuccess() throws DataAccessException {
        ClearResult result = clearService.clear();


        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Database cleared successfully!", result.getMessage());


        Assertions.assertEquals(0, userDAO.getAllUsers().size());
        Assertions.assertEquals(0, authDAO.getAllAuthTokens().size());
    }

    @Test
    public void testClearFailure() throws DataAccessException {

        userDAO.clear();
        authDAO.clear();


        ClearResult result = clearService.clear();


        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Database cleared successfully!", result.getMessage());
    }
}
