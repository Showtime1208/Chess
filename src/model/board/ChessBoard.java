package model.board;


import java.awt.Point;
import java.util.List;
import model.piece.Bishop;
import model.piece.ChessPiece;
import model.piece.King;
import model.piece.Knight;
import model.piece.Pawn;


import model.piece.Queen;
import model.piece.Rook;

public class ChessBoard implements Board {

  private ChessPiece[][] board;
  private boolean whiteToMove;
  private List<ChessPiece> whitePieces;
  private List<ChessPiece> blackPieces;

  @Override
  public ChessPiece[][] getBoard() {
    return board;
  }

  public ChessBoard() {
    this.board = new ChessPiece[8][8];
    initializeBoard();
  }

  @Override
  public ChessPiece get(int row, int col) {
    if (isInBounds(row, col)) {
      return board[row][col];
    }
    else throw new IllegalArgumentException("Out of bounds input.");
  }

  @Override
  public void set(int row, int col, ChessPiece piece) {
    if (isInBounds(row, col) && piece != null){
      board[row][col] = piece;
    } else throw new IllegalArgumentException("Out of bounds input.");
  }

  @Override
  public void removePiece(int row, int col) {
    if (isInBounds(row, col) && board[row][col] != null) {
      board[row][col] = null;
    } else throw new IllegalArgumentException("Out of bounds input.");
  }

  @Override
  public void movePiece(int startRow, int startCol, int endRow, int endCol) {
    if (!isInBounds(startRow, startCol) || !isInBounds(endRow, endCol)) {
      throw new IllegalArgumentException("Out of bounds input.");
    }
    ChessPiece startPiece = get(startRow, startCol);
    if (startPiece == null) {
      throw new IllegalArgumentException("StartPiece is empty.");
    }
    List<Point> validMoves = startPiece.getValidMoves(this);
    Point targetSquare = new Point(endRow, endCol);
    if (!validMoves.contains(targetSquare)) {
      throw new IllegalArgumentException("invalid move for selected piece.");
    }
    ChessPiece endPiece = board[endRow][endCol];

    if (endPiece != null && endPiece.isWhite() == startPiece.isWhite()) {
      throw new IllegalArgumentException("Cannot move to a square with your own piece on it.");
    }
    removePiece(endRow, endCol);
    set(endRow, endCol, startPiece);
    this.whiteToMove = !whiteToMove;
  }

  private void initializeBoard() {
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
    this.board[0][2] = new Bishop(true, 0, 2);
    whitePieces.add(this.board[0][2]);
    this.board[0][5] = new Bishop(true, 0, 5);
    whitePieces.add(this.board[0][5]);
    this.board[0][3] = new Queen(true, 0, 3);
    whitePieces.add(this.board[0][3]);
    this.board[0][4] = new King(true, 0, 4);
    whitePieces.add(this.board[0][4]);

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
    this.board[7][3] = new Queen(false, 7, 3);
    blackPieces.add(this.board[7][3]);
    this.board[7][4] = new King(false, 7, 4);
    blackPieces.add(this.board[7][4]);
    this.whiteToMove = true;
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
    boolean black = !white;
    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        ChessPiece piece =  get(row, col);
        if (piece != null && piece.isWhite() == black) {
          List<Point> moves = piece.getValidMoves(this);
          if (moves.contains(kingPosition)) {
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
      return whitePieces.getLast().getPosition();
    } else return blackPieces.getLast().getPosition();
  }

  @Override
  public ChessBoard makeCopy() {
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
//TODO: Need to figure out logic for castling



}
