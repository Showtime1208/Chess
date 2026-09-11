package view;

import model.board.ChessBoard;

/** Black's perspective, using the same rendering and input mapping as the main view. */
public class InvertedChessBoardFrame extends ChessBoardFrame {
  private static final long serialVersionUID = 1L;
  public InvertedChessBoardFrame(ChessBoard model) { super(model, true); }
}
