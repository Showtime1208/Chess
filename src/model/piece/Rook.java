package model.piece;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import model.board.Board;

public class Rook implements ChessPiece {

  private final boolean isWhite;
  private int row;
  private int col;
  private boolean hasMoved;

  public Rook(boolean isWhite, int row, int col) {
    this.isWhite = isWhite;
    this.row = row;
    this.col = col;
  }

  @Override
  public void setPosition(int row, int col) {
    this.row = row;
    this.col = col;
  }

  @Override
  public List<Point> getValidMoves(Board boardState) {
    List<Point> validMoves = new ArrayList<>();
    //Going up
    for (int newRow = row - 1; newRow >= 0 ; newRow--) {
      if (!isInBounds(newRow, col)) break;
      ChessPiece occupant = boardState.get(newRow, col);
      if (occupant == null) {
        validMoves.add(new Point(newRow, col));
      } else {
        if (occupant.isWhite() != this.isWhite) {
          validMoves.add(new Point(newRow, col));
        }
        break;
      }
    }
    //Going down
    for (int newRow = row + 1; newRow < 8 ; newRow++) {
      if (!isInBounds(newRow, col)) break;
      ChessPiece occupant = boardState.get(newRow, col);
      if (occupant == null) {
        validMoves.add(new Point(newRow, col));
      } else {
        if (occupant.isWhite() != this.isWhite) {
          validMoves.add(new Point(newRow, col));
        }
        break;
      }
    }
    //going left
    for (int newCol = col - 1; newCol >= 0 ; newCol--) {
      if (!isInBounds(row, newCol)) break;
      ChessPiece occupant = boardState.get(row, newCol);
      if (occupant == null) {
        validMoves.add(new Point(row, newCol));
      } else {
        if (occupant.isWhite() != this.isWhite) {
          validMoves.add(new Point(row, newCol));
        }
        break;
      }
    }
    //going right
    for (int newCol = col + 1; newCol < 8 ; newCol++) {
      if (!isInBounds(row, newCol)) break;
      ChessPiece occupant = boardState.get(row, newCol);
      if (occupant == null) {
        validMoves.add(new Point(row, newCol));
      } else {
        if (occupant.isWhite() != this.isWhite) {
          validMoves.add(new Point(row, newCol));
        }
        break;
      }
    }
    return validMoves;
  }

  private boolean isInBounds(int row, int col) {
    return (row >= 0 && row <= 7) && (col >= 0 && col <= 7);
  }

  @Override
  public boolean isWhite() {
    return isWhite;
  }

  @Override
  public int getPointValue() {
    return 5;
  }

  public boolean isHasMoved() {
    return hasMoved;
  }
}
