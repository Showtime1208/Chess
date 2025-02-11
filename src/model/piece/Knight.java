package model.piece;

import java.util.List;
import model.board.Board;

import java.awt.*;

public class Knight implements ChessPiece{
    private final boolean isWhite;
    private int row;
    private int col;
    @Override
    public List<Point> getValidMoves(Board boardState) {
        return null;
    }

    public Knight(boolean isWhite, int row, int col) {
        this.isWhite = isWhite;
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean isWhite() {
        return isWhite;
    }

    @Override
    public int getPointValue() {
        return 3;
    }

    @Override
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
