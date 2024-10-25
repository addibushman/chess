package service;

import results.ClearResult;
import dataaccess.DataAccessException;

public class ClearService {

    public ClearResult clear() throws DataAccessException {
        DaoService.getInstance().getUserDAO().clear();
        DaoService.getInstance().getAuthDAO().clear();
        DaoService.getInstance().getGameDAO().clear();

        return new ClearResult(true, "Database cleared successfully!");
    }
}


