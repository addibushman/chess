package ui;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.AuthToken;
import requests.*;

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

    // Register first
    public AuthToken register(String username, String password, String email) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        String requestBody = gson.toJson(registerRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
            String setToken = jsonResponse.get("token").getAsString();
            String setUsername = jsonResponse.get("username").getAsString();
            return new AuthToken(setToken, setUsername);
        } else {
            throw new Exception("Registration failed: " + response.body());
        }
    }
    //next login

}