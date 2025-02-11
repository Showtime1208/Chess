package model.piece;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
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
    return Stream.concat(getValidBishopMoves(boardState).stream(), getValidRookMoves(boardState).stream()).toList();
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

  private List<Point> getValidBishopMoves(Board boardState) {
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

  private List<Point> getValidRookMoves(Board boardState) {
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
}
