package view;

import controller.Controller;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.board.ChessBoard;
import model.piece.ChessPiece;
import model.piece.King;

/** Local two-player chess, with one coordinate mapping shared by rendering and input. */
public class ChessBoardFrame extends JFrame implements ChessView {
  private static final long serialVersionUID = 1L;
  private static final Color DARK = new Color(73, 108, 91), LIGHT = new Color(238, 232, 213);
  private static final Color BACKGROUND = new Color(26, 36, 32), INK = new Color(239, 240, 227);
  private final ChessBoard model;
  private Controller controller;
  private final Square[][] squares = new Square[8][8];
  private final JLabel status = new JLabel("White to move"), score = new JLabel();
  private final JLabel message = new JLabel("Select a piece to see its legal moves.");
  private final JTextArea history = new JTextArea();
  private final JButton undo = new JButton("Undo move"), draw = new JButton("Claim draw");
  private int selectedRow = -1, selectedCol = -1;
  private List<Point> highlightedMoves = new ArrayList<>();
  private boolean flipped;

  public ChessBoardFrame(ChessBoard model) { this(model, false); }
  protected ChessBoardFrame(ChessBoard model, boolean flipped) {
    this.model = model; this.flipped = flipped;
    setTitle("Chess — Local two-player game");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel content = new JPanel(new BorderLayout(20, 18));
    content.setBackground(BACKGROUND);
    content.setBorder(new EmptyBorder(22, 24, 20, 24));
    setContentPane(content);
    JPanel header = new JPanel(new GridLayout(2, 1, 0, 8));
    header.setOpaque(false);
    JLabel title = new JLabel("CHESS");
    title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28)); title.setForeground(INK);
    status.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18)); status.setForeground(INK);
    header.add(title); header.add(status); content.add(header, BorderLayout.NORTH);
    JPanel grid = new JPanel(new GridLayout(8, 8));
    grid.setPreferredSize(new Dimension(560, 560));
    for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) {
      Square square = new Square(r, c); squares[r][c] = square; grid.add(square);
    }
    JPanel boardHolder = new JPanel(new GridBagLayout()); boardHolder.setOpaque(false);
    boardHolder.add(grid);
    content.add(boardHolder, BorderLayout.CENTER);
    JPanel side = new JPanel(new BorderLayout(0, 14)); side.setOpaque(false);
    side.setPreferredSize(new Dimension(205, 560));
    JPanel info = new JPanel(new GridLayout(2, 1, 0, 8)); info.setOpaque(false);
    JLabel moves = new JLabel("MOVE HISTORY"); moves.setForeground(INK);
    moves.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
    score.setForeground(INK); info.add(moves); info.add(score); side.add(info, BorderLayout.NORTH);
    history.setEditable(false); history.setFocusable(false);
    history.setBackground(new Color(35, 47, 41)); history.setForeground(INK);
    history.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    history.setBorder(new EmptyBorder(10, 10, 10, 10));
    JScrollPane scroll = new JScrollPane(history); scroll.setBorder(BorderFactory.createEmptyBorder());
    side.add(scroll, BorderLayout.CENTER);
    JPanel actions = new JPanel(new GridLayout(4, 1, 0, 8)); actions.setOpaque(false);
    JButton newGame = new JButton("New game"), flip = new JButton("Flip board");
    newGame.addActionListener(e -> {
      if (model.getHistory().isEmpty() || model.isGameOver() || JOptionPane.showConfirmDialog(this,
          "Start a new game? The current game will be cleared.", "New game", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        controller.playGame();
    });
    undo.addActionListener(e -> controller.undoMove());
    flip.addActionListener(e -> { this.flipped = !this.flipped; update(); });
    draw.addActionListener(e -> controller.claimDraw());
    for (JButton button : new JButton[]{newGame, undo, flip, draw}) {
      button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14)); actions.add(button);
    }
    side.add(actions, BorderLayout.SOUTH); content.add(side, BorderLayout.EAST);
    message.setForeground(new Color(188, 204, 192));
    message.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13)); content.add(message, BorderLayout.SOUTH);
    pack(); setResizable(false); setLocationRelativeTo(null);
  }
  public void setController(Controller controller) { this.controller = controller; }
  public void highlightMoves(List<Point> moves) { highlightedMoves = new ArrayList<>(moves); }
  public void setSelection(int r, int c) { selectedRow = r; selectedCol = c; }
  public void showMessage(String text) { message.setText(text); }
  public char choosePromotion(boolean white) {
    String[] options = {"Queen", "Rook", "Bishop", "Knight"};
    int choice = JOptionPane.showOptionDialog(this, "Choose your promotion piece.",
        (white ? "White" : "Black") + " pawn promotion", JOptionPane.DEFAULT_OPTION,
        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    return choice < 0 ? 0 : "QRBN".charAt(choice);
  }
  public void update() {
    status.setText(model.getStatus());
    int[] points = model.getScore(); score.setText("Captured: White " + points[0] + "  ·  Black " + points[1]);
    undo.setEnabled(model.canUndo()); draw.setEnabled(model.canClaimDraw());
    StringBuilder text = new StringBuilder(); List<String> moves = model.getHistory();
    for (int i = 0; i < moves.size(); i++) {
      if (i % 2 == 0) text.append(i / 2 + 1).append(". "); else text.append("   ");
      text.append(moves.get(i)).append('\n');
    }
    if (!history.getText().equals(text.toString())) {
      history.setText(text.toString()); history.setCaretPosition(history.getDocument().getLength());
    }
    message.setText(model.isGameOver() ? "Game finished. Start a new game or undo a move."
        : selectedRow < 0 ? "Select a piece to see its legal moves." : "Choose a highlighted square to move.");
    for (Square[] rank : squares) for (Square square : rank) square.refresh();
    repaint();
  }
  private int modelRow(int displayRow) { return flipped ? displayRow : 7 - displayRow; }
  private int modelCol(int displayCol) { return flipped ? 7 - displayCol : displayCol; }
  private final class Square extends JButton {
    private static final long serialVersionUID = 1L;
    private final int displayRow, displayCol;
    private boolean checked;
    Square(int r, int c) {
      displayRow = r; displayCol = c;
      setBorderPainted(false); setContentAreaFilled(false); setFocusPainted(false);
      setMargin(new Insets(0, 0, 0, 0));
      addActionListener(e -> { if (controller != null) controller.handleSquareClick(modelRow(r), modelCol(c)); });
    }
    void refresh() {
      int r = modelRow(displayRow), c = modelCol(displayCol);
      ChessPiece piece = ChessBoardFrame.this.model.get(r, c);
      String label = "" + (char) ('a' + c) + (r + 1);
      if (piece != null) label += " " + (piece.isWhite() ? "White " : "Black ") + piece.getClass().getSimpleName();
      getAccessibleContext().setAccessibleName(label); setToolTipText(label);
      checked = piece instanceof King && ChessBoardFrame.this.model.isCheck(piece.isWhite());
    }
    @Override protected void paintComponent(Graphics graphics) {
      Graphics2D g = (Graphics2D) graphics.create();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      int r = modelRow(displayRow), c = modelCol(displayCol), w = getWidth(), h = getHeight();
      boolean selected = r == selectedRow && c == selectedCol;
      g.setColor(selected ? new Color(211, 186, 96) : checked ? new Color(198, 104, 89) : (r + c) % 2 == 0 ? DARK : LIGHT);
      g.fillRect(0, 0, w, h);
      ChessPiece piece = ChessBoardFrame.this.model.get(r, c);
      if (piece != null) {
        ImageIcon icon = piece.getIcon();
        double scale = Math.min((w - 14.0) / icon.getIconWidth(), (h - 14.0) / icon.getIconHeight());
        int iw = (int) (icon.getIconWidth() * scale), ih = (int) (icon.getIconHeight() * scale);
        g.drawImage(icon.getImage(), (w - iw) / 2, (h - ih) / 2, iw, ih, null);
      }
      if (highlightedMoves.contains(new Point(r, c))) {
        g.setColor(new Color(25, 54, 35, 120));
        if (piece == null) g.fillOval(w / 2 - 7, h / 2 - 7, 14, 14);
        else { g.setStroke(new BasicStroke(4)); g.drawOval(5, 5, w - 10, h - 10); }
      }
      g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
      g.setColor((r + c) % 2 == 0 ? LIGHT : DARK);
      if (displayCol == 0) g.drawString(Integer.toString(r + 1), 4, 13);
      if (displayRow == 7) g.drawString(Character.toString((char) ('a' + c)), w - 12, h - 5);
      if (isFocusOwner()) { g.setColor(new Color(228, 176, 67)); g.setStroke(new BasicStroke(3)); g.drawRect(2, 2, w - 4, h - 4); }
      g.dispose();
    }
  }
}
