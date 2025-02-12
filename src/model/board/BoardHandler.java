package model.board;

import model.piece.ChessPiece;

public class BoardHandler implements BoardHandlerInterface {
  private final ChessPiece[][] board;
  public final int size = 8;

  public BoardHandler() {
    this.board = new ChessBoard().getBoard();
  }


  @Override
  public void set(int row, int col, ChessPiece piece) {
    if (isInBounds(row, col) && piece != null){
      board[row][col] = piece;
    } else throw new IllegalArgumentException("Out of bounds input.");

  }

  @Override
  public void removePiece(int row, int col) {
    if (isInBounds(row, col) && board[row][col] != null) {
      board[row][col] = null;
    } else throw new IllegalArgumentException("Out of bounds input.");

  }

  @Override
  public ChessPiece get(int row, int col) {
    if (isInBounds(row, col)) {
      return board[row][col];
    }
    else throw new IllegalArgumentException("Out of bounds input.");
  }

  private boolean isInBounds(int row, int col) {
    return (row >= 0 && row <= 7) && (col >= 0 && col <= 7);
  }



}
