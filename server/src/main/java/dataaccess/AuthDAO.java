package dataaccess;

import model.AuthToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AuthDAO {
    // In-memory storage for auth tokens
    private static HashMap<String, AuthToken> authTokens = new HashMap<>();

    public void addAuthToken(AuthToken authToken) {
        authTokens.put(authToken.getToken(), authToken);
    }

    public AuthToken getAuthToken(String token) {
        return authTokens.get(token);
    }

    public List<AuthToken> getAllAuthTokens() {
        return new ArrayList<>(authTokens.values()); // Return a list of all auth tokens
    }

    public void clear() {
        authTokens.clear(); // Clear all auth tokens from the in-memory store
    }
}



