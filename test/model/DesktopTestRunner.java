package model;

import java.awt.Window;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import javax.swing.*;

/** A macOS application entry point for UI tests when a shell has no WindowServer access. */
public final class DesktopTestRunner {
  public static void main(String[] args) throws Exception {
    Path output = Paths.get(args[0]);
    String result;
    try {
      SwingTests.main(args);
      Class.forName("Main").getMethod("main", String[].class).invoke(null, (Object) new String[0]);
      SwingUtilities.invokeAndWait(() -> {
        boolean found = false;
        for (Window w : Window.getWindows()) if (w.isVisible()) { found = true; w.dispose(); }
        if (!found) throw new AssertionError("Main did not open its window");
      });
      result = "PASS: Swing integration tests and application startup.";
    } catch (Throwable error) {
      java.io.StringWriter trace = new java.io.StringWriter();
      error.printStackTrace(new java.io.PrintWriter(trace)); result = "FAIL: " + trace;
    }
    Files.write(output.resolve("desktop-tests.txt"), result.getBytes(StandardCharsets.UTF_8));
    String finalResult = result;
    SwingUtilities.invokeLater(() -> {
      JFrame f = new JFrame("Chess test results");
      JTextArea text = new JTextArea(finalResult); text.setEditable(false);
      text.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
      f.add(new JScrollPane(text)); f.setSize(650, 180); f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setLocationRelativeTo(null); f.setVisible(true);
    });
  }
}
