package dataaccess;

import model.AuthToken;

public class AuthDAO {

    private final AuthData authData = AuthData.getInstance();

    public void addAuthToken(AuthToken authToken) {
        authData.addAuthToken(authToken);
    }

    public AuthToken getAuthToken(String token) {
        return authData.getAuthToken(token);
    }

    public void clearAuthTokens() {
        authData.clearAuthData();
    }
}

