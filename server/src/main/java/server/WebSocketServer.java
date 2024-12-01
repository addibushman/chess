package server;

import chess.*;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.DaoService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import model.AuthToken;
import dataaccess.DataAccessException;

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

                boolean isValidMove = validateMove(game, move, session);
                if (!isValidMove) {
                    return; // Invalid move (Error message will already be sent by validateMove)
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

    private boolean validateMove(GameData game, ChessMove move, Session session) {
        try {
            // Retrieve the game instance and the current turn
            ChessGame chessGame = getGameInstanceById(game.getGameID());

            if (chessGame == null) {
                sendMessage(session, new ServerMessage.ErrorMessage("Invalid Game ID"));
                return false;
            }

            // Get the current player's color (whose turn it is)
            ChessGame.TeamColor currentPlayerColor = chessGame.getTeamTurn(); // Assuming this method returns the current player's color

            // Get the piece to move
            ChessPiece piece = chessGame.getBoard().getPiece(move.getStartPosition());
            if (piece == null) {
                sendMessage(session, new ServerMessage.ErrorMessage("No piece at start position"));
                return false;  // No piece at the start position
            }

            // Check if the current player is trying to move out of turn
            if (piece.getTeamColor() != currentPlayerColor) {
                sendMessage(session, new ServerMessage.ErrorMessage("It's not your turn"));
                return false; // The player is trying to make a move out of turn
            }

            // Check if the move is valid for the piece
            Collection<ChessMove> validMoves = piece.pieceMoves(chessGame.getBoard(), move.getStartPosition());
            if (!validMoves.contains(move)) {
                sendMessage(session, new ServerMessage.ErrorMessage("Invalid Move"));
                return false;  // The move is not valid for this piece
            }

            // Check if the rook's path is blocked (if the piece is a rook)
            if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                if (isRookBlocked(chessGame.getBoard(), move)) {
                    sendMessage(session, new ServerMessage.ErrorMessage("Rook path is blocked"));
                    return false; // Rook's path is blocked
                }
            }

            return true; // All checks passed, the move is valid
        } catch (Exception e) {
            // Handle the exception (e.g., log it or rethrow it)
            System.out.println("Error in validateMove: " + e.getMessage());
            return false; // Return false to indicate that the move is not valid
        }
    }


    private ChessGame getGameInstanceById(String gameID) {
        try {
            // Assuming DaoService can give you the game data from the database or in-memory storage
            GameData gameData = DaoService.getInstance().getGameDAO().getGameByID(gameID);  // Fetch game data
            if (gameData != null) {
                ChessGame chessGame = new ChessGame();  // Create a new ChessGame instance
                // Populate the chessGame instance using gameData (you can customize this part as needed)
                // For example, initialize the board based on gameData or other logic
                return chessGame;  // Return the game instance
            }
        } catch (Exception e) {
            System.out.println("Error retrieving game: " + e.getMessage());
        }
        return null;  // Return null if no game was found
    }



    // Helper method to check if the rook's path is blocked
    private boolean isRookBlocked(ChessBoard board, ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        int row = start.getRow() - 1;  // Adjust for 0-indexed array
        int col = start.getColumn() - 1;

        int deltaRow = Integer.compare(end.getRow(), start.getRow());  // -1 for up, 1 for down, 0 if horizontal move
        int deltaCol = Integer.compare(end.getColumn(), start.getColumn());  // -1 for left, 1 for right, 0 if vertical move

        // Check if the rook is moving horizontally
        if (deltaRow == 0) {
            // Move left or right
            for (int i = col + deltaCol; i != end.getColumn() - 1; i += deltaCol) {
                if (board.getPiece(new ChessPosition(row + 1, i + 1)) != null) {
                    return true;  // A piece is blocking the path
                }
            }
        }
        // Check if the rook is moving vertically
        else if (deltaCol == 0) {
            // Move up or down
            for (int i = row + deltaRow; i != end.getRow() - 1; i += deltaRow) {
                if (board.getPiece(new ChessPosition(i + 1, col + 1)) != null) {
                    return true;  // A piece is blocking the path
                }
            }
        }

        return false;  // No blockage found
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
