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
  }

  private void printBoard() throws IOException {
    transmit(view.displayBoard());
  }

  private void printTurn() throws IOException {
    if (board.getTurn()) {
      transmit("White");
    } else {
      transmit("Black");
    }
  }
  private void handleUserInput() throws IOException {

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


 }
