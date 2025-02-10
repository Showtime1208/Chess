package model.board;


import model.piece.Bishop;
import model.piece.ChessPiece;
import model.piece.King;
import model.piece.Knight;
import model.piece.Pawn;


import model.piece.Queen;
import model.piece.Rook;

public class ChessBoard implements Board {

    private ChessPiece[][] board;
    @Override
    public ChessPiece[][] getBoard() {
        return board;
    }

    public ChessBoard() {
        this.board = new ChessPiece[8][8];
        initializeBoard();
    }
    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            this.board[1][i] = new Pawn(true, 1, i);
            this.board[6][i] = new Pawn(false, 6, i);
        }
        this.board[0][0] = new Rook(true, 0, 0);
        this.board[0][7] = new Rook( true, 0, 7);
        this.board[0][1] = new Knight(true, 0, 1);
        this.board[0][6] = new Knight(true, 0, 6);
        this.board[0][2] = new Bishop(true, 0, 2);
        this.board[0][5] = new Bishop(true, 0, 5);
        this.board[0][3] = new Queen(true, 0, 3);
        this.board[0][4] = new King(true,0 ,4);

        this.board[7][0] = new Rook(false, 7, 0);
        this.board[7][7] = new Rook(false,7, 7);
        this.board[7][1] = new Knight(false, 7, 1);
        this.board[7][6] = new Knight(false, 7, 6);
        this.board[7][2] = new Bishop(false, 7, 2);
        this.board[7][5] = new Bishop(false, 7, 5);
        this.board[7][3] = new Queen(false, 7, 3);
        this.board[7][4] = new King(false, 7, 4);
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
