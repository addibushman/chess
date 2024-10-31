package service;

import dataaccess.MySQLAuthTokenDAO;
import dataaccess.MySQLGameDAO;
import dataaccess.MySQLUserDAO;
import dataaccess.DataAccessException;

public class DaoService {
    private static DaoService daoService = null;

    public static DaoService getInstance() {
        if (daoService == null) {
            daoService = new DaoService();
        }
        return daoService;
    }

    private final MySQLAuthTokenDAO authDAO = new MySQLAuthTokenDAO();
    private final MySQLGameDAO gameDAO = new MySQLGameDAO();
    private final MySQLUserDAO userDAO = new MySQLUserDAO();

    public MySQLAuthTokenDAO getAuthDAO() {
        return authDAO;
    }

    public MySQLGameDAO getGameDAO() {
        return gameDAO;
    }

    public MySQLUserDAO getUserDAO() {
        return userDAO;
    }
    public void clear() throws DataAccessException {
        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
        } catch (DataAccessException e) {
            throw new DataAccessException("Error clearing the database");
        }
    }
}
