package pieces;

import utils.Position;
import board.Board;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a Pawn piece.
 */
public class Pawn extends Piece {

    public Pawn(String color, Position position) {
        super(color, position);
    }

    @Override
    public List<Position> possibleMoves(Board board) {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return color.equals("white") ? "wp" : "bp";
    }
}

