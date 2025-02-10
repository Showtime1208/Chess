package model.board;

import model.piece.Piece;

public interface Board {
    String toString();

    Piece[][] getBoard();
}
