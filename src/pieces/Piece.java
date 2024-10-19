package pieces;

import utils.Position;
import board.Board;
import java.util.List;

/**
 * Abstract class representing a chess piece.
 */
public abstract class Piece {
    protected String color;
    protected Position position;

    public Piece(String color, Position position) {
        this.color = color;
        this.position = position;
    }

    public String getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public abstract List<Position> possibleMoves(Board board);

    public void move(Position newPosition) {
        this.position = newPosition;
    }

    public abstract String toString();
}

