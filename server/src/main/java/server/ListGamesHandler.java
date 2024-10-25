package server;

import com.google.gson.Gson;
import requests.ListGamesRequest;
import results.ListGamesResult;
import service.ListGamesService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
        Gson gson = new Gson();


        String authToken = req.headers("Authorization");


        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);


        ListGamesService listGamesService = new ListGamesService();
        ListGamesResult result = listGamesService.listGames(listGamesRequest);

        res.type("application/json");
        if (result.isSuccess()) {
            res.status(200);
        } else if (result.getMessage().contains("Invalid authToken")) {
            res.status(401);
        } else {
            res.status(403);
        }

        return gson.toJson(result);
    }
}

