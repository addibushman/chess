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

        // Parse the request body into a LoginRequest object
        LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);

        // Call the login service
        LoginService loginService = new LoginService();
        LoginResult loginResult = loginService.login(loginRequest);

        // Set the response type
        res.type("application/json");

        // Set status based on success or failure of login
        if (loginResult.isSuccess()) {
            res.status(200);
        } else {
            res.status(401);
        }

        // Return the result as a JSON string
        return gson.toJson(loginResult);
    }
}
