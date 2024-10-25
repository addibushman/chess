package results;

import java.util.List;

public class ListGamesResult {
    private boolean success;
    private String message;
    private List<String> games;  // Assuming it's a list of game IDs or descriptions

    public ListGamesResult(boolean success, String message, List<String> games) {
        this.success = success;
        this.message = message;
        this.games = games;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getGames() {
        return games;
    }
}

