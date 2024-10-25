package model;

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

    public AuthToken getAuthToken(String token) {
        return authTokensTable.get(token);
    }
}

