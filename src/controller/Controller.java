package controller;

public interface Controller {
  void handleSquareClick(int row, int col);
  void playGame();
  void undoMove();
  void claimDraw();
}
