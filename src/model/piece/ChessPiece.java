package model.piece;

import model.board.Board;

import java.awt.*;

/**
 * Piece interface.
 */
public interface ChessPiece {

    String toString();

    List getValidMoves(Board boardState);

    boolean isWhite();

    int getPointValue();
}
