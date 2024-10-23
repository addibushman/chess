package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import request.LoginRequest;
import model.LoginResult;
import model.User;
import model.AuthToken;

import java.util.UUID;

public class LoginService {

    public LoginResult login(LoginRequest request) throws DataAccessException {
        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUser(request.getUsername());

        if (user != null && user.getPassword().equals(request.getPassword())) {
            //something is wrong here with me generating random data
            AuthToken token = new AuthToken(UUID.randomUUID().toString(), user.getUsername());
            AuthDAO authDAO = new AuthDAO();
            authDAO.createAuthToken(token);

            return new LoginResult(user.getUsername(), token.getToken(), true);
        } else {
            return new LoginResult(null, null, false);
        }
    }
}

