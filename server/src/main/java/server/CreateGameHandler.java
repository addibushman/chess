package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import requests.CreateGameRequest;
import results.CreateGameResult;
import service.CreateGameService;

public class CreateGameHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
        Gson gson = new Gson();
        CreateGameRequest createGameRequest = gson.fromJson(req.body(), CreateGameRequest.class);

        String authToken = req.headers("Authorization");
        createGameRequest.setAuthToken(authToken);

        CreateGameService createGameService = new CreateGameService();
        CreateGameResult result = createGameService.createGame(createGameRequest);

        res.type("application/json");
        if (result.isSuccess()) {
            res.status(200);
        } else {
            res.status(401);
        }

        return gson.toJson(result);
    }
}
