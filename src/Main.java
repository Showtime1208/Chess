import javax.swing.*;
import model.board.ChessBoard;
import model.piece.Bishop;
import view.ChessBoardFrame;


public class Main {

  public static void main(String[] args) {
    ChessBoard board = new ChessBoard();
    board.startGame();
    board.movePiece(1, 0, 2, 0);
    SwingUtilities.invokeLater(() -> {
      ChessBoardFrame frame = new ChessBoardFrame(board);
      frame.updateBoard();
      frame.setVisible(true);
    });

  }
}