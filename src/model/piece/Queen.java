package model.piece;

import java.util.ArrayList;
import java.util.List;
import model.board.Board;

import java.awt.*;

public class Queen implements ChessPiece {

  private final boolean isWhite;
  private int row;
  private int col;

  public Queen(boolean isWhite, int row, int col) {
    this.isWhite = isWhite;
    this.row = row;
    this.col = col;
  }

  @Override
  public List<Point> getValidMoves(Board boardState) {
    List<Point> validMoves = new ArrayList<>();
    for (int nRow = -1; nRow <= 1; nRow++) {
      for (int nCol = -1; nCol <= 1; nCol++) {
        if (nRow == 0 && nCol == 0) {
          continue;
        }
        int newRow = row + nRow;
        int newCol = col + nCol;
        if (isInBounds(newRow, newCol)) {
          ChessPiece squareOccupant = boardState.get(newRow, newCol);
          if (squareOccupant == null || squareOccupant.isWhite() != this.isWhite) {
            validMoves.add(new Point(row, col));
          }
        }
      }
    }
    return validMoves;
  }

  private boolean isInBounds(int row, int col) {
    return (row >= 0 && row <= 7) && (col >= 0 && col <= 7);
  }


  @Override
  public void setPosition(int row, int col) {
    this.row = row;
    this.col = col;
  }

  @Override
  public boolean isWhite() {
    return false;
  }

  @Override
  public int getPointValue() {
    return 9;
  }
}
