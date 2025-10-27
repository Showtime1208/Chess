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

public class InvertedChessBoardFrame extends JFrame implements ChessView {
  private JPanel[][] panel;
  private ChessBoard model;
  private Controller controller;
  private int selectedRow = -1;
  private int selectedCol = -1;
  private List<Point> highlightedMoves = new ArrayList<>();


  public InvertedChessBoardFrame(ChessBoard model) {
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
    this.highlightedMoves = new ArrayList<>(moves);
    update();
  }

  public void setController(controller.Controller controller) {
    this.controller = controller;
  }

  public void setSelection(int row, int col) {
    this.selectedRow = row;
    this.selectedCol = col;
    update();
  }

  public void update() {
    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        JPanel square = panel[row][col];
        square.removeAll(); // Clear old piece

        // Invert the coordinates for black player view
        int invertedRow = 7 - row;
        int invertedCol = 7 - col;
        
        // Set background color based on selection and highlights
        if (invertedRow == selectedRow && invertedCol == selectedCol) {
          square.setBackground(Color.YELLOW); // Selected piece
        } else if (highlightedMoves.contains(new Point(invertedRow, invertedCol))) {
          square.setBackground(Color.GREEN); // Valid move
        } else if ((row + col) % 2 == 0) {
          square.setBackground(Color.WHITE);
        } else {
          square.setBackground(Color.GRAY);
        }

        ChessPiece piece = model.get(invertedRow, invertedCol);
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
        // Invert the coordinates for black player view
        int invertedRow = 7 - row;
        int invertedCol = 7 - col;
        controller.handleSquareClick(invertedRow, invertedCol);
      } else {
        throw new IllegalStateException();
      }
    }
  }
}
