package results;

public class LogoutResult {
    private boolean success;
    private String message;

    public LogoutResult(boolean success, String message) {
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
