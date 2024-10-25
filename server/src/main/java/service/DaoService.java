package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class DaoService {

    private static DaoService daoService = null;

    public static DaoService getInstance() {
        if (daoService == null) {
            daoService = new DaoService();
        }
        return daoService;
    }

    private final AuthDAO authDAO = new AuthDAO();
    private final GameDAO gameDAO = new GameDAO();
    private final UserDAO userDAO = new UserDAO();

    public AuthDAO getAuthDAO() {
        return authDAO;
    }

    public GameDAO getGameDAO() {
        return gameDAO;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }
}


