package ui;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.AuthToken;
import requests.*;
import results.LoginResult;
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
    public AuthToken login(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(username, password);

        String requestBody = gson.toJson(loginRequest);
        System.out.println("Login request body: " + requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Debugging
        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        if (response.statusCode() == 200) {
            LoginResult loginResult = gson.fromJson(response.body(), LoginResult.class);
            if (loginResult.isSuccess()) {
                return new AuthToken(loginResult.getAuthToken(), loginResult.getUsername());
            } else {
                throw new Exception("Login failed: " + loginResult.getMessage());
            }
        } else {
            throw new Exception("Login failed with status code " + response.statusCode() + ": " + response.body());
        }
    }
}