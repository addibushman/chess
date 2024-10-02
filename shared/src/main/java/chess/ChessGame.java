package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
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

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    //check for piece, if its your turn to even go, and if it is a valid move
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

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, this.board);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        return getAllTeamMoves(teamColor).isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    //need to know what stalemate means
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        return getAllTeamMoves(teamColor).isEmpty();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    // start of extra credit
    private ChessPosition lastDoublePawnMove = null;
    private boolean[][] hasMoved = new boolean[8][8]; // Track if pieces have moved

    // Modify your resetBoard method to also reset piece movement tracking
    public void resetBoard() {
        board = new ChessBoard();
        board.resetBoard();
        hasMoved = new boolean[8][8];
        lastDoublePawnMove = null;
    }

    // Add to your validMoves method
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        Collection<ChessMove> possibleMoves = new ArrayList<>(piece.pieceMoves(board, startPosition));

        // Add castling moves for King
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            possibleMoves.addAll(getCastlingMoves(startPosition, piece.getTeamColor()));
        }

        // Add en passant moves for Pawns
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            possibleMoves.addAll(getEnPassantMoves(startPosition, piece.getTeamColor()));
        }
        return null;
    }

    // Add these methods to your ChessGame class
    private Collection<ChessMove> getCastlingMoves(ChessPosition kingPosition, TeamColor color) {
        Collection<ChessMove> castlingMoves = new ArrayList<>();
        int row = kingPosition.getRow();

        // Check if king has moved
        if (hasMoved[row - 1][4]) return castlingMoves;

        // Check if king is in check
        if (isInCheck(color)) return castlingMoves;

        // Try kingside castling
        if (!hasMoved[row - 1][7] && canCastle(kingPosition, true)) {
            castlingMoves.add(new ChessMove(kingPosition, new ChessPosition(row, 7), null));
        }

        // Try queenside castling
        if (!hasMoved[row - 1][0] && canCastle(kingPosition, false)) {
            castlingMoves.add(new ChessMove(kingPosition, new ChessPosition(row, 3), null));
        }

        return castlingMoves;
    }

    private boolean canCastle(ChessPosition kingPosition, boolean kingSide) {
        int row = kingPosition.getRow();
        int startCol = kingSide ? 6 : 1;
        int endCol = kingSide ? 7 : 4;

        // Check if squares between king and rook are empty
        for (int col = startCol; col < endCol; col++) {
            if (board.getPiece(new ChessPosition(row, col)) != null) {
                return false;
            }
        }

        // Check if king passes through check
        TeamColor color = board.getPiece(kingPosition).getTeamColor();
        ChessBoard tempBoard = copyBoard();
        int direction = kingSide ? 1 : -1;
        for (int i = 1; i <= 2; i++) {
            ChessPosition newKingPos = new ChessPosition(row, kingPosition.getColumn() + (i * direction));
            tempBoard.addPiece(kingPosition, null);
            tempBoard.addPiece(newKingPos, board.getPiece(kingPosition));
            if (isInCheck(color, tempBoard)) {
                return false;
            }
        }

        return true;
    }

    private Collection<ChessMove> getEnPassantMoves(ChessPosition pawnPosition, TeamColor color) {
        Collection<ChessMove> enPassantMoves = new ArrayList<>();

        if (lastDoublePawnMove == null) return enPassantMoves;

        int direction = (color == TeamColor.WHITE) ? 1 : -1;
        int row = pawnPosition.getRow();
        int col = pawnPosition.getColumn();

        // Check if our pawn is in the right position
        if ((color == TeamColor.WHITE && row != 5) || (color == TeamColor.BLACK && row != 4)) {
            return enPassantMoves;
        }

        // Check adjacent columns for enemy pawns that just moved
        for (int colOffset : new int[]{-1, 1}) {
            if (col + colOffset >= 1 && col + colOffset <= 8) {
                if (lastDoublePawnMove.getColumn() == col + colOffset &&
                        lastDoublePawnMove.getRow() == row) {
                    ChessPosition endPos = new ChessPosition(row + direction, col + colOffset);
                    enPassantMoves.add(new ChessMove(pawnPosition, endPos, null));
                }
            }
        }

        return enPassantMoves;
    }


    private void handleCastling(ChessMove move) {
        int row = move.getStartPosition().getRow();
        int rookStartCol = move.getEndPosition().getColumn() == 7 ? 8 : 1;
        int rookEndCol = move.getEndPosition().getColumn() == 7 ? 6 : 4;

        // Move king
        board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
        board.addPiece(move.getStartPosition(), null);

        // Move rook
        ChessPosition rookStart = new ChessPosition(row, rookStartCol);
        ChessPosition rookEnd = new ChessPosition(row, rookEndCol);
        board.addPiece(rookEnd, board.getPiece(rookStart));
        board.addPiece(rookStart, null);
    }

    private void handleEnPassant(ChessMove move) {
        // Move the pawn
        board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
        board.addPiece(move.getStartPosition(), null);

        // Remove the captured pawn
        int capturedPawnRow = move.getStartPosition().getRow();
        int capturedPawnCol = move.getEndPosition().getColumn();
        board.addPiece(new ChessPosition(capturedPawnRow, capturedPawnCol), null);
    }
//end of extra credit
    // Helper methods here and below
    private boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        //need to check all possibilites here
        ChessPosition kingPosition = findKing(teamColor, board);
        TeamColor opponentColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece != null && piece.getTeamColor() == opponentColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, newPosition);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private ChessPosition findKing(TeamColor teamColor, ChessBoard board) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition kingPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(kingPosition);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING
                        && piece.getTeamColor() == teamColor) {
                    return kingPosition;
                }
            }
        }
        return null;

    }

    private Collection<ChessMove> getAllTeamMoves(TeamColor teamColor) {
        Collection<ChessMove> alltheMoves = new ArrayList<>();

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> pieceMoves = validMoves(position);
                    if (pieceMoves != null) {
                        alltheMoves.addAll(pieceMoves);
                    }
                }
            }
        }
        return alltheMoves;
    }
//make new board with new possible moves on it
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
    //temp move here
    private void makeTemporaryMove(ChessBoard tempBoard, ChessMove move) {
        ChessPiece tempPiece = tempBoard.getPiece(move.getStartPosition());
        tempBoard.addPiece(move.getEndPosition(), tempPiece);
        tempBoard.addPiece(move.getStartPosition(), null);
    }
//now implement when you can actually do the move, implemented on board
    private void executeMove(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);
    }
//add in overrides here just in case for debugging purposes
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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