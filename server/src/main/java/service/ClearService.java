package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import results.ClearResult;
import dataaccess.DataAccessException;

public class ClearService {

    private final UserDAO userDAO = new UserDAO();
    private final AuthDAO authDAO = new AuthDAO();

    public ClearResult clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();

        return new ClearResult(true, "Database cleared successfully!");
    }
}


