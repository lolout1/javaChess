package pieces;

import utils.Position;
import board.Board;

public class Bishop extends Piece {

    public Bishop(String color, Position position) {
        super(color, position);
    }

    @Override
    public boolean canMove(Board board, Position to) {
        int rowDiff = Math.abs(to.getRow() - this.getPosition().getRow());
        int colDiff = Math.abs(to.getColumn() - this.getPosition().getColumn());

        // Bishop moves diagonally, rowDiff must equal colDiff
        if (rowDiff == colDiff) {
            return board.isPathClear(this.getPosition(), to);
        }

        return false;
    }

    @Override
    public String toString() {
        return getColor().equals("white") ? "wB" : "bB";
    }
}

