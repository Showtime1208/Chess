package controller;

import java.awt.Point;
import java.util.Collections;
import model.board.ChessBoard;
import model.piece.ChessPiece;
import model.piece.Pawn;
import view.ChessView;

public class ChessController implements Controller {
  private final ChessBoard board;
  private final ChessView view;
  private int selectedRow = -1, selectedCol = -1;

  public ChessController(ChessBoard board, ChessView view) {
    if (board == null || view == null) throw new IllegalArgumentException("Board and view are required.");
    this.board = board; this.view = view;
  }
  public void playGame() { board.startGame(); clearSelection(); }
  public void undoMove() { board.undoMove(); clearSelection(); }
  public void claimDraw() { board.claimDraw(); clearSelection(); }
  private void clearSelection() {
    selectedRow = selectedCol = -1;
    view.setSelection(-1, -1);
    view.highlightMoves(Collections.emptyList());
    view.update();
  }
  public void handleSquareClick(int row, int col) {
    if (row < 0 || row > 7 || col < 0 || col > 7 || board.isGameOver()) return;
    ChessPiece piece = board.get(row, col);
    if (row == selectedRow && col == selectedCol) { clearSelection(); return; }
    if (piece != null && piece.isWhite() == board.getTurn()) {
      selectedRow = row; selectedCol = col;
      view.setSelection(row, col);
      view.highlightMoves(board.getLegalMoves(row, col));
      view.update();
      return;
    }
    if (selectedRow < 0) return;
    if (!board.getLegalMoves(selectedRow, selectedCol).contains(new Point(row, col))) {
      view.showMessage("Choose a highlighted square, or select another piece.");
      return;
    }
    ChessPiece moving = board.get(selectedRow, selectedCol);
    char promotion = 'Q';
    if (moving instanceof Pawn && (row == 0 || row == 7)) {
      promotion = view.choosePromotion(moving.isWhite());
      if (promotion == 0) return;
    }
    board.movePiece(selectedRow, selectedCol, row, col, promotion);
    clearSelection();
  }
}
