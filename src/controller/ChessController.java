package controller;

import java.awt.Point;
import java.io.IOError;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import model.board.ChessBoard;
import model.piece.ChessPiece;
import view.ChessBoardFrame;
import view.ChessView;

public class ChessController {
  private ChessBoard board;
  private ChessBoardFrame view;
  private boolean pieceSelected;
  private int selectedRow;
  private int selectedCol;
  public ChessController(ChessBoard board, ChessBoardFrame view) {
    if  (board == null ||  view == null) {
      throw new IllegalArgumentException("Board  or View is null");
    }
    this.board = board;
    this.view = view;
    this.selectedRow = -1;
    this.selectedCol = -1;
  }

  public void playGame() {
    board.startGame();
    view.updateBoard();

  }

  public void handleSquareClick(int row, int col) {
    if (!pieceSelected) {
      ChessPiece piece = null;
      try {
        piece = board.get(row, col);
      } catch (IllegalArgumentException | IllegalStateException e) {
        throw new IllegalArgumentException("How");
      }
      if (piece == null) {
        return;
      }
      if (piece.isWhite() != board.getTurn()) {
        return;
      }
      pieceSelected = true;
      selectedRow = row;
      selectedCol = col;
      List<Point> moves = piece.getValidMoves(board);
      view.highlightMoves(moves);
    }
    else {
      try {
        board.movePiece(selectedRow, selectedCol, row, col);
      } catch (IllegalArgumentException | IllegalStateException e) {
        return;
      }
      pieceSelected = false;
      selectedRow = -1;
      selectedCol = -1;
      view.updateBoard();
    }
  }


 }
