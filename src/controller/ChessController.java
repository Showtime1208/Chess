package controller;

import java.awt.Point;
import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.board.ChessBoard;
import model.piece.ChessPiece;
import view.ChessBoardFrame;
import view.ChessView;

public class ChessController implements Controller {
  private ChessBoard board;
  private ChessView view;
  private boolean pieceSelected;
  private int selectedRow;
  private int selectedCol;
  
  public ChessController(ChessBoard board, ChessView view) {
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
    view.update();
  }

  public void handleSquareClick(int row, int col) {
    System.out.println("Clicked on square (" + row + "," + col + "), pieceSelected=" + pieceSelected);
    
    if (!pieceSelected) {
      ChessPiece piece = null;
      try {
        piece = board.get(row, col);
      } catch (IllegalArgumentException | IllegalStateException e) {
        throw new IllegalArgumentException("Invalid position");
      }
      if (piece == null) {
        return; // Clicked on empty square, do nothing
      }
      if (piece.isWhite() != board.getTurn()) {
        return; // Not your piece, do nothing
      }
      pieceSelected = true;
      selectedRow = row;
      selectedCol = col;
      List<Point> moves = piece.getValidMoves(board);
      System.out.println("Selected piece at (" + row + "," + col + ") with " + moves.size() + " valid moves");
      for (Point move : moves) {
        System.out.println("  Valid move: (" + move.x + "," + move.y + ")");
      }
      view.setSelection(row, col);
      view.highlightMoves(moves);
    }
    else {
      // Check if clicking on the same piece (deselect)
      if (row == selectedRow && col == selectedCol) {
        pieceSelected = false;
        selectedRow = -1;
        selectedCol = -1;
        view.setSelection(-1, -1);
        view.highlightMoves(new ArrayList<>()); // Clear highlights
        view.update();
        return;
      }
      
      // Check if clicking on empty square (deselect)
      ChessPiece piece = null;
      try {
        piece = board.get(row, col);
      } catch (IllegalArgumentException | IllegalStateException e) {
        // Invalid position, deselect
        pieceSelected = false;
        selectedRow = -1;
        selectedCol = -1;
        view.setSelection(-1, -1);
        view.highlightMoves(new ArrayList<>());
        view.update();
        return;
      }
      
      if (piece == null) {
        // Clicked on empty square - this could be a valid move, try it
        try {
          System.out.println("Attempting move from (" + selectedRow + "," + selectedCol + ") to (" + row + "," + col + ")");
          board.movePiece(selectedRow, selectedCol, row, col);
          System.out.println("Move successful!");
          pieceSelected = false;
          selectedRow = -1;
          selectedCol = -1;
          view.setSelection(-1, -1);
          view.highlightMoves(new ArrayList<>()); // Clear highlights
          view.update();
          return;
        } catch (IllegalArgumentException | IllegalStateException e) {
          System.out.println("Move failed: " + e.getMessage());
          // Move was invalid, deselect
          pieceSelected = false;
          selectedRow = -1;
          selectedCol = -1;
          view.setSelection(-1, -1);
          view.highlightMoves(new ArrayList<>());
          view.update();
          return;
        }
      }
      
      // Try to make a move
      try {
        System.out.println("Attempting move from (" + selectedRow + "," + selectedCol + ") to (" + row + "," + col + ")");
        board.movePiece(selectedRow, selectedCol, row, col);
        System.out.println("Move successful!");
        pieceSelected = false;
        selectedRow = -1;
        selectedCol = -1;
        view.setSelection(-1, -1);
        view.highlightMoves(new ArrayList<>()); // Clear highlights
        view.update();
      } catch (IllegalArgumentException | IllegalStateException e) {
        System.out.println("Move failed: " + e.getMessage());
        // Move was invalid, deselect
        pieceSelected = false;
        selectedRow = -1;
        selectedCol = -1;
        view.setSelection(-1, -1);
        view.highlightMoves(new ArrayList<>());
        view.update();
      }
    }
  }
}
