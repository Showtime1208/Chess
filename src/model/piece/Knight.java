package model.piece;

import model.board.Board;

import java.awt.*;

public class Knight implements ChessPiece{
    private final boolean isWhite;
    @Override
    public List getValidMoves(Board boardState) {
        return null;
    }

    public Knight(boolean isWhite) {
        this.isWhite = isWhite;
    }

    @Override
    public boolean isWhite() {
        return isWhite;
    }

    @Override
    public int getPointValue() {
        return 3;
    }
}
