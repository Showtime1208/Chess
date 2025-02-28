package view;
import controller.ChessController;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import java.util.*;
import model.board.ChessBoard;
import model.piece.ChessPiece;

public class ChessBoardFrame extends JFrame {
  private JPanel[][] panel;
  private ChessBoard model;
  private ChessController controller;


  public ChessBoardFrame(ChessBoard model, ChessController controller) {
    this.model = model;
    this.panel = new JPanel[8][8];
    this.controller = controller;
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
        square.addMouseListener(new MouseClickListener(row, col, controller));
        panel[row][col] = square;
        add(square);
      }
    }
  }

  public void highlightMoves(List<Point> moves) {

  }

  public void addController(ChessController controller) {
    this.controller = controller;
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

  private class MouseClickListener extends MouseAdapter {
    private int row;
    private int col;
    private ChessController controller;

    public MouseClickListener(int row, int col, ChessController controller) {
      this.row = row;
      this.col = col;
      this.controller = controller;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      controller.handleSquareClick(row, col);
    }
  }
}
