package model.piece;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import javax.swing.ImageIcon;
import model.board.ChessBoard;

public class King implements ChessPiece {

  private final boolean isWhite;
  private int row;
  private int col;
  private boolean hasMoved;

  public King(boolean isWhite, int row, int col) {
    this.isWhite = isWhite;
    this.row = row;
    this.col = col;
  }

  @Override
  public ImageIcon getIcon() {
    String string = isWhite ? "white" : "black";
    return new ImageIcon("pieceImages" + File.separator
        + string + File.separator +  "King.png");

  }

  @Override
  public List<Point> getValidMoves(ChessBoard boardState) {
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
            validMoves.add(new Point(newRow, newCol));
          }
        }
      }
    }
    addCastlingMoves(boardState, validMoves);
    return validMoves;
  }

  private void addCastlingMoves(ChessBoard boardState, List<Point> validMoves) {
    if (!this.hasMoved) {
      // === KING-SIDE ===
      ChessPiece rightRook = boardState.get(row, 7);
      if (rightRook instanceof Rook) {
        Rook rook = (Rook) rightRook;
        if (rook.isWhite() == this.isWhite && !rook.isHasMoved()) {
          if (boardState.get(row, 5) == null && boardState.get(row, 6) == null) {
            boolean kingSquareSafe  = !boardState.isUnderAttack(this.isWhite, new Point(row, col));
            boolean passSquare5Safe = !boardState.isUnderAttack(this.isWhite, new Point(row, 5));
            boolean passSquare6Safe = !boardState.isUnderAttack(this.isWhite, new Point(row, 6));
            if (kingSquareSafe && passSquare5Safe && passSquare6Safe) {
              validMoves.add(new Point(row, col + 2));
            }
          }
        }
      }

      // === QUEEN-SIDE ===
      ChessPiece leftRook = boardState.get(row, 0);
      if (leftRook instanceof Rook) {
        Rook rook = (Rook) leftRook;
        if (rook.isWhite() == this.isWhite && !rook.isHasMoved()) {
          if (boardState.get(row, 1) == null
              && boardState.get(row, 2) == null
              && boardState.get(row, 3) == null) {

            boolean kingSquareSafe  = !boardState.isUnderAttack(this.isWhite, new Point(row, col));
            boolean passSquare1Safe = !boardState.isUnderAttack(this.isWhite, new Point(row, 1));
            boolean passSquare2Safe = !boardState.isUnderAttack(this.isWhite, new Point(row, 2));
            boolean passSquare3Safe = !boardState.isUnderAttack(this.isWhite, new Point(row, 3));
            if (kingSquareSafe && passSquare1Safe && passSquare2Safe && passSquare3Safe) {
              validMoves.add(new Point(row, col - 2));
            }
          }
        }
      }
    }
  }

  private boolean isInBounds(int row, int col) {
    return (row >= 0 && row <= 7) && (col >= 0 && col <= 7);
  }

  @Override
  public boolean isWhite() {
    return isWhite;
  }

  @Override
  public void setPosition(int row, int col) {
    this.row = row;
    this.col = col;
  }

  @Override
  public int getPointValue() {
    return 999;
  }

  @Override
  public Point getPosition() {
    return new Point(this.row, this.col);
  }

  @Override
  public ChessPiece clone() {
    King king = new King(isWhite, row, col);
    king.hasMoved = this.hasMoved;
    return king;
  }

  public boolean isHasMoved() {
    return hasMoved;
  }
}
