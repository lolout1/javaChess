package pieces;

import utils.Position;
import board.Board;

public class Queen extends Piece {

    public Queen(String color, Position position) {
        super(color, position);
    }

    @Override
    public boolean canMove(Board board, Position to) {
        int rowDiff = Math.abs(to.getRow() - this.getPosition().getRow());
        int colDiff = Math.abs(to.getColumn() - this.getPosition().getColumn());

        // Queen moves like a Rook or Bishop (straight or diagonal)
        if (rowDiff == colDiff || rowDiff == 0 || colDiff == 0) {
            return board.isPathClear(this.getPosition(), to);
        }

        return false;
    }

    @Override
    public String toString() {
        return getColor().equals("white") ? "wQ" : "bQ";
    }
}

