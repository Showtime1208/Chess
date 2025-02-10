package model.piece;

import model.board.Board;

import java.awt.*;

public class Queen implements ChessPiece {

    private final boolean isWhite;
    private int row;
    private int col;

    public Queen(boolean isWhite, int row, int col) {
        this.isWhite = isWhite;
        this.row = row;
        this.col = col;
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
