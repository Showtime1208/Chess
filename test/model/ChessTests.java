package model;

import controller.ChessController;
import controller.Controller;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.*;
import model.board.ChessBoard;
import model.piece.*;
import view.ChessView;

/** Dependency-free regression suite; failures exit nonzero without requiring -ea. */
public final class ChessTests {
  private static int tests;
  private static void check(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
  }
  private static void test(String name, Runnable body) {
    body.run(); tests++; System.out.println("PASS " + name);
  }
  static ChessBoard fresh() { ChessBoard b = new ChessBoard(); b.startGame(); return b; }
  static ChessBoard position(String fen) {
    ChessBoard b = fresh();
    for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) b.set(r, c, null);
    String[] fields = fen.split(" "); String[] ranks = fields[0].split("/");
    for (int i = 0; i < 8; i++) {
      int c = 0, r = 7 - i;
      for (char ch : ranks[i].toCharArray()) {
        if (Character.isDigit(ch)) { c += ch - '0'; continue; }
        boolean w = Character.isUpperCase(ch); ChessPiece p;
        switch (Character.toUpperCase(ch)) {
          case 'K': p = new King(w, r, c); ((King) p).setHasMoved(true); break;
          case 'Q': p = new Queen(w, r, c); break;
          case 'R': p = new Rook(w, r, c); ((Rook) p).setHasMoved(true); break;
          case 'B': p = new Bishop(w, r, c); break;
          case 'N': p = new Knight(w, r, c); break;
          default: p = new Pawn(w, r, c); ((Pawn) p).setHasMoved(r != (w ? 1 : 6));
        }
        b.set(r, c++, p);
      }
    }
    field(b, "whiteToMove", fields[1].equals("w"));
    for (char right : fields[2].toCharArray()) {
      if (right == '-') continue;
      int r = Character.isUpperCase(right) ? 0 : 7;
      ((King) b.get(r, 4)).setHasMoved(false);
      ((Rook) b.get(r, Character.toUpperCase(right) == 'K' ? 7 : 0)).setHasMoved(false);
    }
    if (!fields[3].equals("-")) field(b, "enPassant", new Point(fields[3].charAt(1) - '1', fields[3].charAt(0) - 'a'));
    if (fields.length > 4) field(b, "halfmoveClock", Integer.parseInt(fields[4]));
    return b;
  }
  private static void field(ChessBoard b, String name, Object value) {
    try { Field f = ChessBoard.class.getDeclaredField(name); f.setAccessible(true); f.set(b, value); }
    catch (ReflectiveOperationException e) { throw new AssertionError(e); }
  }
  static void move(ChessBoard b, String uci) {
    b.movePiece(uci.charAt(1) - '1', uci.charAt(0) - 'a', uci.charAt(3) - '1', uci.charAt(2) - 'a',
        uci.length() == 5 ? uci.charAt(4) : 'Q');
  }
  private static boolean legal(ChessBoard b, String uci) {
    return b.getLegalMoves(uci.charAt(1) - '1', uci.charAt(0) - 'a')
        .contains(new Point(uci.charAt(3) - '1', uci.charAt(2) - 'a'));
  }
  private static void rejects(Runnable action) {
    try { action.run(); } catch (IllegalArgumentException | IllegalStateException expected) { return; }
    throw new AssertionError("Expected move to be rejected");
  }
  static long perft(ChessBoard b, int depth) {
    if (depth == 0) return 1;
    long count = 0;
    for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) {
      ChessPiece p = b.get(r, c);
      if (p == null || p.isWhite() != b.getTurn()) continue;
      for (Point target : b.getLegalMoves(r, c)) {
        String promotions = p instanceof Pawn && (target.x == 0 || target.x == 7) ? "QRBN" : "Q";
        for (char promotion : promotions.toCharArray()) {
          if (depth == 1) count++;
          else {
            ChessBoard child = b.makeCopy(); child.movePiece(r, c, target.x, target.y, promotion);
            count += perft(child, depth - 1);
          }
        }
      }
    }
    return count;
  }
  public static void main(String[] args) {
    test("standard starting squares, turn and images", () -> {
      ChessBoard b = fresh();
      check(b.get(0, 4) instanceof King && b.get(7, 3) instanceof Queen && b.getTurn(), "Initial setup");
      int pieces = 0;
      for (ChessPiece[] rank : b.getBoard()) for (ChessPiece p : rank) if (p != null) {
        pieces++; check(p.getIcon().getIconWidth() > 0, "Image missing");
      }
      check(pieces == 32 && !b.isCheck(true) && !b.isCheck(false), "Initial state");
    });
    test("initial perft depths 1–4", () -> {
      long[] expected = {1, 20, 400, 8902, 197281};
      for (int d = 1; d <= 4; d++) check(perft(fresh(), d) == expected[d], "Perft depth " + d);
    });
    test("Kiwipete castling and tactical perft", () -> {
      ChessBoard b = position("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
      check(perft(b, 1) == 48 && perft(b, 2) == 2039 && perft(b, 3) == 97862, "Kiwipete counts");
    });
    test("en passant endgame perft", () -> {
      ChessBoard b = position("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1");
      check(perft(b, 1) == 14 && perft(b, 2) == 191 && perft(b, 3) == 2812, "Endgame counts");
    });
    test("ordinary moves do not score; captures and undo do", () -> {
      ChessBoard b = fresh(); move(b, "e2e4"); move(b, "d7d5");
      check(Arrays.equals(b.getScore(), new int[]{0, 0}), "False capture");
      move(b, "e4d5"); check(b.getScore()[0] == 1 && b.get(3, 4) == null, "Capture");
      b.undoMove(); check(b.get(3, 4) instanceof Pawn && b.get(4, 3) instanceof Pawn && b.getScore()[0] == 0, "Undo capture");
    });
    test("turn enforcement, bounds and rejected moves are atomic", () -> {
      ChessBoard b = fresh(); String initial = b.toString();
      rejects(() -> move(b, "e7e5")); rejects(() -> move(b, "e2e5")); rejects(() -> b.movePiece(-1, 0, 0, 0));
      check(initial.equals(b.toString()) && b.getTurn() && !b.canUndo(), "Invalid move changed game");
    });
    test("pawns cannot jump blockers or double-step off their starting rank", () -> {
      ChessBoard b = fresh(); b.set(2, 4, new Knight(false, 2, 4)); check(!legal(b, "e2e4"), "Jumped blocker");
      b.set(3, 0, new Pawn(true, 3, 0)); check(!legal(b, "a4a6"), "Off-rank double move");
    });
    test("pinned pieces cannot expose king", () -> {
      ChessBoard b = position("k3r3/8/8/8/8/8/4R3/4K3 w - - 0 1");
      check(!legal(b, "e2d2") && legal(b, "e2e8"), "Pin filtering"); rejects(() -> move(b, "e2d2"));
    });
    test("check escape does not recurse or corrupt source board", () -> {
      ChessBoard b = position("k3r3/8/8/8/8/8/8/4K3 w - - 0 1");
      check(b.isCheck(true), "Missing check"); move(b, "e1d1"); check(!b.isCheck(true), "Check escape failed");
    });
    test("pawn attack geometry and adjacent kings", () -> {
      ChessBoard b = position("7k/8/8/8/8/8/3p4/4K3 w - - 0 1");
      check(b.isUnderAttack(true, new Point(0, 2)), "Empty pawn diagonal");
      check(!b.isUnderAttack(true, new Point(0, 3)), "Pawn forward is not an attack");
      b = position("8/8/8/8/8/4k3/8/4K3 w - - 0 1"); check(!legal(b, "e1e2"), "Adjacent kings");
    });
    test("castling moves both pieces and undo restores rights", () -> {
      for (String uci : new String[]{"e1g1", "e1c1", "e8g8", "e8c8"}) {
        boolean w = uci.charAt(1) == '1'; int r = w ? 0 : 7;
        ChessBoard b = position("r3k2r/8/8/8/8/8/8/R3K2R " + (w ? "w" : "b") + " KQkq - 0 1");
        move(b, uci);
        int rookCol = uci.charAt(2) == 'g' ? 5 : 3;
        check(b.get(r, rookCol) instanceof Rook && b.get(r, 4) == null, "Rook not relocated");
        b.undoMove(); check(legal(b, uci), "Castling rights not restored");
      }
    });
    test("castling cannot cross check, but queenside b-file may be attacked", () -> {
      ChessBoard b = position("k4r2/8/8/8/8/8/8/4K2R w K - 0 1"); check(!legal(b, "e1g1"), "Castled across check");
      b = position("1r5k/8/8/8/8/8/8/R3K3 w Q - 0 1"); check(legal(b, "e1c1"), "b1 attack should not block castling");
      b = position("k7/8/8/8/8/8/6p1/4K2R w K - 0 1"); check(!legal(b, "e1g1"), "Pawn attacks transit");
    });
    test("rook return does not restore castling rights", () -> {
      ChessBoard b = position("4k3/8/8/8/8/8/8/4K2R w K - 0 1");
      move(b, "h1h2"); move(b, "e8e7"); move(b, "h2h1"); move(b, "e7e8"); check(!legal(b, "e1g1"), "Lost rights returned");
    });
    test("en passant capture, expiry and undo", () -> {
      ChessBoard b = fresh();
      for (String m : new String[]{"e2e4", "a7a6", "e4e5", "d7d5"}) move(b, m);
      check(legal(b, "e5d6"), "Missing en passant"); move(b, "e5d6");
      check(b.get(4, 3) == null && b.getScore()[0] == 1, "En passant capture");
      b.undoMove(); check(legal(b, "e5d6"), "Undo en passant");
      move(b, "g1f3"); move(b, "a6a5"); check(!legal(b, "e5d6"), "Expired en passant remains");
    });
    test("en passant cannot expose a rook attack", () -> {
      ChessBoard b = position("7k/8/8/r4pPK/8/8/8/8 w - f6 0 1"); check(!legal(b, "g5f6"), "En passant exposed king");
    });
    test("all four promotions for both colors, capture promotion and undo", () -> {
      for (boolean w : new boolean[]{true, false}) for (char p : "QRBN".toCharArray()) {
        ChessBoard b = position(w ? "7k/P7/8/8/8/8/8/7K w - - 0 1" : "7k/8/8/8/8/8/p7/7K b - - 0 1");
        move(b, (w ? "a7a8" : "a2a1") + p);
        ChessPiece promoted = b.get(w ? 7 : 0, 0);
        check(!(promoted instanceof Pawn) && promoted.isWhite() == w, "Promotion failed");
        check(p == 'N' ? promoted instanceof Knight : promoted.getClass().getSimpleName().charAt(0) == p, "Wrong promotion");
        b.undoMove(); check(b.get(w ? 6 : 1, 0) instanceof Pawn, "Undo promotion");
      }
      ChessBoard b = position("1r5k/P7/8/8/8/8/8/7K w - - 0 1"); move(b, "a7b8Q"); check(b.getScore()[0] == 5, "Capture promotion");
    });
    test("Fool's mate ends the game and undo reopens it", () -> {
      ChessBoard b = fresh(); for (String m : new String[]{"f2f3", "e7e5", "g2g4", "d8h4"}) move(b, m);
      check(b.isCheckMate(true) && b.getStatus().contains("Black wins"), "Mate not found");
      rejects(() -> move(b, "a2a3")); b.undoMove(); check(!b.isGameOver(), "Undo mate");
    });
    test("stalemate and material draws", () -> {
      ChessBoard b = position("7k/5Q2/6K1/8/8/8/8/8 b - - 0 1"); check(b.isStalemate(false) && b.isGameOver(), "Stalemate");
      for (String fen : new String[]{"7k/8/8/8/8/8/8/K7", "7k/8/8/8/8/8/8/KB6", "7k/8/8/8/8/8/8/KN6", "7k/8/8/8/4b3/8/8/KB6"})
        check(position(fen + " w - - 0 1").isInsufficientMaterial(), "Material draw " + fen);
      check(!position("7k/8/8/8/8/8/8/KNN5 w - - 0 1").isInsufficientMaterial(), "Two knights not automatically dead");
    });
    test("threefold claim, undo claim, and automatic fivefold draw", () -> {
      ChessBoard b = fresh();
      for (int cycle = 0; cycle < 2; cycle++) for (String m : new String[]{"g1f3", "g8f6", "f3g1", "f6g8"}) move(b, m);
      check(b.canClaimDraw() && !b.isGameOver(), "Threefold claim"); b.claimDraw(); check(b.isGameOver(), "Claim did not end game");
      b.undoMove(); check(b.canClaimDraw() && !b.isGameOver(), "Undo claim");
      for (int cycle = 0; cycle < 2; cycle++) for (String m : new String[]{"g1f3", "g8f6", "f3g1", "f6g8"}) move(b, m);
      check(b.isGameOver() && b.getStatus().contains("fivefold"), "Fivefold draw");
    });
    test("fifty-move claim, 75-move draw and pawn reset", () -> {
      ChessBoard b = position("7k/8/8/8/8/8/P7/R3K3 w - - 99 1"); move(b, "a1b1"); check(b.canClaimDraw(), "50-move claim");
      b = position("7k/8/8/8/8/8/P7/R3K3 w - - 149 1"); move(b, "a1b1"); check(b.getStatus().contains("seventy-five"), "75-move draw");
      b = position("7k/8/8/8/8/8/P7/R3K3 w - - 99 1"); move(b, "a2a3"); check(!b.canClaimDraw(), "Pawn clock reset");
    });
    test("deep copy and restart clear all game state", () -> {
      ChessBoard b = fresh(); move(b, "e2e4"); ChessBoard copy = b.makeCopy(); move(copy, "d7d5");
      check(b.get(6, 3) != null && copy.get(6, 3) == null, "Aliased board");
      b.startGame(); check(b.getHistory().isEmpty() && !b.canUndo() && b.getTurn() && b.getEnPassantTarget() == null, "Restart state");
    });
    test("controller selection, legal highlights, move, undo and promotion cancellation", () -> {
      ChessBoard b = fresh(); FakeView v = new FakeView(); ChessController c = new ChessController(b, v);
      c.handleSquareClick(1, 4); check(v.moves.size() == 2, "Pawn highlights");
      c.handleSquareClick(0, 6); check(v.row == 0 && v.col == 6, "Reselect own piece");
      c.handleSquareClick(2, 5); check(b.get(2, 5) instanceof Knight && !b.getTurn(), "Controller move");
      c.undoMove(); check(b.getTurn() && v.row == -1, "Controller undo");
      b = position("7k/P7/8/8/8/8/8/7K w - - 0 1"); c = new ChessController(b, v); v.promotion = 0;
      c.handleSquareClick(6, 0); c.handleSquareClick(7, 0); check(b.get(6, 0) instanceof Pawn && b.getTurn(), "Cancelled promotion moved pawn");
      v.promotion = 'N'; c.handleSquareClick(7, 0); check(b.get(7, 0) instanceof Knight, "Controller promotion");
    });
    test("unstarted board, empty squares and invalid promotion are rejected", () -> {
      ChessBoard cold = new ChessBoard();
      rejects(() -> cold.get(0, 0)); rejects(cold::makeCopy); rejects(cold::getTurn); rejects(cold::getScore);
      ChessBoard b = fresh(); String initial = b.toString();
      check(b.getLegalMoves(3, 3).isEmpty(), "Empty square moves");
      rejects(() -> move(b, "a3a4")); rejects(() -> move(b, "e2e4K"));
      rejects(b::claimDraw); b.undoMove();
      check(initial.equals(b.toString()), "Rejected operation mutated board");
      b.movePiece(1, 4, 3, 4); check(b.get(3, 4) instanceof Pawn, "Default move overload");
      rejects(() -> b.get(0, 8));
      b.removePiece(0, 4); rejects(() -> b.isCheck(true));
    });
    test("piece metadata, detached positions, clones and move flags", () -> {
      ChessBoard b = fresh();
      for (ChessPiece[] rank : b.getBoard()) for (ChessPiece p : rank) if (p != null) {
        Point original = p.getPosition(), exposed = p.getPosition(); exposed.x = 99;
        check(p.getPosition().equals(original), "Mutable position escaped");
        ChessPiece clone = p.clone(); clone.setPosition(3, 3);
        check(p.getPosition().equals(original) && clone.getPosition().equals(new Point(3, 3)), "Clone isolation");
        int value = p instanceof Pawn ? 1 : p instanceof Rook ? 5 : p instanceof Queen ? 9 : p instanceof King ? 999 : 3;
        check(p.getPointValue() == value && p.isWhite() == clone.isWhite(), "Piece metadata");
      }
      Pawn pawn = new Pawn(true, 1, 1); pawn.setHasMoved(true);
      check(((Pawn) pawn.clone()).isHasMoved(), "Pawn move flag clone");
      Rook rook = new Rook(true, 0, 0); rook.setHasMoved(true);
      check(((Rook) rook.clone()).isHasMoved(), "Rook move flag clone");
      King king = new King(true, 0, 4); king.setHasMoved(true);
      check(((King) king.clone()).isHasMoved(), "King move flag clone");
      rejects(() -> new Bishop(true, 0, 2).setPosition(9, 2));
      ChessPiece[][] snapshot = b.getBoard(); snapshot[0][0].setPosition(5, 5); snapshot[0][1] = null;
      check(b.get(0, 0).getPosition().equals(new Point(0, 0)) && b.get(0, 1) != null, "Board snapshot isolation");
    });
    test("kings cannot be captured and castling requires same-color original pieces", () -> {
      ChessBoard b = position("4k3/8/8/8/8/8/4Q3/K7 w - - 0 1");
      check(!legal(b, "e2e8"), "King capture generated");
      b = position("7k/8/8/8/8/8/8/R3K2R w KQ - 0 1");
      b.set(0, 7, new Rook(false, 0, 7)); check(!b.canCastle(true, true), "Enemy rook accepted");
      b.set(0, 4, new King(false, 0, 4)); check(!b.canCastle(true, false), "Enemy king accepted");
    });
    test("black en passant and promotion from both board edges", () -> {
      ChessBoard b = position("7k/8/8/8/3pP3/8/8/K7 b - e3 0 1"); move(b, "d4e3");
      check(b.get(3, 4) == null && b.getScore()[1] == 1, "Black en passant");
      b = position("k7/7P/8/8/8/8/8/K7 w - - 0 1"); move(b, "h7h8r");
      check(b.get(7, 7) instanceof Rook && ((Rook) b.get(7, 7)).isHasMoved(), "Promoted rook rights");
    });
    test("all terminal status messages and fifty-move claim", () -> {
      ChessBoard b = position("7k/5Q2/6K1/8/8/8/8/8 b - - 0 1"); check(b.getStatus().contains("stalemate"), "Stalemate message");
      b = position("7k/8/8/8/8/8/8/K7 w - - 0 1"); check(b.getStatus().contains("material"), "Material message");
      b = position("7k/8/8/8/8/8/8/KR6 w - - 100 1"); b.claimDraw(); check(b.getStatus().contains("fifty"), "Claim message");
      b = position("7k/8/8/8/8/8/8/KR6 b - - 0 1"); check(b.getStatus().equals("Black to move"), "Black turn message");
      b = position("7k/6Q1/6K1/8/8/8/8/8 b - - 150 1"); check(b.getStatus().contains("White wins"), "Mate precedence over move clock");
      b = position("k3r3/8/8/8/8/8/8/4K3 w - - 0 1"); check(b.getStatus().contains("check"), "Check status");
    });
    test("controller guards, deselection, invalid clicks and restart", () -> {
      FakeView v = new FakeView(); ChessBoard b = fresh();
      rejects(() -> new ChessController(null, v)); rejects(() -> new ChessController(b, null));
      ChessController c = new ChessController(b, v); c.playGame();
      for (int[] click : new int[][]{{-1,0},{8,0},{0,-1},{0,8},{3,3},{6,4}}) c.handleSquareClick(click[0], click[1]);
      check(b.getHistory().isEmpty() && v.row == -1, "Ignored clicks changed selection");
      c.handleSquareClick(1, 4); c.handleSquareClick(1, 4); check(v.row == -1, "Deselect");
      c.handleSquareClick(1, 4); c.handleSquareClick(4, 4); check(v.row == 1 && b.getTurn(), "Invalid move preserved selection");
      c.handleSquareClick(3, 4); c.playGame(); check(b.getHistory().isEmpty() && v.row == -1, "Restart selected state");
      for (int cycle = 0; cycle < 2; cycle++) for (String m : new String[]{"g1f3", "g8f6", "f3g1", "f6g8"}) move(b, m);
      c.claimDraw(); check(b.isGameOver(), "Controller draw");
      c.handleSquareClick(1, 0); check(v.row == -1, "Terminal selection");
    });
    System.out.println("All " + tests + " regression groups passed.");
  }
  static final class FakeView implements ChessView {
    int row, col; char promotion = 'Q'; List<Point> moves;
    public void setController(Controller c) { }
    public void update() { }
    public void highlightMoves(List<Point> m) { moves = m; }
    public void setSelection(int r, int c) { row = r; col = c; }
    public char choosePromotion(boolean white) { return promotion; }
  }
}
