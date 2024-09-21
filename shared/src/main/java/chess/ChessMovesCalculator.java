package chess;
//don't forget to import okay
import java.util.ArrayList;
import java.util.Collection;

public class ChessMovesCalculator {
//think of a slide, directly going to what it needs to do, faster than if statements, hopefully this will make the programming exam easier
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

    //order pieces from most to least important (easier to think about so I don't miss one)

    // KING move calculation
    private static Collection<ChessMove> calculateKingMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
        };
// this is doing an array of arrays, so you can go from 2 dimensions to 1, making it easier to digest
        for (int[] dir : directions) {
            int newRow = position.getRow() + dir[0];
            int newCol = position.getColumn() + dir[1];

            if (isOnBoard(newRow, newCol)) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece targetPiece = board.getPiece(newPos);
                if (targetPiece == null || targetPiece.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(position, newPos, null));
                }
            }
        }
        return moves;
    }

    //order pieces from most to least important
    // QUEEN move calculation, mix of ROOK and BISHOP, make this easier for yourself
    private static Collection<ChessMove> calculateQueenMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        moves.addAll(calculateRookMoves(board, position, piece));
        moves.addAll(calculateBishopMoves(board, position, piece));
        return moves;
    }

    // ROOK move calculation, straight in all direction
    private static Collection<ChessMove> calculateRookMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] dir : directions) {
            addMoveLength(board, position, piece, moves, dir[0], dir[1]);
        }
        return moves;
    }

    // BISHOP move calculation, all diagonal
    private static Collection<ChessMove> calculateBishopMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Direction order don't matter heck ya, that makes it easier
        int[][] directions = {
                {-1, 1}, {-1, -1}, {1, 1}, {1, -1}
        };

        for (int[] dir : directions) {
            addMoveLength(board, position, piece, moves, dir[0], dir[1]);

        }
        return moves;

    }

    // KNIGHT move calculation, this is the funky one, 3 over 1 up, and all variations of this
    private static Collection<ChessMove> calculateKnightMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] knightMoves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] move : knightMoves) {
            int newRow = position.getRow() + move[0];
            int newCol = position.getColumn() + move[1];

            if (isOnBoard(newRow, newCol)) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece targetPiece = board.getPiece(newPos);
                if (targetPiece == null || targetPiece.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(position, newPos, null));
                }
            }
        }
        return moves;
    }

    //PAWN move calculation woo wooo, this is supposed to be the hard one
    private static Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int row = position.getRow();
        int col = position.getColumn();

        ChessPosition oneStepForward = new ChessPosition(row + direction, col);
        if (board.getPiece(oneStepForward) == null) {
            // Check for promotion if moving to the final row on board
            if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && oneStepForward.getRow() == 8) ||
                    (piece.getTeamColor() == ChessGame.TeamColor.BLACK && oneStepForward.getRow() == 1)) {
                // Promotion move
                moves.add(new ChessMove(position, oneStepForward, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(position, oneStepForward, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(position, oneStepForward, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(position, oneStepForward, ChessPiece.PieceType.BISHOP));
            } else {
                // Normal move if not promotion, also do I need this part or am I reiterating too much?
                moves.add(new ChessMove(position, oneStepForward, null));
            }

            // Two-step forward (only for pawn's first move)
            if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 2) ||
                    (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 7)) {
                ChessPosition twoStepsForward = new ChessPosition(row + 2 * direction, col);
                if (board.getPiece(twoStepsForward) == null) {
                    moves.add(new ChessMove(position, twoStepsForward, null));
                }
            }
        }

        // Pawn can capture Diagonally (and be a potential promotion too)
        int[][] diagonals = {{direction, 1}, {direction, -1}};
        for (int[] diag : diagonals) {
            int newRow = row + diag[0];
            int newCol = col + diag[1];
            if (isOnBoard(newRow, newCol)) {
                ChessPosition diagPos = new ChessPosition(newRow, newCol);
                ChessPiece targetPiece = board.getPiece(diagPos);
                if (targetPiece != null && targetPiece.getTeamColor() != piece.getTeamColor()) {
                    if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && newRow == 8) ||
                            (piece.getTeamColor() == ChessGame.TeamColor.BLACK && newRow == 1)) {
                        // Promotion to be any piece, how do I do that??
                        moves.add(new ChessMove(position, diagPos, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(position, diagPos, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(position, diagPos, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(position, diagPos, ChessPiece.PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(position, diagPos, null));
                    }
                }
            }
        }

        return moves;
    }
    // Helper function to check board boundaries (to know when to stop, or to know if it is a valid move)
    private static boolean isOnBoard(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }


    // Helper to add directional moves, this way you can pick direction and not worry about how many to do each time
    private static void addMoveLength(ChessBoard board, ChessPosition position, ChessPiece piece,
                                            Collection<ChessMove> moves, int rowChange, int colChange) {
        int row = position.getRow();
        int col = position.getColumn();

        while (true) {
            row -= rowChange;
            col -= colChange;

            // Check if the new row and column are on the board before setting position to be there
            if (!isOnBoard(row, col)) {
                break;
            }


            // Create the new position based given updated row and col
            ChessPosition newPos = new ChessPosition(row, col);
            ChessPiece proposedPiecePosition = board.getPiece(newPos);  // Check if there's a piece at the position you wanna go to

            if (proposedPiecePosition == null) {
                // No piece there, you are so good to add move
                moves.add(new ChessMove(position, newPos, null));
            } else {
                // There's a piece at that position you wanna go to, check if it's the enemy
                if (proposedPiecePosition.getTeamColor() != piece.getTeamColor()) {
                    // Enemy piece, capture is allowed heck ya
                    moves.add(new ChessMove(position, newPos, null));
                }
                break;
            }

        }

    }
}