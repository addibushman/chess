package dataaccess;

import model.AuthToken;

import java.util.HashMap;
import java.util.Map;

public class AuthDAO {
    private static Map<String, AuthToken> authTokens = new HashMap<>();

    public void createAuthToken(AuthToken token) {
        authTokens.put(token.getToken(), token);
    }

    public AuthToken getAuthToken(String token) {
        return authTokens.get(token);
    }
}
