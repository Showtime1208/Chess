package model.board;


import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import model.piece.Bishop;
import model.piece.ChessPiece;
import model.piece.King;
import model.piece.Knight;
import model.piece.Pawn;


import model.piece.Queen;
import model.piece.Rook;

public class ChessBoard implements Board {

  /*
   * Thoughts for chessboard. Change the initializeBoard to be just like the startGame in the pokerPolygons, where I have a gameStart condition I gotta do before I start everyhting.
   *  Would make the controller stuff a lot easier.
   */

  private ChessPiece[][] board;
  private boolean gameStart;
  private boolean whiteToMove;
  private List<ChessPiece> whitePieces;
  private List<ChessPiece> blackPieces;
  private List<ChessPiece> blackGraveyard;
  private List<ChessPiece> whiteGraveyard;

  @Override
  public ChessPiece[][] getBoard() {
    return board;
  }

  public ChessBoard() {
    this.board = new ChessPiece[8][8];
    this.gameStart = false;
    this.whitePieces = new ArrayList<>();
    this.blackPieces = new ArrayList<>();
    this.blackGraveyard = new ArrayList<>();
    this.whiteGraveyard = new ArrayList<>();
  }

  @Override
  public ChessPiece get(int row, int col) {
    if (!gameStart) {
      throw new IllegalStateException("Game has not started.");
    }
    if (isInBounds(row, col)) {
      return board[row][col];
    }
    else throw new IllegalArgumentException("Out of bounds input.");
  }

  @Override
  public void set(int row, int col, ChessPiece piece) {
    if (!gameStart) {
      throw new IllegalStateException("Game has not started yet.");
    }
    if (isInBounds(row, col) && piece != null){
      board[row][col] = piece;
    } else throw new IllegalArgumentException("Out of bounds input.");
  }

  @Override
  public int[] getScore() {
    if (!gameStart) {
      throw new IllegalStateException("Game has not started.");
    }
    int whiteScore = 0;
    int blackScore = 0;
    int[] intArray = new int[2];
    for (ChessPiece piece : whiteGraveyard) {
      whiteScore += piece.getPointValue();
    }
    intArray[0] = whiteScore;
    for (ChessPiece piece : blackGraveyard) {
      blackScore += piece.getPointValue();
    }
    intArray[1] = blackScore;
    return intArray;
  }

  @Override
  public void removePiece(int row, int col) {
    if (!gameStart) {
      throw new IllegalStateException("Game has not started.");
    }
    if (isInBounds(row, col) && board[row][col] != null) {
      boolean isWhite = board[row][col].isWhite();
      if (isWhite) {
        whiteGraveyard.add(board[row][col]);
      } else {
        blackGraveyard.add(board[row][col]);
      }
      board[row][col] = null;
    } else throw new IllegalArgumentException("Out of bounds input.");
  }

  @Override
  public void movePiece(int startRow, int startCol, int endRow, int endCol) {
    if (!gameStart) {
      throw new IllegalStateException("Game has not started.");
    }
    if (!isInBounds(startRow, startCol) || !isInBounds(endRow, endCol)) {
      throw new IllegalArgumentException("Out of bounds input.");
    }
    ChessPiece startPiece = get(startRow, startCol);
    if (startPiece == null) {
      throw new IllegalArgumentException("StartPiece is empty.");
    }
    if (startPiece.isWhite() != whiteToMove) {
      throw new IllegalStateException("Initial piece must be your color.");
    }
    List<Point> validMoves = startPiece.getValidMoves(this);
    Point targetSquare = new Point(endRow, endCol);
    if (!validMoves.contains(targetSquare)) {
      throw new IllegalArgumentException("invalid move for selected piece.");
    }
    ChessPiece endPiece = board[endRow][endCol];

    if (endPiece != null && endPiece.isWhite() == startPiece.isWhite()) {
      throw new IllegalStateException("Cannot move to a square with your own piece on it.");
    }
    if (endPiece == null) {
      set(endRow, endCol, startPiece);
      removePiece(startRow, startCol);
    }
    removePiece(endRow, endCol);
    set(endRow, endCol, startPiece);
    this.whiteToMove = !whiteToMove;
  }

  public void startGame() {
    for (int i = 0; i < 8; i++) {
      this.board[1][i] = new Pawn(true, 1, i);
      whitePieces.add(this.board[1][i]);
      this.board[6][i] = new Pawn(false, 6, i);
      blackPieces.add(this.board[6][i]);
    }
    this.board[0][0] = new Rook(true, 0, 0);
    whitePieces.add(this.board[0][0]);
    this.board[0][7] = new Rook(true, 0, 7);
    whitePieces.add(this.board[0][7]);
    this.board[0][1] = new Knight(true, 0, 1);
    whitePieces.add(this.board[0][1]);
    this.board[0][6] = new Knight(true, 0, 6);
    whitePieces.add(this.board[0][6]);
    this.board[0][2] = new Bishop(true, 0, 2);
    whitePieces.add(this.board[0][2]);
    this.board[0][5] = new Bishop(true, 0, 5);
    whitePieces.add(this.board[0][5]);
    this.board[0][4] = new Queen(true, 0, 4);
    whitePieces.add(this.board[0][4]);
    this.board[0][3] = new King(true, 0, 3);
    whitePieces.add(this.board[0][3]);

    this.board[7][0] = new Rook(false, 7, 0);
    blackPieces.add(this.board[7][0]);
    this.board[7][7] = new Rook(false, 7, 7);
    blackPieces.add(this.board[7][7]);
    this.board[7][1] = new Knight(false, 7, 1);
    blackPieces.add(this.board[7][1]);
    this.board[7][6] = new Knight(false, 7, 6);
    blackPieces.add(this.board[7][6]);
    this.board[7][2] = new Bishop(false, 7, 2);
    blackPieces.add(this.board[7][2]);
    this.board[7][5] = new Bishop(false, 7, 5);
    blackPieces.add(this.board[7][5]);
    this.board[7][4] = new Queen(false, 7, 4);
    blackPieces.add(this.board[7][4]);
    this.board[7][3] = new King(false, 7, 3);
    blackPieces.add(this.board[7][3]);
    this.whiteToMove = true;
    this.gameStart = true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
          if (board[i][j] == null) {
              builder.append(" __");
          } else {
              builder.append(" ").append(board[i][j].toString());
          }
      }
      builder.append("\n");
    }
    return builder.toString();
  }

  public boolean isCheck(boolean white) {
    Point kingPosition = findKing(white);
    if (kingPosition == null) {
      throw new IllegalArgumentException("How?");
    }
    return isUnderAttack(white, kingPosition);
  }

  public boolean isUnderAttack(boolean white, Point point) {
    boolean black = !white;
    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        ChessPiece piece = get(row, col);
        if (piece != null && piece.isWhite() == black) {
          List<Point> moves = piece.getValidMoves(this);
          if (moves.contains(point)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public boolean isCheckMate(boolean isWhite) {
    if (!isCheck(isWhite)) {
      return false;
    }
    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        ChessPiece piece = get(row, col);
        if (piece != null && piece.isWhite() == isWhite) {
          List<Point> moves = piece.getValidMoves(this);
          for (Point move : moves) {
            if (wouldEscapeCheck(row, col, move.x, move.y, isWhite)) {
              return false;
            }
          }
        }
      }
    }
    return true;
  }

  private boolean wouldEscapeCheck(int startRow, int startCol, int endRow, int endCol, boolean isWhite) {
    ChessBoard temp = this.makeCopy();
    temp.movePiece(startRow, startCol, endRow, endCol);
    return !temp.isCheck(isWhite);
  }

  private boolean isInBounds(int row, int col) {
    return (row >= 0 && row <= 7) && (col >= 0 && col <= 7);
  }

  private Point findKing(boolean isWhite) {
    if (isWhite) {
      return whitePieces.get(whitePieces.size()-1).getPosition();
    } else return blackPieces.get(blackPieces.size()-1).getPosition();
  }

  private void pawnPromotion(Pawn pawn, int endRow) {
    boolean isWhite = pawn.isWhite();
    //if (isWhite && end)
  }

  @Override
  public ChessBoard makeCopy() {
    if (!gameStart) {
      throw new IllegalStateException("Game has not started.");
    }
    ChessBoard copy = new ChessBoard();
    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        ChessPiece original = this.get(row, col);
        if (original != null) {
          copy.set(row, col, original.clone());
        } else {
          copy.set(row, col, null);
        }
      }
    }
    return copy;
  }

  public boolean getTurn() {
    if (!gameStart) {
      throw new IllegalStateException("Game has not started.");
    }
    return whiteToMove;
  }
//TODO: Need to figure out logic for pawn promotion.



}
