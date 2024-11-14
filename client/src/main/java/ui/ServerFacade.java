package ui;

import com.google.gson.*;
import model.AuthToken;
import model.GameData;
import requests.*;
import results.*;
import requests.CreateGameRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
            String errorMessage = extractErrorMessage(response.body());
            throw new Exception("Registration failed: " + errorMessage);
        }
    }

    //method to make sure JSON doesn't print out, but only the error message within
    private String extractErrorMessage(String responseBody) {
        try {
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonElement messageElement = json.get("message");
            if (messageElement != null) {
                return messageElement.getAsString();
            }
        } catch (JsonSyntaxException e) {
            return responseBody;
        }
        return "An unknown error occurred";
    }


    //next login
    public AuthToken login(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(username, password);
        String requestBody = gson.toJson(loginRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            LoginResult loginResult = gson.fromJson(response.body(), LoginResult.class);
            if (loginResult.isSuccess()) {
                return new AuthToken(loginResult.getAuthToken(), loginResult.getUsername());
            } else {
                throw new Exception("Login failed: " + loginResult.getMessage());
            }
        } else {
            String errorMessage = extractErrorMessage(response.body());
            throw new Exception("Login failed: " + errorMessage);
        }
    }

    // Logout
    public void logout(AuthToken token) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/session"))
                .header("Content-Type", "application/json")
                .header("Authorization", token.getToken())
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Logout failed: " + response.body());
        }
    }

    public String createGame(String gameName, AuthToken token) throws Exception {
        if (gameName == null || gameName.trim().isEmpty()) {
            throw new Exception("Failed to create game: Error: Game name cannot be empty");
        }


        CreateGameRequest createGameRequest = new CreateGameRequest(gameName, token.getToken());
        String requestBody = gson.toJson(createGameRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/game"))
                .header("Content-Type", "application/json")
                .header("Authorization", token.getToken())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to create game: " + response.body());
        }

        return response.body();
    }

    //list games
    public List<GameData> listGames(AuthToken token) throws Exception {
        ListGamesRequest listGamesRequest = new ListGamesRequest(token.getToken());
        String requestBody = gson.toJson(listGamesRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/game"))
                .header("Content-Type", "application/json")
                .header("Authorization", token.getToken())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ListGamesResult result = gson.fromJson(response.body(), ListGamesResult.class);
            return result.getGames();
        } else {
            throw new Exception("Failed to retrieve games: " + response.body());
        }
    }
    public void joinGame(String gameID, String playerColor, AuthToken token) throws Exception {
        JoinGameRequest joinGameRequest = new JoinGameRequest(playerColor, gameID, token.getToken());
        String requestBody = gson.toJson(joinGameRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/game"))
                .header("Content-Type", "application/json")
                .header("Authorization", token.getToken())
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
        } else {
            String errorMessage = extractErrorMessage(response.body());
            throw new Exception("Failed to join game: " + errorMessage);
        }
    }
    //observe game

    public void observeGame(String gameID, AuthToken token) throws Exception {
        String requestBody = gson.toJson(new JoinGameRequest("OBSERVING", gameID, token.getToken()));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/game"))
                .header("Content-Type", "application/json")
                .header("Authorization", token.getToken())
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to observe game: " + response.body());
        }
    }
}