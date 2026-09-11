package model.board;

import java.awt.Point;
import java.util.*;
import model.piece.*;

/** Rows 0..7 correspond to ranks 1..8; columns 0..7 to files a..h. */
public class ChessBoard implements Board {
  private ChessPiece[][] board = new ChessPiece[8][8];
  private boolean gameStart, whiteToMove;
  private Point enPassant;
  private int halfmoveClock;
  private String claimedDraw;
  private List<ChessPiece> captured = new ArrayList<>();
  private List<String> history = new ArrayList<>();
  private Map<String, Integer> repetitions = new HashMap<>();
  private final Deque<ChessBoard> undo = new ArrayDeque<>();

  private void requireStarted() {
    if (!gameStart) throw new IllegalStateException("Game has not started.");
  }
  private static boolean inBounds(int r, int c) { return r >= 0 && r < 8 && c >= 0 && c < 8; }
  @Override public ChessPiece get(int r, int c) {
    requireStarted();
    if (!inBounds(r, c)) throw new IllegalArgumentException("Square is outside the board.");
    return board[r][c];
  }
  /** Detached snapshot; use set for custom positions. */
  @Override public ChessPiece[][] getBoard() { return makeCopy().board; }
  /** Manual position edits invalidate history and transient rights. */
  @Override public void set(int r, int c, ChessPiece piece) {
    get(r, c);
    board[r][c] = piece;
    if (piece != null) piece.setPosition(r, c);
    enPassant = null;
    halfmoveClock = 0;
    claimedDraw = null;
    repetitions.clear(); history.clear(); captured.clear(); undo.clear();
  }
  @Override public void removePiece(int r, int c) { set(r, c, null); }
  public void startGame() {
    board = new ChessPiece[8][8];
    for (int c = 0; c < 8; c++) {
      board[1][c] = new Pawn(true, 1, c);
      board[6][c] = new Pawn(false, 6, c);
    }
    for (boolean w : new boolean[]{true, false}) {
      int r = w ? 0 : 7;
      board[r][0] = new Rook(w, r, 0); board[r][7] = new Rook(w, r, 7);
      board[r][1] = new Knight(w, r, 1); board[r][6] = new Knight(w, r, 6);
      board[r][2] = new Bishop(w, r, 2); board[r][5] = new Bishop(w, r, 5);
      board[r][3] = new Queen(w, r, 3); board[r][4] = new King(w, r, 4);
    }
    gameStart = true; whiteToMove = true; enPassant = null; halfmoveClock = 0; claimedDraw = null;
    captured.clear(); history.clear(); undo.clear(); repetitions.clear();
    repetitions.put(positionKey(), 1);
  }
  public boolean getTurn() { requireStarted(); return whiteToMove; }
  public Point getEnPassantTarget() { return enPassant == null ? null : new Point(enPassant); }
  public List<String> getHistory() { return Collections.unmodifiableList(history); }
  public boolean canUndo() { return !undo.isEmpty(); }
  /** Points captured by White, then by Black. */
  @Override public int[] getScore() {
    requireStarted();
    int[] score = new int[2];
    for (ChessPiece p : captured) score[p.isWhite() ? 1 : 0] += p.getPointValue();
    return score;
  }
  public List<Point> getLegalMoves(int r, int c) {
    ChessPiece piece = get(r, c);
    List<Point> legal = new ArrayList<>();
    if (piece == null) return legal;
    for (Point target : piece.getValidMoves(this)) {
      if (board[target.x][target.y] instanceof King) continue;
      ChessBoard copy = makeCopy();
      copy.applyMove(r, c, target.x, target.y, 'Q');
      if (!copy.isCheck(piece.isWhite())) legal.add(target);
    }
    return legal;
  }
  @Override public void movePiece(int sr, int sc, int er, int ec) { movePiece(sr, sc, er, ec, 'Q'); }
  public void movePiece(int sr, int sc, int er, int ec, char promotion) {
    ChessPiece piece = get(sr, sc); get(er, ec);
    promotion = Character.toUpperCase(promotion);
    if ("QRBN".indexOf(promotion) < 0) throw new IllegalArgumentException("Choose queen, rook, bishop or knight.");
    if (piece == null || piece.isWhite() != whiteToMove)
      throw new IllegalArgumentException("Select a piece belonging to the player whose turn it is.");
    if (isGameOver()) throw new IllegalStateException("The game is over. Start a new game or undo a move.");
    if (!getLegalMoves(sr, sc).contains(new Point(er, ec))) throw new IllegalArgumentException("That move is not legal.");
    if (repetitions.isEmpty()) repetitions.put(positionKey(), 1);
    undo.push(makeCopy());
    boolean capture = board[er][ec] != null || (piece instanceof Pawn && sc != ec);
    String move = square(sr, sc) + (capture ? " × " : " → ") + square(er, ec);
    if (piece instanceof King && Math.abs(ec - sc) == 2) move = ec == 6 ? "O-O" : "O-O-O";
    if (piece instanceof Pawn && (er == 0 || er == 7)) move += "=" + promotion;
    applyMove(sr, sc, er, ec, promotion);
    repetitions.merge(positionKey(), 1, Integer::sum);
    if (isCheckMate(whiteToMove)) move += "#";
    else if (isCheck(whiteToMove)) move += "+";
    history.add(move);
  }
  /** Candidate application never calls validation, avoiding recursive check detection. */
  private void applyMove(int sr, int sc, int er, int ec, char promotion) {
    ChessPiece piece = board[sr][sc], victim = board[er][ec];
    if (piece instanceof Pawn && sc != ec && victim == null) {
      victim = board[sr][ec]; board[sr][ec] = null;
    }
    if (victim != null) captured.add(victim);
    halfmoveClock = piece instanceof Pawn || victim != null ? 0 : halfmoveClock + 1;
    board[sr][sc] = null; board[er][ec] = piece; piece.setPosition(er, ec);
    if (piece instanceof King) {
      ((King) piece).setHasMoved(true);
      if (Math.abs(ec - sc) == 2) {
        int from = ec == 6 ? 7 : 0, to = ec == 6 ? 5 : 3;
        Rook rook = (Rook) board[sr][from];
        board[sr][from] = null; board[sr][to] = rook;
        rook.setPosition(sr, to); rook.setHasMoved(true);
      }
    }
    if (piece instanceof Rook) ((Rook) piece).setHasMoved(true);
    enPassant = null;
    if (piece instanceof Pawn) {
      ((Pawn) piece).setHasMoved(true);
      if (Math.abs(er - sr) == 2) enPassant = new Point((er + sr) / 2, ec);
      if (er == 0 || er == 7) {
        boolean w = piece.isWhite();
        switch (promotion) {
          case 'R':
            Rook rook = new Rook(w, er, ec); rook.setHasMoved(true); board[er][ec] = rook; break;
          case 'B': board[er][ec] = new Bishop(w, er, ec); break;
          case 'N': board[er][ec] = new Knight(w, er, ec); break;
          default: board[er][ec] = new Queen(w, er, ec);
        }
      }
    }
    whiteToMove = !whiteToMove;
  }
  public boolean isCheck(boolean white) {
    requireStarted();
    for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++)
      if (board[r][c] instanceof King && board[r][c].isWhite() == white)
        return isUnderAttack(white, new Point(r, c));
    throw new IllegalStateException("Position must contain both kings.");
  }
  /** Attacks use geometry, not legal moves: pawns attack diagonally and kings never castle to attack. */
  public boolean isUnderAttack(boolean white, Point target) {
    requireStarted();
    for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) {
      ChessPiece p = board[r][c];
      if (p == null || p.isWhite() == white) continue;
      int dr = target.x - r, dc = target.y - c;
      if (dr == 0 && dc == 0) continue;
      if (p instanceof Pawn) {
        if (dr == (p.isWhite() ? 1 : -1) && Math.abs(dc) == 1) return true;
      } else if (p instanceof Knight) {
        if (Math.abs(dr) * Math.abs(dc) == 2) return true;
      } else if (p instanceof King) {
        if (Math.max(Math.abs(dr), Math.abs(dc)) == 1) return true;
      } else {
        boolean diagonal = Math.abs(dr) == Math.abs(dc), straight = dr == 0 || dc == 0;
        if (!((p instanceof Bishop && diagonal) || (p instanceof Rook && straight)
            || (p instanceof Queen && (diagonal || straight)))) continue;
        int rr = r + Integer.signum(dr), cc = c + Integer.signum(dc);
        boolean clear = true;
        while (rr != target.x || cc != target.y) {
          if (board[rr][cc] != null) { clear = false; break; }
          rr += Integer.signum(dr); cc += Integer.signum(dc);
        }
        if (clear) return true;
      }
    }
    return false;
  }
  public boolean canCastle(boolean white, boolean kingSide) {
    int r = white ? 0 : 7;
    ChessPiece king = get(r, 4), rook = get(r, kingSide ? 7 : 0);
    if (!(king instanceof King) || king.isWhite() != white || ((King) king).isHasMoved()
        || !(rook instanceof Rook) || rook.isWhite() != white || ((Rook) rook).isHasMoved()) return false;
    for (int c = kingSide ? 5 : 1; c <= (kingSide ? 6 : 3); c++) if (board[r][c] != null) return false;
    if (isCheck(white)) return false;
    ChessBoard copy = makeCopy();
    copy.applyMove(r, 4, r, kingSide ? 5 : 3, 'Q');
    if (copy.isCheck(white)) return false;
    copy = makeCopy(); copy.applyMove(r, 4, r, kingSide ? 6 : 2, 'Q');
    return !copy.isCheck(white);
  }
  private boolean hasLegalMove(boolean white) {
    for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++)
      if (board[r][c] != null && board[r][c].isWhite() == white && !getLegalMoves(r, c).isEmpty()) return true;
    return false;
  }
  public boolean isCheckMate(boolean white) { return isCheck(white) && !hasLegalMove(white); }
  public boolean isStalemate(boolean white) { return !isCheck(white) && !hasLegalMove(white); }
  public boolean isInsufficientMaterial() {
    int minors = 0, bishopColor = -1;
    boolean onlyBishops = true;
    for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) {
      ChessPiece p = board[r][c];
      if (p == null || p instanceof King) continue;
      if (!(p instanceof Bishop) && !(p instanceof Knight)) return false;
      minors++;
      if (p instanceof Knight) onlyBishops = false;
      else if (bishopColor == -1) bishopColor = (r + c) % 2;
      else if (bishopColor != (r + c) % 2) onlyBishops = false;
    }
    return minors <= 1 || onlyBishops;
  }
  public boolean canClaimDraw() {
    return !isGameOver() && (halfmoveClock >= 100 || repetitions.getOrDefault(positionKey(), 0) >= 3);
  }
  public void claimDraw() {
    if (!canClaimDraw()) throw new IllegalStateException("No draw can be claimed in this position.");
    undo.push(makeCopy());
    claimedDraw = halfmoveClock >= 100 ? "Draw — fifty-move rule" : "Draw — threefold repetition";
  }
  public boolean isGameOver() {
    return claimedDraw != null || isCheckMate(whiteToMove) || isStalemate(whiteToMove)
        || isInsufficientMaterial() || halfmoveClock >= 150 || repetitions.getOrDefault(positionKey(), 0) >= 5;
  }
  public String getStatus() {
    if (claimedDraw != null) return claimedDraw;
    if (isCheckMate(whiteToMove)) return "Checkmate — " + (whiteToMove ? "Black" : "White") + " wins";
    if (isStalemate(whiteToMove)) return "Draw — stalemate";
    if (isInsufficientMaterial()) return "Draw — insufficient material";
    if (halfmoveClock >= 150) return "Draw — seventy-five-move rule";
    if (repetitions.getOrDefault(positionKey(), 0) >= 5) return "Draw — fivefold repetition";
    return (whiteToMove ? "White" : "Black") + " to move" + (isCheck(whiteToMove) ? " — check" : "");
  }
  private String positionKey() {
    StringBuilder key = new StringBuilder(80);
    for (ChessPiece[] rank : board) for (ChessPiece p : rank) key.append(symbol(p));
    key.append(whiteToMove ? 'w' : 'b');
    for (boolean white : new boolean[]{true, false}) {
      int r = white ? 0 : 7;
      ChessPiece k = board[r][4];
      for (int c : new int[]{0, 7}) {
        ChessPiece rook = board[r][c];
        key.append(k instanceof King && k.isWhite() == white && !((King) k).isHasMoved()
            && rook instanceof Rook && rook.isWhite() == white && !((Rook) rook).isHasMoved() ? '1' : '0');
      }
    }
    // Only a legal en passant capture distinguishes repetition positions.
    if (enPassant != null) {
      int r = enPassant.x + (whiteToMove ? -1 : 1);
      for (int c : new int[]{enPassant.y - 1, enPassant.y + 1})
        if (inBounds(r, c) && board[r][c] instanceof Pawn && board[r][c].isWhite() == whiteToMove
            && getLegalMoves(r, c).contains(enPassant)) { key.append(square(enPassant.x, enPassant.y)); break; }
    }
    return key.toString();
  }
  public void undoMove() {
    if (undo.isEmpty()) return;
    ChessBoard p = undo.pop();
    board = p.board; whiteToMove = p.whiteToMove; enPassant = p.enPassant;
    halfmoveClock = p.halfmoveClock; claimedDraw = p.claimedDraw;
    captured = p.captured; history = p.history; repetitions = p.repetitions;
  }
  @Override public ChessBoard makeCopy() {
    requireStarted();
    ChessBoard copy = new ChessBoard();
    copy.gameStart = true; copy.whiteToMove = whiteToMove; copy.enPassant = getEnPassantTarget();
    copy.halfmoveClock = halfmoveClock; copy.claimedDraw = claimedDraw;
    for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++)
      if (board[r][c] != null) copy.board[r][c] = board[r][c].clone();
    for (ChessPiece p : captured) copy.captured.add(p.clone());
    copy.history = new ArrayList<>(history); copy.repetitions = new HashMap<>(repetitions);
    return copy;
  }
  private static String square(int r, int c) { return "" + (char) ('a' + c) + (r + 1); }
  private static char symbol(ChessPiece p) {
    char ch = p == null ? '.' : p instanceof Knight ? 'N' : p.getClass().getSimpleName().charAt(0);
    return p != null && !p.isWhite() ? Character.toLowerCase(ch) : ch;
  }
  @Override public String toString() {
    StringBuilder text = new StringBuilder();
    for (int r = 7; r >= 0; r--) {
      for (int c = 0; c < 8; c++) text.append(symbol(board[r][c])).append(' ');
      text.append('\n');
    }
    return text.toString();
  }
}
