package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;
    private boolean whiteHasResigned;
    private boolean blackHasResigned;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
        this.whiteHasResigned = false;  // Initially no one has resigned
        this.blackHasResigned = false;
    }

    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    public enum TeamColor {
        WHITE,
        BLACK
    }

    public boolean getWhiteResigned() {
        return whiteHasResigned;
    }

    public void setWhiteResigned(boolean hasResigned) {
        this.whiteHasResigned = hasResigned;
    }

    public boolean getBlackResigned() {
        return blackHasResigned;
    }

    public void setBlackResigned(boolean hasResigned) {
        this.blackHasResigned = hasResigned;
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : possibleMoves) {
            ChessBoard tempBoard = copyBoard();
            makeTemporaryMove(tempBoard, move);

            if (!isInCheck(piece.getTeamColor(), tempBoard)) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());

        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not your turn");
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }

        executeMove(move);
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, this.board);
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return getAllTeamMoves(teamColor).isEmpty();
    }

    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return getAllTeamMoves(teamColor).isEmpty();
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessBoard getBoard() {
        return board;
    }

    private boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        ChessPosition kingPosition = findTheKing(teamColor, board);
        if (kingPosition == null) {
            return false;
        }

        TeamColor opponentColor = getOpponentColor(teamColor);

        return isKingThreatened(kingPosition, opponentColor, board);
    }

    private boolean isKingThreatened(ChessPosition kingPosition, TeamColor opponentColor, ChessBoard board) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (isOpponentPiece(piece, opponentColor) && threatensKing(piece, position, kingPosition, board)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isOpponentPiece(ChessPiece piece, TeamColor opponentColor) {
        return piece != null && piece.getTeamColor() == opponentColor;
    }

    private boolean threatensKing(ChessPiece piece, ChessPosition startPosition, ChessPosition kingPosition, ChessBoard board) {
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    private TeamColor getOpponentColor(TeamColor teamColor) {
        return (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    private ChessPosition findTheKing(TeamColor teamColor, ChessBoard board) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition kingPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(kingPosition);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return kingPosition;
                }
            }
        }
        return null;
    }

    private Collection<ChessMove> getAllTeamMoves(TeamColor teamColor) {
        Collection<ChessMove> allMoves = new ArrayList<>();

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> pieceMoves = validMoves(position);
                    if (pieceMoves != null) {
                        allMoves.addAll(pieceMoves);
                    }
                }
            }
        }
        return allMoves;
    }

    private ChessBoard copyBoard() {
        ChessBoard newBoard = new ChessBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null) {
                    newBoard.addPiece(position, piece);
                }
            }
        }
        return newBoard;
    }

    private void makeTemporaryMove(ChessBoard tempBoard, ChessMove move) {
        ChessPiece tempPiece = tempBoard.getPiece(move.getStartPosition());
        tempBoard.addPiece(move.getEndPosition(), tempPiece);
        tempBoard.addPiece(move.getStartPosition(), null);
    }

    private void executeMove(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", teamTurn=" + teamTurn +
                '}';
    }
}