package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessMovesCalculator {

    public static Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        switch (piece.getPieceType()) {
            case KING:
                return calculateMovesInDirections(board, position, piece, new int[][]{
                        {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
                }, 1);
            case QUEEN:
                return calculateMovesInDirections(board, position, piece, new int[][]{
                        {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
                }, Integer.MAX_VALUE);
            case ROOK:
                return calculateMovesInDirections(board, position, piece, new int[][]{
                        {1, 0}, {-1, 0}, {0, 1}, {0, -1}
                }, Integer.MAX_VALUE);
            case BISHOP:
                return calculateMovesInDirections(board, position, piece, new int[][]{
                        {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
                }, Integer.MAX_VALUE);
            case KNIGHT:
                return calculateMovesInDirections(board, position, piece, new int[][]{
                        {2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
                }, 1);
            case PAWN:
                return calculatePawnMoves(board, position, piece);
            default:
                throw new IllegalArgumentException("Unknown piece type: " + piece.getPieceType());
        }
    }

    private static Collection<ChessMove> calculateMovesInDirections(ChessBoard board, ChessPosition position,
                                                                    ChessPiece piece, int[][] directions, int maxSteps) {
        Collection<ChessMove> moves = new ArrayList<>();
        for (int[] dir : directions) {
            int row = position.getRow();
            int col = position.getColumn();
            int steps = 0;

            while (steps < maxSteps) {
                row += dir[0];
                col += dir[1];
                if (!isOnBoard(row, col)) {
                    break;
                }

                ChessPosition newPos = new ChessPosition(row, col);
                ChessPiece targetPiece = board.getPiece(newPos);
                if (targetPiece == null) {
                    moves.add(new ChessMove(position, newPos, null));
                } else {
                    if (targetPiece.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(position, newPos, null));
                    }
                    break;
                }
                steps++;
            }
        }
        return moves;
    }

    private static Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int row = position.getRow();
        int col = position.getColumn();

        ChessPosition oneStepForward = new ChessPosition(row + direction, col);
        if (isOnBoard(oneStepForward.getRow(), oneStepForward.getColumn()) && board.getPiece(oneStepForward) == null) {
            handlePawnPromotion(moves, position, oneStepForward, piece);

            if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 2) ||
                    (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 7)) {
                ChessPosition twoStepsForward = new ChessPosition(row + 2 * direction, col);
                if (isOnBoard(twoStepsForward.getRow(), twoStepsForward.getColumn()) && board.getPiece(twoStepsForward) == null) {
                    moves.add(new ChessMove(position, twoStepsForward, null));
                }
            }
        }

        int[][] diagonals = {{direction, 1}, {direction, -1}};
        for (int[] diag : diagonals) {
            int newRow = row + diag[0];
            int newCol = col + diag[1];
            if (isOnBoard(newRow, newCol)) {
                ChessPosition diagPos = new ChessPosition(newRow, newCol);
                ChessPiece targetPiece = board.getPiece(diagPos);
                if (targetPiece != null && targetPiece.getTeamColor() != piece.getTeamColor()) {
                    handlePawnPromotion(moves, position, diagPos, piece);
                }
            }
        }
        return moves;
    }

    private static void handlePawnPromotion(Collection<ChessMove> moves, ChessPosition startPos, ChessPosition endPos, ChessPiece piece) {
        int endRow = endPos.getRow();
        if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && endRow == 8) ||
                (piece.getTeamColor() == ChessGame.TeamColor.BLACK && endRow == 1)) {
            moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(startPos, endPos, null));
        }
    }

    private static boolean isOnBoard(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}
