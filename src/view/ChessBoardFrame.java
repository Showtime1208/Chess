package view;
import controller.ChessController;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import java.util.*;

import controller.Controller;
import model.board.ChessBoard;
import model.piece.ChessPiece;

public class ChessBoardFrame extends JFrame implements ChessView {
  private JPanel[][] panel;
  private ChessBoard model;
  private Controller controller;


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

  public void highlightMoves(List<Point> moves) {

  }

  public void setController(Controller controller) {
    this.controller = controller;
  }

  public void update() {
    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        JPanel square = panel[row][col];
        square.removeAll(); // Clear old piece

        ChessPiece piece = model.get(row, col);
        if (piece != null && piece.getIcon() != null) {
          JLabel pieceLabel = new JLabel(piece.getIcon());
          pieceLabel.setHorizontalAlignment(SwingConstants.CENTER);
          square.add(pieceLabel, BorderLayout.CENTER);
        }
      }
    }
    revalidate();
    repaint();
  }

  private class MouseClickListener extends MouseAdapter {
    private int row;
    private int col;

    public MouseClickListener(int row, int col) {
      this.row = row;
      this.col = col;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      if (controller != null) {
        controller.handleSquareClick(row, col);
      } else {
        throw new IllegalStateException();
      }
    }
  }
}
