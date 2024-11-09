package client;

import model.AuthToken;
import model.GameData;
import java.util.List;

public class ServerFacade {
    private final String baseUrl;

    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
    }

    public AuthToken register(String username, String password, String email) throws Exception {

    }

    public AuthToken login(String username, String password) throws Exception {

    }

    public boolean logout(AuthToken token) throws Exception {

    }

    public GameData createGame(String gameName, AuthToken token) throws Exception {

    }

    public List<GameData> listGames(AuthToken token) throws Exception {

    }

    public boolean joinGame(String gameID, String teamColor, AuthToken token) throws Exception {

    }

    public boolean observeGame(String gameID, AuthToken token) throws Exception {
        return true;
    }
}

