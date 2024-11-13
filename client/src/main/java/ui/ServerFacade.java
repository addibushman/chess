package ui;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.AuthToken;
import requests.*;
import results.RegisterResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ServerFacade {
    private final String baseUrl;
    private final HttpClient client;
    private final Gson gson;

    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    //Register
    public AuthToken register(String username, String password, String email) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        String requestBody = gson.toJson(registerRequest);

        //http url connection, look at slides and web api page
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            RegisterResult registerResult = gson.fromJson(response.body(), RegisterResult.class);
            if (registerResult.isSuccess()) {
                return new AuthToken(registerResult.getAuthToken(), registerResult.getUsername());
            } else {
                throw new Exception("Registration failed: " + registerResult.getMessage());
            }
        } else {
            throw new Exception("Registration failed: " + response.body());
        }
    }
    //next login

}