package view;

import model.board.ChessBoard;
import javax.swing.*;
import java.awt.*;

public interface ChessView {
  void setController(controller.Controller controller);
  void update();

  void highlightMoves(java.util.List<Point> moves);
}
