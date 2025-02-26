import javax.swing.*;
import model.board.ChessBoard;
import view.ChessBoardFrame;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

  public static void main(String[] args) {
    ChessBoard board = new ChessBoard();
    board.startGame();
    SwingUtilities.invokeLater(() -> {
      ChessBoardFrame frame = new ChessBoardFrame(board);
      frame.updateBoard();
      frame.setVisible(true);

    });

  }
}