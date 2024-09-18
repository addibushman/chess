package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessMovesCalculator {

    public static Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        switch (piece.getPieceType()) {
            case KING:
                return calculateKingMoves(board, position, piece);
            case QUEEN:
                return calculateQueenMoves(board, position, piece);
            case ROOK:
                return calculateRookMoves(board, position, piece);
            case BISHOP:
                return calculateBishopMoves(board, position, piece);
            case KNIGHT:
                return calculateKnightMoves(board, position, piece);
            case PAWN:
                return calculatePawnMoves(board, position, piece);
            default:
                throw new IllegalArgumentException("Unknown piece type: " + piece.getPieceType());
        }
    }

    // KING move calculation
    private static Collection<ChessMove> calculateKingMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
        };

        for (int[] dir : directions) {
            int newRow = position.getRow() + dir[0];
            int newCol = position.getColumn() + dir[1];

            if (isInBounds(newRow, newCol)) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece targetPiece = board.getPiece(newPos);
                if (targetPiece == null || targetPiece.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(position, newPos, null));
                }
            }
        }
        return moves;
    }

    // QUEEN move calculation
    private static Collection<ChessMove> calculateQueenMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        moves.addAll(calculateRookMoves(board, position, piece));
        moves.addAll(calculateBishopMoves(board, position, piece));
        return moves;
    }

    // ROOK move calculation
    private static Collection<ChessMove> calculateRookMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] dir : directions) {
            addDirectionalMoves(board, position, piece, moves, dir[0], dir[1]);
        }
        return moves;
    }

    // BISHOP move calculation
    private static Collection<ChessMove> calculateBishopMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Correct the direction order
        int[][] directions = {
                {-1, 1}, {-1, -1}, {1, 1}, {1, -1}
        };

        for (int[] dir : directions) {
            addDirectionalMoves(board, position, piece, moves, dir[0], dir[1]);

        }
        return moves;

    }

    // KNIGHT move calculation
    private static Collection<ChessMove> calculateKnightMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] knightMoves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] move : knightMoves) {
            int newRow = position.getRow() + move[0];
            int newCol = position.getColumn() + move[1];

            if (isInBounds(newRow, newCol)) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece targetPiece = board.getPiece(newPos);
                if (targetPiece == null || targetPiece.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(position, newPos, null));
                }
            }
        }
        return moves;
    }

    // PAWN move calculation
    private static Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int row = position.getRow();
        int col = position.getColumn();

        ChessPosition oneStepForward = new ChessPosition(row + direction, col);
        if (board.getPiece(oneStepForward) == null) {
            moves.add(new ChessMove(position, oneStepForward, null));

            // Two-step forward (only first move for a pawn can do this)
            if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 2) ||
                    (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 7)) {
                ChessPosition twoStepsForward = new ChessPosition(row + 2 * direction, col);
                if (board.getPiece(twoStepsForward) == null) {
                    moves.add(new ChessMove(position, twoStepsForward, null));
                }
            }
        }

        // Diagonal captures
        int[][] diagonals = {{direction, 1}, {direction, -1}};
        for (int[] diag : diagonals) {
            int newRow = row + diag[0];
            int newCol = col + diag[1];
            if (isInBounds(newRow, newCol)) {
                ChessPosition diagPos = new ChessPosition(newRow, newCol);
                ChessPiece targetPiece = board.getPiece(diagPos);
                if (targetPiece != null && targetPiece.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(position, diagPos, null));
                }
            }
        }

        return moves;
    }




    // Helper to check board boundaries
    private static boolean isInBounds(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }



    // Helper to add directional moves
    private static void addDirectionalMoves(ChessBoard board, ChessPosition position, ChessPiece piece,
                                            Collection<ChessMove> moves, int rowDelta, int colDelta) {
        int row = position.getRow();  // Start from current row
        int col = position.getColumn();  // Start from current column

        while (true) {
            // Apply the deltas to move in the specified direction
            //switching between - and + cause its going in the wrong direction
            row -= rowDelta;
            col -= colDelta;

            // Check if the new row and column are within bounds (on the board)
            if (!isInBounds(row, col)) {
                break;  // Stop if out of bounds
            }


            // Create the new position based on the updated row and col
            ChessPosition newPos = new ChessPosition(row, col);
            ChessPiece targetPiece = board.getPiece(newPos);  // Check if there's a piece at the new position

            if (targetPiece == null) {
                // No piece in the target position, add move
                moves.add(new ChessMove(position, newPos, null));
            } else {
                // There's a piece, check if it's an enemy
                if (targetPiece.getTeamColor() != piece.getTeamColor()) {
                    // Enemy piece, capture is allowed
                    moves.add(new ChessMove(position, newPos, null));
                }
                // Stop the loop after hitting any piece (friendly or enemy)
                break;
            }

        }

    }
}