package controller;

import java.io.IOError;
import java.io.IOException;
import java.util.Scanner;
import model.board.ChessBoard;
import view.ChessView;

public class ChessController {
  private ChessBoard board;
  private ChessView view;

  private Scanner scan;
  private Appendable out;
  private boolean gameQuit;

  public ChessController(Readable rd, Appendable ap) throws IllegalArgumentException {
    if (rd == null || ap == null) {
      throw new IllegalArgumentException("rd/ap cannot be null");
    }
    this.scan = new Scanner(rd);
    this.out = ap;
    this.gameQuit = false;
  }

  public void playGame(ChessBoard board, ChessView view) {
    if (board == null || view == null) {
      throw new IllegalArgumentException("Model/view cannot be null");
    }
    this.board = board;
    this.view = view;
    board.startGame();
    try {
      while (!board.isCheckMate(board.getTurn()) && gameQuit) {
        printBoard();
        printWhiteScore();
        printBlackScore();
        printTurn();
        handleUserInput();
      }
      if (gameQuit) {
        printGameQuitMessage();
      } else {
        printGameOverMessage();
      }
    } catch (IOException ex) {
      throw new IllegalStateException("IOException shawty.");
    }
  }

  private void printBoard() throws IOException {
    transmit(view.displayBoard());
  }

  private void printGameOverMessage() throws IOException {

  }

  private void printTurn() throws IOException {
    if (board.getTurn()) {
      transmit("White");
    } else {
      transmit("Black");
    }
  }
  private void handleUserInput() throws IOException {
    if (!scan.hasNext()) {
      throw new IllegalStateException("No more input when expecting a move.");
    }
    String input = scan.next();
    if (input.equalsIgnoreCase("move")) {
      int index1 = getNextInt() - 1;
      int index2 = getNextInt() - 1;
      int index3 = getNextInt() - 1;
      int index4 = getNextInt() - 1;
      try {
        board.movePiece(index1, index2, index3, index4);
      } catch (IllegalArgumentException | IllegalStateException ex) {
        throw new IllegalStateException("Could not move." + ex.getLocalizedMessage());
      }
    }
  }
  private void printWhiteGraveyard() throws IOException {
    transmit(view.displayWhiteGraveyard());
  }
  private void printBlackGraveyard() throws IOException {
    transmit(view.displayBlackGraveyard());
  }
  private void printGameQuitMessage() throws IOException {
    printBoard();
    transmit("Game Quit:");
    printWhiteGraveyard();
    printWhiteScore();
    printBlackGraveyard();
    printBlackScore();
  }
  private void transmit(String message) throws IOException {
    out.append(message).append("\n");
  }

  private void printWhiteScore() throws IOException {
    transmit(board.getScore()[0] + "");
  }
  private void printBlackScore() throws IOException {
    transmit(board.getScore()[1] + "");
  }

  private int getNextInt() {
    while (scan.hasNext()) {
      String token = scan.next();
      if (token.equalsIgnoreCase("q")) {
        this.gameQuit = true;
        return -999;
      }
      try {
        int val = Integer.parseInt(token);
        if (val < 0) {
          throw new NumberFormatException();
        }
        return val;
      } catch (NumberFormatException ex) {
        return getNextInt();
      }
    }
    throw new IllegalStateException("No more input when expecting an integer.");
  }


 }
