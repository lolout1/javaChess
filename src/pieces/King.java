package pieces;

import utils.Position;
import board.Board;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a King piece.
 */
public class King extends Piece {

    public King(String color, Position position) {
        super(color, position);
    }

    @Override
    public List<Position> possibleMoves(Board board) {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return color.equals("white") ? "wK" : "bK";
    }
}

