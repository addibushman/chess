package server;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.*;

@WebSocket
public class WebSocketServer {

    private static final Map<Integer, Set<Session>> gameSessions = new HashMap<>();
    private Gson gson = new Gson();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.println("Received message from client: " + message);

        UserGameCommand command = parseCommand(message);

        if (command != null) {
            if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
                // Log connection and add session to the game
                System.out.println("Client " + session.getRemoteAddress() + " connected to the game.");
                addSessionToGame(command.getGameID(), session);

                ChessGame game = getGameByID(command.getGameID());

                // Create the LOAD_GAME message to send back to the client
                ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
                sendMessage(session, serverMessage);

                // Create the NOTIFICATION message to send to other clients (excluding the root user)
                ServerMessage notificationServerMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Player has connected: " + session.getRemoteAddress());

                // Send notification to everyone else in the game except the root user
                sendMessageToOthers(command.getGameID(), session, notificationServerMessage);
            }
        } else {
            System.out.println("Invalid command received: " + message);
            sendMessage(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR));
        }
    }

    private ChessGame getGameByID(int gameID) {
        return new ChessGame(); // Returning a dummy game for now
    }

    private void addSessionToGame(Integer gameID, Session session) {
        gameSessions.putIfAbsent(gameID, new HashSet<>());
        gameSessions.get(gameID).add(session);
        System.out.println("Session added to game " + gameID);
    }

    private void removeSessionFromGame(Session session) {
        gameSessions.forEach((gameID, sessions) -> {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                gameSessions.remove(gameID);
            }
        });
        System.out.println("Session removed from all games");
    }

    // Method to send a message to a session
    private void sendMessage(Session session, ServerMessage message) throws Exception {
        String jsonMessage = gson.toJson(message);
        session.getRemote().sendString(jsonMessage);
        System.out.println("Sent message to client: " + jsonMessage);
    }

    // New method to send a message to everyone in the game except the root user (the session)
    private void sendMessageToOthers(Integer gameID, Session rootSession, ServerMessage message) throws Exception {
        Set<Session> sessions = gameSessions.get(gameID);
        if (sessions != null) {
            for (Session s : sessions) {
                if (!s.equals(rootSession)) { // Do not send message to the root session
                    s.getRemote().sendString(gson.toJson(message));
                    System.out.println("Sent notification to other client in game " + gameID + ": " + gson.toJson(message));
                }
            }
        }
    }

    private UserGameCommand parseCommand(String message) {
        try {
            return gson.fromJson(message, UserGameCommand.class);
        } catch (Exception e) {
            System.out.println("Error parsing command: " + e.getMessage());
            return null;
        }
    }
}
