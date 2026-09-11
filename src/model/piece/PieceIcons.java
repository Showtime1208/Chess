package model.piece;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/** Classpath resources work both from compiled classes and the runnable JAR. */
final class PieceIcons {
  private static final Map<String, ImageIcon> CACHE = new HashMap<>();
  private PieceIcons() { }
  static synchronized ImageIcon get(String name) {
    return CACHE.computeIfAbsent(name, key -> {
      URL resource = PieceIcons.class.getResource("/pieceImages/" + key + ".png");
      if (resource == null) throw new IllegalStateException("Missing piece image: " + key);
      return new ImageIcon(resource);
    });
  }
}
