package model;

import java.awt.Point;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import model.board.ChessBoard;
import model.piece.*;

/** Compares complete legal move sets and state against an independent chess implementation. */
public final class OracleTests {
  private static int states, moves;
  private static void check(boolean value, String error) { if (!value) throw new AssertionError(error); }
  private static void verify(ChessBoard board, String fen, String expectedMoves, String flags) {
    Set<String> actual = new TreeSet<>();
    for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) {
      ChessPiece p = board.get(r, c);
      if (p == null || p.isWhite() != board.getTurn()) continue;
      for (Point t : board.getLegalMoves(r, c)) {
        String move = "" + (char) ('a' + c) + (r + 1) + (char) ('a' + t.y) + (t.x + 1);
        if (p instanceof Pawn && (t.x == 0 || t.x == 7)) for (char suffix : "qrbn".toCharArray()) actual.add(move + suffix);
        else actual.add(move);
      }
    }
    Set<String> expected = new TreeSet<>();
    if (!expectedMoves.isEmpty()) expected.addAll(Arrays.asList(expectedMoves.split(",")));
    check(actual.equals(expected), "Legal moves differ at " + fen + "\nExpected " + expected + "\nActual " + actual);
    String actualFlags = (board.isCheck(board.getTurn()) ? 1 : 0) + "," + (board.isCheckMate(board.getTurn()) ? 1 : 0)
        + "," + (board.isStalemate(board.getTurn()) ? 1 : 0) + "," + (board.isInsufficientMaterial() ? 1 : 0);
    check(actualFlags.equals(flags), "Status differs at " + fen + ": " + actualFlags + " vs " + flags);
    ChessBoard parsed = ChessTests.position(fen);
    check(board.toString().equals(parsed.toString()) && board.getTurn() == parsed.getTurn(), "Transition differs at " + fen);
    check(Objects.equals(board.getEnPassantTarget(), parsed.getEnPassantTarget()), "En passant target differs at " + fen);
    states++; moves += actual.size();
  }
  public static void main(String[] args) throws IOException {
    for (String line : Files.readAllLines(Paths.get("test/fixtures/positions.tsv"), StandardCharsets.UTF_8)) {
      String[] f = line.split("\t", -1); verify(ChessTests.position(f[0]), f[0], f[1], f[2]);
    }
    System.out.println("PASS " + states + " independent static positions");
    ChessBoard board = null;
    for (String line : Files.readAllLines(Paths.get("test/fixtures/games.tsv"), StandardCharsets.UTF_8)) {
      String[] f = line.split("\t", -1);
      if (f[1].equals("-")) board = ChessTests.fresh();
      else {
        String before = board.toString(); boolean turn = board.getTurn();
        ChessTests.move(board, f[1]);
        verify(board, f[2], f[3], f[4]);
        // Round-trip every transition, including captures, castling and promotion.
        board.undoMove();
        check(before.equals(board.toString()) && turn == board.getTurn(), "Undo differs in game " + f[0]);
        ChessTests.move(board, f[1]);
      }
    }
    System.out.println("PASS independent oracle: " + states + " positions, " + moves + " legal moves, 50 replayed games with undo");
  }
}
