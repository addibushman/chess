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
            AuthToken authToken = null;
            try {
                authToken = DaoService.getInstance().getAuthDAO().getAuthToken(command.getAuthToken());
                if (authToken == null) {
                    sendMessage(session, new ServerMessage.ErrorMessage("Invalid AuthToken"));
                    return;
                }
            } catch (DataAccessException e) {
                System.out.println("Error verifying auth token: " + e.getMessage());
                sendMessage(session, new ServerMessage.ErrorMessage("Error verifying auth token"));
                return;
            }

            if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
                System.out.println("Client " + session.getRemoteAddress() + " connected to the game.");
                addSessionToGame(command.getGameID(), session);

                GameData game = getGameByID(command.getGameID());
                if (game == null) {
                    sendMessage(session, new ServerMessage.ErrorMessage("Invalid Game ID"));
                    return;
                }

                ServerMessage serverMessage = new ServerMessage.LoadGameMessage(game);
                sendMessage(session, serverMessage);

                ServerMessage notificationServerMessage = new ServerMessage.NotificationMessage("Player has connected: " + session.getRemoteAddress());
                sendMessageToOthers(command.getGameID(), session, notificationServerMessage);
            }

            else if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
                ChessMove move = command.getMove();
                GameData game = getGameByID(command.getGameID());

                if (game == null) {
                    sendMessage(session, new ServerMessage.ErrorMessage("Invalid Game ID"));
                    return;
                }

                boolean isValidMove = validateMove(game, move, session);
                if (!isValidMove) {
                    return;
                }

                updateGameState(game, move);

                ServerMessage loadGameMessage = new ServerMessage.LoadGameMessage(game);
                sendMessageToAll(command.getGameID(), loadGameMessage);

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
            System.out.println("Validating move for game: " + game.getGameID() + " with move: " + move.toString());

            ChessGame chessGame = getGameInstanceById(game.getGameID());
            if (chessGame == null) {
                System.out.println("Error: No game found with the given gameID.");
                sendMessage(session, new ServerMessage.ErrorMessage("Invalid Game ID"));
                return false;
            }

            ChessGame.TeamColor currentPlayerColor = chessGame.getTeamTurn();
            System.out.println("Current player color: " + currentPlayerColor);

            ChessPiece piece = chessGame.getBoard().getPiece(move.getStartPosition());
            if (piece == null) {
                System.out.println("Error: No piece at the start position.");
                sendMessage(session, new ServerMessage.ErrorMessage("No piece at start position"));
                return false;
            }

            if (piece.getTeamColor() != currentPlayerColor) {
                System.out.println("Error: Player is trying to move the opponent's piece or it's not their turn.");
                sendMessage(session, new ServerMessage.ErrorMessage("It's not your turn or you cannot move your opponent's piece"));
                return false;
            }

            Collection<ChessMove> validMoves = piece.pieceMoves(chessGame.getBoard(), move.getStartPosition());
            if (!validMoves.contains(move)) {
                System.out.println("Error: Invalid move for this piece.");
                sendMessage(session, new ServerMessage.ErrorMessage("Invalid Move"));
                return false;
            }

            if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                if (isRookBlocked(chessGame.getBoard(), move)) {
                    System.out.println("Error: Rook path is blocked.");
                    sendMessage(session, new ServerMessage.ErrorMessage("Rook path is blocked"));
                    return false;
                }
            }

            System.out.println("Move validation passed.");
            return true;
        } catch (Exception e) {
            System.out.println("Error in validateMove: " + e.getMessage());
            return false;
        }
    }


    private ChessGame getGameInstanceById(String gameID) {
        try {
            GameData gameData = DaoService.getInstance().getGameDAO().getGameByID(gameID);  // Fetch game data
            if (gameData != null) {
                ChessGame chessGame = new ChessGame();
                return chessGame;
            }
        } catch (Exception e) {
            System.out.println("Error retrieving game: " + e.getMessage());
        }
        return null;
    }



    private boolean isRookBlocked(ChessBoard board, ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        int row = start.getRow() - 1;
        int col = start.getColumn() - 1;

        int deltaRow = Integer.compare(end.getRow(), start.getRow());
        int deltaCol = Integer.compare(end.getColumn(), start.getColumn());

        if (deltaRow == 0) {
            for (int i = col + deltaCol; i != end.getColumn() - 1; i += deltaCol) {
                if (board.getPiece(new ChessPosition(row + 1, i + 1)) != null) {
                    return true;
                }
            }
        }
        else if (deltaCol == 0) {
            for (int i = row + deltaRow; i != end.getRow() - 1; i += deltaRow) {
                if (board.getPiece(new ChessPosition(i + 1, col + 1)) != null) {
                    return true;
                }
            }
        }

        return false;
    }



    private void updateGameState(GameData game, ChessMove move) {
        System.out.println("Move made: " + move);
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
