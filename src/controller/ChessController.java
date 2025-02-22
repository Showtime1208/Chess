package controller;

import model.board.ChessBoard;
import view.ChessView;

public class ChessController {
  private ChessBoard board;
  private ChessView view;

  private Readable rd;
  private Appendable ap;

  public ChessController(Readable rd, Appendable ap) {
    if (rd == null || ap == null) {
      throw new IllegalArgumentException("rd/ap cannot be null");
    }
    this.rd = rd;
    this.ap = ap;
  }

  public void startGame(ChessBoard board, ChessView view) {
    if (board == null || view == null) {
      throw new IllegalArgumentException("Model/view cannot be null");
    }
    this.board = board;
    this.view = view;
  }

}
