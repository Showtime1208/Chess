package model;

import controller.ChessController;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import model.board.ChessBoard;
import model.piece.*;
import view.*;

/** Exercises real Swing controls on the event thread; requires a display (or xvfb-run). */
public final class SwingTests {
  private static int assertions;
  private static void check(boolean condition, String message) {
    assertions++; if (!condition) throw new AssertionError(message);
  }
  private static java.util.List<JButton> buttons(Container parent) {
    java.util.List<JButton> all = new ArrayList<>();
    for (Component c : parent.getComponents()) {
      if (c instanceof JButton) all.add((JButton) c);
      if (c instanceof Container) all.addAll(buttons((Container) c));
    }
    return all;
  }
  private static JButton button(Container parent, String text) {
    for (JButton b : buttons(parent)) if (text.equals(b.getText())) return b;
    throw new AssertionError("Missing control: " + text);
  }
  private static JButton square(ChessBoardFrame frame, String name) {
    for (JButton b : buttons(frame)) {
      String label = b.getAccessibleContext().getAccessibleName();
      if (label != null && (label.equals(name) || label.startsWith(name + " "))) return b;
    }
    throw new AssertionError("Missing square " + name);
  }
  private static void click(ChessBoardFrame frame, String name) { square(frame, name).doClick(0); }
  private static Timer answerDialog(String text) {
    Timer timer = new Timer(50, e -> {
      for (Window window : Window.getWindows()) if (window instanceof JDialog && window.isVisible()) {
        if (text == null) window.dispose(); else button((Container) window, text).doClick(0);
        ((Timer) e.getSource()).stop(); return;
      }
    });
    timer.start(); return timer;
  }
  public static void main(String[] args) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      ChessBoard b = new ChessBoard(); ChessBoardFrame f = new ChessBoardFrame(b);
      ChessController c = new ChessController(b, f); f.setController(c); c.playGame(); f.setVisible(true);
      try {
        check(square(f, "a1").getY() > square(f, "a8").getY(), "White perspective rank order");
        check(square(f, "a1").getX() < square(f, "h1").getX(), "White perspective file order");
        check(!button(f, "Undo move").isEnabled(), "Undo initially disabled");
        click(f, "e2"); click(f, "e4"); check(b.get(3, 4) instanceof Pawn && !b.getTurn(), "Click-to-move");
        button(f, "Flip board").doClick(0);
        check(square(f, "a1").getY() < square(f, "a8").getY(), "Flipped ranks");
        check(square(f, "a1").getX() > square(f, "h1").getX(), "Flipped files");
        click(f, "e7"); click(f, "e5"); check(b.get(4, 4) instanceof Pawn && b.getTurn(), "Flipped click mapping");
        button(f, "Undo move").doClick(0); check(b.get(6, 4) instanceof Pawn, "Undo button");
        answerDialog("Cancel"); button(f, "New game").doClick(0); check(b.getHistory().size() == 1, "Cancel restart");
        answerDialog("OK"); button(f, "New game").doClick(0); check(b.getHistory().isEmpty(), "Restart button");
        for (int cycle = 0; cycle < 2; cycle++) for (String move : new String[]{"g1f3", "g8f6", "f3g1", "f6g8"}) {
          click(f, move.substring(0, 2)); click(f, move.substring(2));
        }
        check(button(f, "Claim draw").isEnabled(), "Draw button enabled");
        button(f, "Claim draw").doClick(0); check(b.isGameOver(), "Claim button");
        button(f, "New game").doClick(0);
        for (String move : new String[]{"f2f3", "e7e5", "g2g4", "d8h4"}) {
          click(f, move.substring(0, 2)); click(f, move.substring(2));
        }
        check(b.isCheckMate(true), "Mate through clicks");
        click(f, "a2"); click(f, "a3"); check(b.get(1, 0) != null, "Terminal clicks blocked");
        button(f, "New game").doClick(0);
        for (String option : new String[]{"Queen", "Rook", "Bishop", "Knight"}) {
          answerDialog(option); check("QRBN".indexOf(f.choosePromotion(true)) >= 0, "Promotion dialog " + option);
        }
        answerDialog(null); check(f.choosePromotion(false) == 0, "Promotion cancel");
        button(f, "Flip board").doClick(0);
        click(f, "e2"); click(f, "e4"); click(f, "e7"); click(f, "e5"); click(f, "g1");
        BufferedImage image = new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics(); f.paint(graphics); graphics.dispose();
        try { ImageIO.write(image, "png", new File(args.length == 0 ? "build" : args[0], "chess-preview.png")); }
        catch (Exception ex) { throw new AssertionError(ex); }
      } finally { f.dispose(); }
      ChessBoard invertedBoard = new ChessBoard(); InvertedChessBoardFrame inverted = new InvertedChessBoardFrame(invertedBoard);
      ChessController invertedController = new ChessController(invertedBoard, inverted);
      inverted.setController(invertedController); invertedController.playGame();
      try {
        inverted.setVisible(true); click(inverted, "d2"); click(inverted, "d4");
        check(invertedBoard.get(3, 3) instanceof Pawn, "Inverted frame input");
      } finally { inverted.dispose(); }
    });
    System.out.println("PASS Swing integration: " + assertions + " assertions; preview saved to build/chess-preview.png");
  }
}
