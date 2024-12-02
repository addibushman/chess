package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.MySQLAuthTokenDAO;
import model.AuthData;
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

                boolean isValidMove = validateMove(game, move, session, command.getAuthToken());
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

    //black is attempting to control white or reverse, add in a check to see if the move
    // that's wanting to be made is the same color as the turn

    //need to make sure person who is calling make move, is the person who is authorized to do it

    //make sure username associated with auth token is the same as the person who is making the move

    private boolean validateMove(GameData game, ChessMove move, Session session, String authToken) {
        try {
            System.out.println("Validating move for game: " + game.getGameID() + " with move: " + move.toString());

            ChessGame chessGame = getGameInstanceById(game.getGameID());
            if (chessGame == null) {
                System.out.println("Error: No game found with the given gameID.");
                sendMessage(session, new ServerMessage.ErrorMessage("Invalid Game ID"));
                return false;
            }

            // Get the current player's color (whoever turn it is)
            ChessGame.TeamColor currentPlayerColor = chessGame.getTeamTurn();
            System.out.println("Current player color: " + currentPlayerColor);

            // Get the player's username
            String username = getUsernameFromAuthToken(authToken);
            if (username == null) {
                System.out.println("Error: Invalid or missing username.");
                sendMessage(session, new ServerMessage.ErrorMessage("Invalid username"));
                return false;
            }

            // Verify if the current player has the correct color based on their username (this should work)
            if (currentPlayerColor == ChessGame.TeamColor.WHITE && !username.equals(game.getWhiteUsername())) {
                System.out.println("Error: It's White's turn, but the player is not White.");
                sendMessage(session, new ServerMessage.ErrorMessage("It's White's turn"));
                return false;
            } else if (currentPlayerColor == ChessGame.TeamColor.BLACK && !username.equals(game.getBlackUsername())) {
                System.out.println("Error: It's Black's turn, but the player is not Black.");
                sendMessage(session, new ServerMessage.ErrorMessage("It's Black's turn"));
                return false;
            }

            // move piece
            ChessPiece piece = chessGame.getBoard().getPiece(move.getStartPosition());
            if (piece == null) {
                System.out.println("Error: No piece at the start position.");
                sendMessage(session, new ServerMessage.ErrorMessage("No piece at start position"));
                return false;
            }

            // Check if the piece's color matches the player's turn
            if (piece.getTeamColor() != currentPlayerColor) {
                System.out.println("Error: Player is trying to move the opponent's piece or it's not their turn.");
                sendMessage(session, new ServerMessage.ErrorMessage("It's not your turn or you cannot move your opponent's piece"));
                return false;
            }

            // Check if the move is valid for the piece
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

    private String getUsernameFromAuthToken(String authTokenString) throws DataAccessException {
        //returning null, not working. I should be able to use a class I already have
        //AuthToken token = AuthData.getInstance().getAuthToken(authTokenString);
        MySQLAuthTokenDAO authTokenDAO = DaoService.getInstance().getAuthDAO();

        AuthToken token = authTokenDAO.getAuthToken(authTokenString);
        if (token != null) {
            return token.getUsername();
        } else {
            System.out.println("No username found for this auth token: " + authTokenString);
            return null;
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
            return DaoService.getInstance().getGameDAO().getGameByID(String.valueOf(gameID));
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
