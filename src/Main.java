import controller.ChessController;
import javax.swing.SwingUtilities;
import model.board.ChessBoard;
import view.ChessBoardFrame;

public class Main {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      ChessBoard board = new ChessBoard();
      ChessBoardFrame frame = new ChessBoardFrame(board);
      ChessController controller = new ChessController(board, frame);
      frame.setController(controller);
      controller.playGame();
      frame.setVisible(true);
    });
  }
}
