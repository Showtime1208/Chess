package model.piece;

public enum ChessPieceVals {
    KING(100, "K"),
    QUEEN(8, "Q"),
    ROOK(5, "R"),
    KNIGHT(3, "N"),
    BISHOP(3, "B"),
    PAWN(1, "P");

    private final int pointVal;
    private final String nameString;

    ChessPieceVals(int pointVal, String nameString) {
        this.pointVal = pointVal;
        this.nameString = nameString;
    }
    public int getPointVal() {
        return pointVal;
    }
    public String getNameString() {
        return nameString;
    }
}
