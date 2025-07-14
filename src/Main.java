import controller.ChessController;
import javax.swing.*;
import model.board.ChessBoard;
import model.piece.Bishop;
import view.ChessBoardFrame;


public class Main {

  public static void main(String[] args) {
    ChessBoard board = new ChessBoard();
    ChessBoardFrame frame = new ChessBoardFrame(board);
    ChessController controller = new  ChessController(board, frame);
    frame.setController(controller);
    controller.playGame();
    SwingUtilities.invokeLater(() -> {
      frame.update();
      frame.setVisible(true);
    });

  }
}