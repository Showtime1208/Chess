package model.piece;

import model.board.Board;

import java.awt.*;

public class Pawn implements ChessPiece {
    private final boolean isWhite;

    public Pawn(boolean isWhite) {
        this.isWhite = isWhite;
    }


    @Override
    public List getValidMoves(Board boardState) {
        return null;
    }

    @Override
    public boolean isWhite() {
        return isWhite;
    }

    @Override
    public int getPointValue() {
        return 1;
    }
}
