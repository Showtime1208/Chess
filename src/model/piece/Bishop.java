package model.piece;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

import java.awt.*;
import model.board.ChessBoard;

public class Bishop implements ChessPiece {

  private final boolean isWhite;
  private int row;
  private int col;

  public Bishop(boolean isWhite, int row, int col) {
    this.isWhite = isWhite;
    this.row = row;
    this.col = col;
  }

  @Override
  public ImageIcon getIcon() {
    String string = isWhite ? "white" : "black";
    return new ImageIcon("pieceImages" + File.separator
        + string + "Bishop.png");

  }

  @Override
  public void setPosition(int row, int col) {
    if (isInBounds(row, col)) {
      this.row = row;
      this.col = col;
    } else {
      throw new IllegalArgumentException("Out of bounds for setPosition.");
    }
  }

  @Override
  public List<Point> getValidMoves(ChessBoard boardState) {
    List<Point> validMoves = new ArrayList<>();
    //down and right
    for (int newRow = row + 1; newRow < 8; newRow++) {
      for (int newCol = col + 1; newCol < 8; newCol++) {
        if (!isInBounds(newRow, newCol)) break;
        ChessPiece occupant = boardState.get(newRow, newCol);
        if (occupant == null) {
          validMoves.add(new Point(newRow, newCol));
        } else {
          if (occupant.isWhite() != this.isWhite) {
            validMoves.add(new Point(newRow, newCol));
          }
          break;
        }
      }
    }
    //up and right
    for (int newRow = row - 1; newRow >= 0; newRow--) {
      for (int newCol = col + 1; newCol < 9; newCol++) {
        if (!isInBounds(newRow, newCol)) break;
        ChessPiece occupant = boardState.get(newRow, newCol);
        if (occupant == null) {
          validMoves.add(new Point(newRow, newCol));
        } else {
          if (occupant.isWhite() != this.isWhite) {
            validMoves.add(new Point(newRow, newCol));
          }
          break;
        }
      }
    }
    //down and left
    for (int newRow = row + 1; newRow < 8; newRow++) {
      for (int newCol = col - 1; newCol >= 0; newCol--) {
        if (!isInBounds(newRow, newCol)) break;
        ChessPiece occupant = boardState.get(newRow, newCol);
        if (occupant == null) {
          validMoves.add(new Point(newRow, newCol));
        } else {
          if (occupant.isWhite() != this.isWhite) {
            validMoves.add(new Point(newRow, newCol));
          }
          break;
        }
      }
    }
    //down and right
    for (int newRow = row - 1; newRow >= 0; newRow--) {
      for (int newCol = col - 1; newCol >= 0; newCol--) {
        if (!isInBounds(newRow, newCol)) break;
        ChessPiece occupant = boardState.get(newRow, newCol);
        if (occupant == null) {
          validMoves.add(new Point(newRow, newCol));
        } else {
          if (occupant.isWhite() != this.isWhite) {
            validMoves.add(new Point(newRow, newCol));
          }
          break;
        }
      }
    }
    return validMoves;
  }

  @Override
  public Point getPosition() {
    return new Point(this.row, this.col);
  }

  @Override
  public ChessPiece clone() {
    return new Bishop(isWhite, row, col);
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
    return 3;
  }
}
