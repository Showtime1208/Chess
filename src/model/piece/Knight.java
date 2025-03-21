package model.piece;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

import java.awt.*;
import model.board.ChessBoard;

public class Knight implements ChessPiece {

  private final boolean isWhite;
  private int row;
  private int col;

  @Override
  public ImageIcon getIcon() {
    String string = isWhite ? "white" : "black";
    return new ImageIcon("pieceImages" + File.separator
        + string + "Knight.png");
  }

  @Override
  public List<Point> getValidMoves(ChessBoard boardState) {
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
            if (occupant == null || occupant.isWhite() != this.isWhite) {
              validMoves.add(new Point(newRow, newCol));
            }
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

  @Override
  public Point getPosition() {
    return new Point(this.row, this.col);
  }

  @Override
  public ChessPiece clone() {
    return new Knight(this.isWhite, this.row, this.col);
  }
}
