package model.piece;

import java.awt.List;
import model.board.Board;

public class Rook implements ChessPiece {

  private final boolean isWhite;

  public Rook(boolean isWhite) {
    this.isWhite = isWhite;
  }
  @Override
  public List getValidMoves(Board boardState) {
    return null;
  }

  @Override
  public boolean isWhite() {
    return isWhite;
  }

  @Override
  public int getPointValue() {
    return 5;
  }
}
