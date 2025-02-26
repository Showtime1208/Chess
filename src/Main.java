import javax.swing.SwingUtilities;
import model.board.ChessBoard;
import view.ChessBoardFrame;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

  public static void main(String[] args) {
    ChessBoard board = new ChessBoard();
    SwingUtilities.invokeLater(() -> {
      ChessBoardFrame frame = new ChessBoardFrame(board);
      frame.setVisible(true);
      frame.updateBoard();
    });

  }
}