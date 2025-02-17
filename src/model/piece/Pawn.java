package model.piece;

import java.util.ArrayList;
import java.util.List;
import model.board.Board;

import java.awt.*;
import model.board.ChessBoard;

public class Pawn implements ChessPiece {

  private final boolean isWhite;
  private int row;
  private int col;
  private boolean hasMoved;
  private boolean hasMovedOnce;

  public Pawn(boolean isWhite, int row, int col) {
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
  public List<Point> getValidMoves(ChessBoard boardState) {
    List<Point> validMoves = new ArrayList<>();
    int direction = isWhite ? 1 : -1;
    int oneStepRow = row + direction;
    //forward motion
    if (isInBounds(oneStepRow, col)) {
      //seeing if it can move one step
      if (boardState.get(oneStepRow, col) == null) {
        validMoves.add(new Point(oneStepRow, col));
      }
      //seeing if it can move two steps
      int twoStepRow = row + (2 * direction);
      if (isInBounds(twoStepRow, col) && !hasMoved && boardState.get(twoStepRow, col) == null) {
        validMoves.add(new Point(twoStepRow, col));
      }
    }
    int leftDiagCol = col - 1;
    int rightDiagCol = col + 1;
    // leftDiag take
    if (isInBounds(oneStepRow, leftDiagCol)) {
      ChessPiece occupant = boardState.get(oneStepRow, leftDiagCol);
      if (occupant != null && occupant.isWhite() != this.isWhite) {
        validMoves.add(new Point(oneStepRow, leftDiagCol));
      }
    }
    // rightDiag take
    if (isInBounds(oneStepRow, rightDiagCol)) {
      ChessPiece occupant = boardState.get(oneStepRow, rightDiagCol);
      if (occupant != null && occupant.isWhite() != this.isWhite) {
        validMoves.add(new Point(oneStepRow, rightDiagCol));
      }
    }
    //This might work for en passant not sure you prolly gotta test it
    if (isInBounds(row, leftDiagCol)) {
      ChessPiece occupant = boardState.get(row, leftDiagCol);
      if (occupant.getClass() == Pawn.class) {
        Pawn pawn = (Pawn) occupant;
        if (pawn.isWhite() != this.isWhite && pawn.isHasMovedOnce() == true) {
          validMoves.add(new Point(row, leftDiagCol));
        }
      }
    }
    //checking en passsant to the right
    if (isInBounds(row, rightDiagCol)) {
      ChessPiece occupant = boardState.get(row, rightDiagCol);
      if (occupant.getClass() == Pawn.class) {
        Pawn pawn = (Pawn) occupant;
        if (pawn.isWhite() != this.isWhite && pawn.isHasMovedOnce() == true) {
          validMoves.add(new Point(row, rightDiagCol));
        }
      }
    }
    // You still need to figure out en passant
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
    return 1;
  }

  @Override
  public Point getPosition() {
    return new Point(this.row, this.col);
  }

  @Override
  public ChessPiece clone() {
    Pawn pawn = new Pawn(isWhite, row, col);
    pawn.hasMoved = this.hasMoved;
    pawn.hasMovedOnce = this.hasMovedOnce;
    return pawn;
  }

  public boolean isHasMoved() {
    return hasMoved;
  }

  public boolean isHasMovedOnce() {
    return hasMovedOnce;
  }
}
