package service;

import dataaccess.DataAccessException;
import model.User;
import model.AuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import results.ClearResult;
import org.junit.jupiter.api.Assertions;

public class ClearServiceTest {

    private ClearService clearService;


    @BeforeEach
    public void setUp() throws DataAccessException {
        clearService = new ClearService();


        DaoService.getInstance().getUserDAO().addUser(new User("testUser1", "password1", "email1@test.com"));
        DaoService.getInstance().getUserDAO().addUser(new User("testUser2", "password2", "email2@test.com"));
        DaoService.getInstance().getAuthDAO().addAuthToken(new AuthToken("token1", "testUser1"));
        DaoService.getInstance().getAuthDAO().addAuthToken(new AuthToken("token2", "testUser2"));
    }

    @Test
    public void testClearSuccess() throws DataAccessException {
        ClearResult result = clearService.clear();


        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Database cleared successfully!", result.getMessage());


        Assertions.assertEquals(0,DaoService.getInstance().getUserDAO().getAllUsers().size());
        Assertions.assertEquals(0, DaoService.getInstance().getAuthDAO().getAllAuthTokens().size());
    }

    @Test
    public void testClearFailure() throws DataAccessException {

        DaoService.getInstance().getUserDAO().clear();
        DaoService.getInstance().getAuthDAO().clear();


        ClearResult result = clearService.clear();


        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Database cleared successfully!", result.getMessage());
    }
}
