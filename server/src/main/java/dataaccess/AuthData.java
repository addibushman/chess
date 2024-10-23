package dataaccess;

import model.AuthToken;

import java.util.HashMap;
import java.util.Map;

public class AuthData {
    private Map<String, AuthToken> authTokensTable = new HashMap<>();

    private static AuthData instance;

    private AuthData() {}

    public static AuthData getInstance() {
        if (instance == null) {
            instance = new AuthData();
        }
        return instance;
    }

    public void addAuthToken(AuthToken authToken) {
        authTokensTable.put(authToken.getToken(), authToken);
    }

    public AuthToken getAuthToken(String token) {
        return authTokensTable.get(token);
    }

    public void clearAuthData() {
        authTokensTable.clear();
    }
}

