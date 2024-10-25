package chess;

import java.util.Objects;
/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
//initialize variables, this is easy, they are given as parameters already in ChessMove constructor, thank you
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;


    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        //now just assign, couldn't be more straight forward
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    //I think I'll use this later in Phase 1
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    //I think I'll use this later in Phase 1
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    //Ill use this later in Phase 1, just gotta return for now
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

//Java can generate these 3 for me, phew
    @Override
    public String toString() {
        return "Move from " + startPosition.toString() + " to " + endPosition.toString() +
                (promotionPiece != null ? ", promote to " + promotionPiece : "");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof ChessMove)) {return false;}
        ChessMove move = (ChessMove) o;
        return Objects.equals(startPosition, move.startPosition) &&
                Objects.equals(endPosition, move.endPosition) &&
                promotionPiece == move.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }
}
