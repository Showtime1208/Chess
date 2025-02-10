package model.piece;

public class ChessPiece implements Piece {
    private ChessPieceVals vals;
    private final boolean isWhite;
    public ChessPiece(ChessPieceVals vals, boolean isWhite) {
        if (vals == null) {
            throw new IllegalArgumentException("Vals cannot be null.");
        }
        this.vals = vals;
        this.isWhite = isWhite;
    }

    @Override
    public String toString() {
        if (isWhite) {
            return "W" + vals.getNameString();
        }
        return  "B" + vals.getNameString();
    }

    @Override
    public int getPointVals() {
        return vals.getPointVal();
    }



}
