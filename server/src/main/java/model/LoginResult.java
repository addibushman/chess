package model;

import com.google.gson.Gson;

public class LoginResult {
    private String username;
    private String authToken;
    private boolean success;

    public LoginResult(String username, String authToken, boolean success) {
        this.username = username;
        this.authToken = authToken;
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

