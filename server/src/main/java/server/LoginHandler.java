package server;

import com.google.gson.Gson;
import requests.LoginRequest;
import results.LoginResult;
import service.LoginService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
        Gson gson = new Gson();

        LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);


        LoginService loginService = new LoginService();
        LoginResult loginResult = loginService.login(loginRequest);


        res.type("application/json");

        if (loginResult.isSuccess()) {
            res.status(200);
        } else {
            res.status(401);
        }

        return gson.toJson(loginResult);
    }
}
