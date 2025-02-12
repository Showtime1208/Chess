package model.board;

import model.piece.ChessPiece;

public interface BoardHandlerInterface {
  ChessPiece get(int row, int col);

  void set(int row, int col, ChessPiece piece);

  void removePiece(int row, int col);

}
