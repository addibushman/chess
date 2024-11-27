package server;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.DaoService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import model.AuthToken;
import dataaccess.DataAccessException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@WebSocket
public class WebSocketServer {

    private static final Map<Integer, Set<Session>> gameSessions = new HashMap<>();
    private Gson gson = new Gson();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.println("Received message from client: " + message);

        UserGameCommand command = parseCommand(message);

        if (command != null) {
            // Check for valid authToken
            AuthToken authToken = null;
            try {
                authToken = DaoService.getInstance().getAuthDAO().getAuthToken(command.getAuthToken());
                if (authToken == null) {
                    // Send error message if the auth token is invalid
                    sendMessage(session, new ServerMessage.ErrorMessage("Invalid AuthToken"));
                    return; // Don't proceed further if the authToken is invalid
                }
            } catch (DataAccessException e) {
                System.out.println("Error verifying auth token: " + e.getMessage());
                sendMessage(session, new ServerMessage.ErrorMessage("Error verifying auth token"));
                return;
            }

            // Handle CONNECT command
            if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
                // Log connection and add session to the game
                System.out.println("Client " + session.getRemoteAddress() + " connected to the game.");
                addSessionToGame(command.getGameID(), session);

                // Check for valid gameID
                GameData game = getGameByID(command.getGameID());
                if (game == null) {
                    sendMessage(session, new ServerMessage.ErrorMessage("Invalid Game ID"));
                    return; // Return early if game not found
                }

                ServerMessage serverMessage = new ServerMessage.LoadGameMessage(game);
                sendMessage(session, serverMessage);

                ServerMessage notificationServerMessage = new ServerMessage.NotificationMessage("Player has connected: " + session.getRemoteAddress());
                sendMessageToOthers(command.getGameID(), session, notificationServerMessage);
            }

            // Handle MAKE_MOVE command
            else if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
                // Handle the move
                ChessMove move = command.getMove();
                GameData game = getGameByID(command.getGameID());

                // Validate the move and game
                if (game == null) {
                    sendMessage(session, new ServerMessage.ErrorMessage("Invalid Game ID"));
                    return;
                }

                boolean isValidMove = validateMove(game, move);
                if (!isValidMove) {
                    sendMessage(session, new ServerMessage.ErrorMessage("Invalid Move"));
                    return;
                }

                // Update the game with the move
                updateGameState(game, move);

                // Send updated game state to all clients
                ServerMessage loadGameMessage = new ServerMessage.LoadGameMessage(game);
                sendMessageToAll(command.getGameID(), loadGameMessage);

                // Send notification to all other clients about the move
                String notificationMessage = "Move made by: " + session.getRemoteAddress();
                sendMessageToOthers(command.getGameID(), session, new ServerMessage.NotificationMessage(notificationMessage));
            }

        } else {
            System.out.println("Invalid command received: " + message);
            sendMessage(session, new ServerMessage.ErrorMessage("Invalid command"));
        }
    }

    private boolean validateMove(GameData game, ChessMove move) {
        // Add move validation logic here. For example, checking if the move follows chess rules.
        // For now, this is just a placeholder returning true.
        return true;
    }

    private void updateGameState(GameData game, ChessMove move) {
        // Update the game with the new move. This could involve updating the board, checking for checkmate, etc.
        System.out.println("Move made: " + move);
        // Example: update the game object with the move (you may need to add more logic here).
    }

    private void sendMessageToAll(Integer gameID, ServerMessage message) throws Exception {
        Set<Session> sessions = gameSessions.get(gameID);
        if (sessions != null) {
            for (Session s : sessions) {
                s.getRemote().sendString(gson.toJson(message));
                System.out.println("Sent message to all clients in game " + gameID + ": " + gson.toJson(message));
            }
        }
    }

    private GameData getGameByID(int gameID) {
        try {
            return DaoService.getInstance().getGameDAO().getGameByID(String.valueOf(gameID)); // Ensure gameID is String
        } catch (Exception e) {
            System.out.println("Error retrieving game: " + e.getMessage());
        }
        return null;
    }

    private void addSessionToGame(Integer gameID, Session session) {
        gameSessions.putIfAbsent(gameID, new HashSet<>());
        gameSessions.get(gameID).add(session);
        System.out.println("Session added to game " + gameID);
    }

    private void sendMessage(Session session, ServerMessage message) throws Exception {
        String jsonMessage = gson.toJson(message);
        session.getRemote().sendString(jsonMessage);
        System.out.println("Sent message to client: " + jsonMessage);
    }

    private void sendMessageToOthers(Integer gameID, Session rootSession, ServerMessage message) throws Exception {
        Set<Session> sessions = gameSessions.get(gameID);
        if (sessions != null) {
            for (Session s : sessions) {
                if (!s.equals(rootSession)) {
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
