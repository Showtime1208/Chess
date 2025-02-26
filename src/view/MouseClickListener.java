package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseClickListener extends MouseAdapter {
  private int row;
  private int col;

  public MouseClickListener(int row, int col) {
    this.row = row;
    this.col = col;
  }

  @Override
  public void mouseClicked(MouseEvent e) {

  }

}
