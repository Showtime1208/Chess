package model.board;

import model.piece.ChessPiece;


public interface Board {
    String toString();

    ChessPiece[][] getBoard();
}
