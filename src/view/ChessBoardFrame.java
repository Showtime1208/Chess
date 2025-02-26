package view;
import java.awt.*;
import javax.swing.*;
import model.board.ChessBoard;
import model.piece.ChessPiece;

public class ChessBoardFrame extends JFrame {
  private JPanel[][] panel;
  private ChessBoard model;


  public ChessBoardFrame(ChessBoard model) {
    this.model = model;
    this.panel = new JPanel[8][8];
    setTitle("Chess Board");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(600, 600);
    setLayout(new GridLayout(8, 8));
    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        JPanel square = new JPanel();
        if ((row + col) % 2 == 0) {
          square.setBackground(Color.WHITE);
        } else {
          square.setBackground(Color.GRAY);
        }
        square.addMouseListener(new MouseClickListener(row, col));
        panel[row][col] = square;
        add(square);
      }
    }
  }

  public void updateBoard() {
    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        JPanel square = panel[row][col];
        ChessPiece piece = model.get(row, col);
        if (piece != null) {
          JLabel pieceLabel = new JLabel(piece.getIcon());
          square.add(pieceLabel);
        }
        square.revalidate();
        square.repaint();
      }
    }
  }
}
