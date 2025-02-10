package model.piece;

import model.board.Board;

import java.awt.*;

public class Queen implements ChessPiece {

    private final boolean isWhite;

    public Queen(boolean isWhite) {
        this.isWhite = isWhite;
    }

    @Override
    public List getValidMoves(Board boardState) {
        return null;
    }

    @Override
    public boolean isWhite() {
        return false;
    }

    @Override
    public int getPointValue() {
        return 9;
    }
}
