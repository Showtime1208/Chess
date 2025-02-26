import javax.swing.*;
import model.board.ChessBoard;
import model.piece.Bishop;
import view.ChessBoardFrame;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

  public static void main(String[] args) {
    ChessBoard board = new ChessBoard();
    board.startGame();
    board.movePiece(1, 0, 2, 0);
    SwingUtilities.invokeLater(() -> {
      ChessBoardFrame frame = new ChessBoardFrame(board);
      frame.updateBoard();
      frame.setVisible(true);
      Icon icon = new Bishop(true, 0, 0).getIcon();
      JLabel label = new JLabel(icon);
      new JLabel(icon).setVisible(true);

    });

  }
}