package server;

import com.google.gson.Gson;
import requests.LogoutRequest;
import results.LogoutResult;
import service.LogoutService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
        Gson gson = new Gson();
        String authToken = req.headers("Authorization");

        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        LogoutService logoutService = new LogoutService();
        LogoutResult result = logoutService.logout(logoutRequest);

        res.type("application/json");
        if (result.isSuccess()) {
            res.status(200);
        } else {
            res.status(401);
        }
        return gson.toJson(result);
    }
}
