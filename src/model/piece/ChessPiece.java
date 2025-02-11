package model.piece;

import java.util.List;
import model.board.Board;

import java.awt.*;

/**
 * Piece interface.
 */
public interface ChessPiece {

    String toString();

    List<Point> getValidMoves(Board boardState);

    boolean isWhite();

    int getPointValue();

    void setPosition(int row, int col);
}
