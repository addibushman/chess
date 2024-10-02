package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.Arrays;
import java.util.ArrayList;

public class ChessMovesCalculator {
    public static Collection<ChessMove> calculateValidChessMoves(ChessBoard board, ChessPosition position, ChessPiece piece){
        switch(piece.getPieceType()){
            case KING:
                return calculateKingMoves(board, position, piece);
            case QUEEN:
                return calculateQueenMoves(board, position, piece);
            case BISHOP:
                return calculateBishopMoves(board, position, piece);
            case KNIGHT:
                return calculateKnightMoves(board, position, piece);
            case ROOK:
                return calculateRookMoves(board, position, piece);
            case PAWN:
                return calculatePawnMoves(board, position, piece);
             //keep going at the end
            default:
                throw new IllegalArgumentException("Unknown Piece Type:"+ piece.getPieceType());
        }
    }
    private static Collection<ChessMove> calculateKingMoves(ChessBoard board, ChessPosition position, ChessPiece piece){
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {1, 1}, {1, 0}, {1, -1}, {0, 1}, {0, -1}, {-1, 1}, {-1, 0}, {-1, -1}
        };
        for (int[] dir : directions){
            int row = position.getRow() + dir[0];
            int col = position.getColumn() + dir[1];

            if(isOnBoard(row, col)){
                ChessPosition newPos = new ChessPosition(row, col);
                ChessPiece targetPiece = board.getPiece(newPos);

                if (targetPiece == null || targetPiece.getTeamColor() != piece.getTeamColor()){
                    moves.add(new ChessMove(position, newPos, null));
                }
            }

        }
        return moves;
    }

    private static Collection<ChessMove> calculateKnightMoves(ChessBoard board, ChessPosition position, ChessPiece piece){
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}
        };
        for (int[] dir : directions){
            int row = position.getRow() + dir[0];
            int col = position.getColumn() + dir[1];

            if(isOnBoard(row, col)){
                ChessPosition newPos = new ChessPosition(row, col);
                ChessPiece targetPiece = board.getPiece(newPos);

                if (targetPiece == null || targetPiece.getTeamColor() != piece.getTeamColor()){
                    moves.add(new ChessMove(position, newPos, null));
                }
            }

        }
        return moves;
    }

    private static Collection<ChessMove> calculateBishopMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        for (int[] dir : directions){
            addMoveLength(board, position, piece, moves, dir[0], dir[1]);

        }
        return moves;
    }

    private static Collection<ChessMove> calculateRookMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {0, 1}, {0, -1}, {1, 0}, {-1, 0}
        };
        for (int[] dir : directions){
            addMoveLength(board, position, piece, moves, dir[0], dir[1]);

        }
        return moves;
    }

    private static Collection<ChessMove> calculateQueenMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        moves.addAll(calculateRookMoves(board, position, piece));
        moves.addAll(calculateBishopMoves(board, position, piece));
        return moves;
    }

    private static Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1: -1;
        int row = position.getRow();
        int col = position.getColumn();

        ChessPosition oneStepForward = new ChessPosition(row + direction, col);
        if (board.getPiece(oneStepForward) == null){
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && oneStepForward.getRow() == 8 ||
                    piece.getTeamColor() == ChessGame.TeamColor.BLACK && oneStepForward.getRow() == 1){
                moves.add(new ChessMove(position, oneStepForward, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(position, oneStepForward, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(position, oneStepForward, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(position, oneStepForward, ChessPiece.PieceType.KNIGHT));
            } else{
                moves.add(new ChessMove(position,oneStepForward, null));

            }
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 2 ||
                    piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 7){
                ChessPosition twoStepsForward = new ChessPosition(row + 2 * direction, col);
                if (board.getPiece(twoStepsForward) == null){
                    moves.add(new ChessMove(position,twoStepsForward, null));

                }
            }
        }
        int [][] diagonals = {{direction, 1}, {direction, -1}};
        for (int[] diag : diagonals){
            int newRow = row + diag[0];
            int newCol = col + diag[1];

            if(isOnBoard(newRow, newCol)){
                ChessPosition diagPos = new ChessPosition(newRow, newCol);
                ChessPiece targetPiece = board.getPiece(diagPos);

                if (targetPiece != null && targetPiece.getTeamColor() != piece.getTeamColor()){
                    if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && newRow == 8) ||
                            (piece.getTeamColor() == ChessGame.TeamColor.BLACK && newRow == 1)){
                        moves.add(new ChessMove(position, diagPos, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(position, diagPos, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(position, diagPos, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(position, diagPos, ChessPiece.PieceType.KNIGHT));

                    } else{
                        moves.add(new ChessMove(position,diagPos, null));
                    }
                }
            }
        }
        return moves;

    }


    private static boolean isOnBoard(int row, int col){
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
    private static void addMoveLength(ChessBoard board, ChessPosition position, ChessPiece piece,
                                      Collection<ChessMove> moves, int rowChange, int colChange) {
        int row = position.getRow();
        int col = position.getColumn();

        while (true){
            row -= rowChange;
            col -= colChange;

            if (!isOnBoard(row, col)){
                break;
            }
            ChessPosition newPos = new ChessPosition(row, col);
            ChessPiece targetPiece = board.getPiece(newPos);

            if (targetPiece == null){
                moves.add(new ChessMove(position, newPos, null));
            } else{
                if(targetPiece.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(position, newPos, null));
                }
                break;
            }
        }
    }
}