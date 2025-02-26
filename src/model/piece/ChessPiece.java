package model.piece;

import java.util.List;
import javax.swing.ImageIcon;
import model.board.Board;

import java.awt.*;
import model.board.ChessBoard;

/**
 * Piece interface.
 */
public interface ChessPiece {

  ImageIcon getIcon();

  String toString();

  List<Point> getValidMoves(ChessBoard boardState);

  boolean isWhite();

  int getPointValue();

  void setPosition(int row, int col);

  Point getPosition();

  ChessPiece clone();
}
