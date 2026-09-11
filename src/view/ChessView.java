package view;

import java.awt.Point;
import java.util.List;

public interface ChessView {
  void setController(controller.Controller controller);
  void update();
  void highlightMoves(List<Point> moves);
  void setSelection(int row, int col);
  default char choosePromotion(boolean white) { return 'Q'; }
  default void showMessage(String message) { }
}
