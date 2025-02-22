package view;

import model.board.ChessBoard;

public class ChessView {
  private ChessBoard board;

  public ChessView(ChessBoard board) {
    if (board == null) {
      throw new IllegalArgumentException("Board cannot be null.");
    }
  }

}
