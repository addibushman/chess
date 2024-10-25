package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import requests.JoinGameRequest;
import results.JoinGameResult;
import service.JoinGameService;

public class JoinGameHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
        Gson gson = new Gson();

        // Parse the incoming JSON request
        JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);

        // Set the auth token from the request headers
        String authToken = req.headers("Authorization");
        joinGameRequest.setAuthToken(authToken);

        // Call the JoinGameService
        JoinGameService joinGameService = new JoinGameService();
        JoinGameResult result = joinGameService.joinGame(joinGameRequest);

        res.type("application/json");
        if (result.isSuccess()) {
            res.status(200); // Success
        }
        else if (result.getMessage().contains("Invalid auth token")) {
            res.status(401); // Bad request
        }
        else if (result.getMessage().contains("Error Color already taken")) {
            res.status(403); // Forbidden
        }
        else {

            res.status(400); // Bad request
        }

        return gson.toJson(result);
    }
}
