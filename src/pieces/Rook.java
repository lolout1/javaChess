package pieces;

import utils.Position;
import board.Board;

public class Rook extends Piece {

    public Rook(String color, Position position) {
        super(color, position);
    }

    @Override
    public boolean canMove(Board board, Position to) {
        int rowDiff = Math.abs(to.getRow() - this.getPosition().getRow());
        int colDiff = Math.abs(to.getColumn() - this.getPosition().getColumn());
        return rowDiff == 0 || colDiff == 0;
    }

    @Override
    public String toString() {
        return getColor().equals("white") ? "wR" : "bR";
    }
}

