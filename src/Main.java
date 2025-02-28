import controller.ChessController;
import javax.swing.*;
import model.board.ChessBoard;
import model.piece.Bishop;
import view.ChessBoardFrame;


public class Main {

  public static void main(String[] args) {
    ChessBoard board = new ChessBoard();
    ChessBoardFrame frame = new ChessBoardFrame(board, null);
    ChessController controller = new  ChessController(board, frame);
    frame.addController(controller);
    board.startGame();
    SwingUtilities.invokeLater(() -> {
      frame.updateBoard();
      frame.setVisible(true);
    });

  }
}