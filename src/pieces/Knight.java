package pieces;

import utils.Position;
import board.Board;

public class Knight extends Piece {

    public Knight(String color, Position position) {
        super(color, position);
    }

    @Override
    public boolean canMove(Board board, Position to) {
        int rowDiff = Math.abs(to.getRow() - this.getPosition().getRow());
        int colDiff = Math.abs(to.getColumn() - this.getPosition().getColumn());

        // Knight moves in an "L" shape
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }

    @Override
    public String toString() {
        return getColor().equals("white") ? "wN" : "bN";
    }
}

