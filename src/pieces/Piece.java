package pieces;

import utils.Position;
import board.Board;

public abstract class Piece {
    private String color;
    private Position position;

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

    public abstract boolean canMove(Board board, Position to);
}

