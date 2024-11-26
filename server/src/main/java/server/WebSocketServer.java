package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import spark.Spark;
import websocket.commands.UserGameCommand;

import javax.websocket.CloseReason;
import java.util.*;

@WebSocket
public class WebSocketServer {

    private static final Map<Integer, Set<Session>> gameSessions = new HashMap<>();
    private Gson gson = new Gson();

//    public static void main(String[] args) {
//        // Set the WebSocket endpoints here (this is where something is wrong)
//        Spark.port(8080);
//        Spark.webSocket("/ws", WebSocketServer.class);
//        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
//        System.out.println("WebSocket server started at ws://localhost:8080/ws");
//    }

//    @OnWebSocketConnect
//    public void onOpen(Session session) {
//        System.out.println("Client connected: " + session.getRemoteAddress());
//    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.println("Received message from client: " + message);

        UserGameCommand command = parseCommand(message);

        if (command != null) {
            if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
                System.out.println("Client " + session.getRemoteAddress() + " connected to the game.");
                addSessionToGame(command.getGameID(), session);
                sendMessage(session, "Successfully connected to the game.");
            }
            else if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
                System.out.println("Player made a move: " + message);
                broadcastMessage(command.getGameID(), "Move made: " + message);
            }
            else if (command.getCommandType() == UserGameCommand.CommandType.LEAVE) {
                System.out.println("Client " + session.getRemoteAddress() + " is leaving the game.");
                removeSessionFromGame(session);
                sendMessage(session, "You have left the game.");
            }
            else if (command.getCommandType() == UserGameCommand.CommandType.RESIGN) {
                System.out.println("Client " + session.getRemoteAddress() + " has resigned.");
                broadcastMessage(command.getGameID(), "Player has resigned.");
            }
        } else {
            System.out.println("Invalid command received: " + message);
            sendMessage(session, "Error: Invalid command");
        }
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

    private void sendMessage(Session session, String message) throws Exception {
        session.getRemote().sendString(message);
        System.out.println("Sent message to client: " + message);
    }

    private void broadcastMessage(Integer gameID, String message) throws Exception {
        Set<Session> sessions = gameSessions.get(gameID);
        if (sessions != null) {
            for (Session s : sessions) {
                s.getRemote().sendString(message);
                System.out.println("Broadcasting message to game " + gameID + ": " + message);
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
