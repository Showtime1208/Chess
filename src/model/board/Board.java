package model.board;

import model.piece.ChessPiece;



public interface Board {

  String toString();

  ChessPiece[][] getBoard();

  ChessPiece get(int row, int col);

  void set(int row, int col, ChessPiece piece);

  void removePiece(int row, int col);

  void movePiece(int startRow, int startCol, int endRow, int endCol);

  int[] getScore();


  Board makeCopy();
}
