package pieces;

import utils.Position;
import board.Board;

public class Bishop extends Piece {

    public Bishop(String color, Position position) {
        super(color, position);
    }

    @Override
    public boolean canMove(Board board, Position to) {
        // Logic for diagonal movement
        int rowDiff = Math.abs(to.getRow() - this.getPosition().getRow());
        int colDiff = Math.abs(to.getColumn() - this.getPosition().getColumn());
        return rowDiff == colDiff;
    }

    @Override
    public String toString() {
        return getColor().equals("white") ? "wB" : "bB";
    }
}

