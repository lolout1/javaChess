package pieces;

import utils.Position;
import board.Board;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a Knight piece.
 */
public class Knight extends Piece {

    public Knight(String color, Position position) {
        super(color, position);
    }

    @Override
    public List<Position> possibleMoves(Board board) {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return color.equals("white") ? "wN" : "bN";
    }
}

