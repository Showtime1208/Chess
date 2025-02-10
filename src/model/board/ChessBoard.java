package model.board;

import model.piece.ChessPiece;
import model.piece.ChessPieceVals;
import model.piece.Piece;

public class ChessBoard implements Board {

    private ChessPiece[][] board;
    @Override
    public Piece[][] getBoard() {
        return board;
    }

    public ChessBoard() {
        this.board = new ChessPiece[8][8];
        initializeBoard();
    }
    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            this.board[1][i] = new ChessPiece(ChessPieceVals.PAWN, true);
            this.board[6][i] = new ChessPiece(ChessPieceVals.PAWN, false);
        }
        this.board[0][0] = new ChessPiece(ChessPieceVals.ROOK, true);
        this.board[0][7] = new ChessPiece(ChessPieceVals.ROOK, true);
        this.board[0][1] = new ChessPiece(ChessPieceVals.KNIGHT, true);
        this.board[0][6] = new ChessPiece(ChessPieceVals.KNIGHT, true);
        this.board[0][2] = new ChessPiece(ChessPieceVals.BISHOP, true);
        this.board[0][5] = new ChessPiece(ChessPieceVals.BISHOP, true);
        this.board[0][3] = new ChessPiece(ChessPieceVals.QUEEN, true);
        this.board[0][4] = new ChessPiece(ChessPieceVals.KING, true);

        this.board[7][0] = new ChessPiece(ChessPieceVals.ROOK, false);
        this.board[7][7] = new ChessPiece(ChessPieceVals.ROOK, false);
        this.board[7][1] = new ChessPiece(ChessPieceVals.KNIGHT, false);
        this.board[7][6] = new ChessPiece(ChessPieceVals.KNIGHT, false);
        this.board[7][2] = new ChessPiece(ChessPieceVals.BISHOP, false);
        this.board[7][5] = new ChessPiece(ChessPieceVals.BISHOP, false);
        this.board[7][3] = new ChessPiece(ChessPieceVals.QUEEN, false);
        this.board[7][4] = new ChessPiece(ChessPieceVals.KING, false);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == null) {
                    builder.append(" __");
                } else builder.append(" ").append(board[i][j].toString());
            }
            builder.append("\n");
        }
        return builder.toString();
    }

}
