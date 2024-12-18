package results;

public class JoinGameResult {
    private boolean success;
    private String message;

    public JoinGameResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}

