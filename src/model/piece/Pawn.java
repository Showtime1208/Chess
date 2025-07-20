package model.piece;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

import java.awt.*;

import model.board.Board;
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
    int currentRow = row;
    int currentCol = col;
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
    int[] colOffsets = {-1, 1}; // Left and right diagonals
    for (int offset : colOffsets) {
      int diagCol = currentCol + offset;
      if (0 <= diagCol && diagCol < 8 && 0 <= oneStepRow && oneStepRow < 8) {
        ChessPiece occupant = boardState.get(oneStepRow, diagCol);
        if (occupant != null && occupant.isWhite() != this.isWhite()) {
          validMoves.add(new Point(oneStepRow, diagCol));
        }
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

  @Override
  public ImageIcon getIcon() {
    String string = isWhite ? "white" : "black";
    return new ImageIcon("pieceImages" + File.separator
        + string + "Pawn.png");
  }

  public boolean isHasMoved() {
    return hasMoved;
  }

  public boolean isHasMovedOnce() {
    return hasMovedOnce;
  }
}
