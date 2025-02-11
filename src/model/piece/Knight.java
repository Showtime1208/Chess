package model.piece;

import java.util.ArrayList;
import java.util.List;
import model.board.Board;

import java.awt.*;

public class Knight implements ChessPiece {

  private final boolean isWhite;
  private int row;
  private int col;

  @Override
  public List<Point> getValidMoves(Board boardState) {
      List<Point> validMoves = new ArrayList<>();
      int[][] offsets = {
          { 2,  1},
          { 2, -1},
          {-2,  1},
          {-2, -1},
          { 1,  2},
          { 1, -2},
          {-1,  2},
          {-1, -2}
      };
      for (int[] offset : offsets) {
          int newRow = row + offset[0];
          int newCol = col + offset[1];
          if (isInBounds(newRow, newCol)) {
              ChessPiece occupant = boardState.get(newRow, newCol);
          }
      }
      return validMoves;
  }

    private boolean isInBounds(int row, int col) {
        return (row >= 0 && row <= 7) && (col >= 0 && col <= 7);
    }

  public Knight(boolean isWhite, int row, int col) {
    this.isWhite = isWhite;
    this.row = row;
    this.col = col;
  }

  @Override
  public boolean isWhite() {
    return isWhite;
  }

  @Override
  public int getPointValue() {
    return 3;
  }

  @Override
  public void setPosition(int row, int col) {
    this.row = row;
    this.col = col;
  }
}
