package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import service.RegisterService;
import requests.RegisterRequest;
import results.RegisterResult;

public class RegisterHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
        Gson gson = new Gson();
        RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);

        RegisterService registerService = new RegisterService();
        RegisterResult result = registerService.register(registerRequest);

        res.type("application/json");
        if (result.isSuccess()) {
            res.status(200);
        } else {
            res.status(403);
        }
        return gson.toJson(result);
    }
}