package dataaccess;

import model.AuthToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AuthDAO {

    private static HashMap<String, AuthToken> authTokens = new HashMap<>();

    public void addAuthToken(AuthToken authToken) {
        authTokens.put(authToken.getToken(), authToken);
    }

    public AuthToken getAuthToken(String token) {
        return authTokens.get(token);
    }

    public List<AuthToken> getAllAuthTokens() {
        return new ArrayList<>(authTokens.values());
    }

    public void clear() {
        authTokens.clear();
    }
    public void deleteAuthToken(String authToken) {
        authTokens.remove(authToken);
    }
}



