package websocket.messages;

import chess.ChessGame;
import java.util.Objects;

public class ServerMessage {
    ServerMessageType serverMessageType;

    String message;
    ChessGame game;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessage(ServerMessageType type, ChessGame game) {
        this.serverMessageType = type;
        this.game = game;
    }

    public ServerMessage(ServerMessageType type, String message) {
        this.serverMessageType = type;
        this.message = message;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String getMessage() {
        return message;
    }

    public ChessGame getGame() {
        return this.game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerMessage)) return false;
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }

    // Subclasses for specific message types

    public static class LoadGameMessage extends ServerMessage {
        public LoadGameMessage(ChessGame game) {
            super(ServerMessageType.LOAD_GAME, game);
        }

        public ChessGame getGame() {
            return this.game;
        }
    }

    public static class ErrorMessage extends ServerMessage {
        public ErrorMessage(String errorMessage) {
            super(ServerMessageType.ERROR, errorMessage);
        }

        public String getErrorMessage() {
            return this.message;
        }
    }

    public static class NotificationMessage extends ServerMessage {
        public NotificationMessage(String message) {
            super(ServerMessageType.NOTIFICATION, message);
        }

        public String getNotificationMessage() {
            return this.message;
        }
    }
}
