package pieces;

import utils.Position;
import board.Board;

/**
 * Abstract base class for all chess pieces.
 * Provides common functionality and properties for chess pieces
 * including color, position, and movement validation.
 * 
 * @author Abheek Pradhan
 */
public abstract class Piece {
    private String color;
    private Position position;

    /**
     * Constructs a chess piece with specified color and position.
     * 
     * @param color The color of the piece ("white" or "black")
     * @param position The initial position of the piece on the board
     * @author Abheek Pradhan
     */
    public Piece(String color, Position position) {
        this.color = color;
        this.position = position;
    }

    /**
     * Gets the color of the piece.
     * 
     * @return The color of the piece ("white" or "black")
     * @author Abheek Pradhan
     */
    public String getColor() {
        return color;
    }

    /**
     * Gets the current position of the piece.
     * 
     * @return The current position of the piece
     * @author Abheek Pradhan
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Sets the position of the piece.
     * 
     * @param position The new position to set
     * @author Abheek Pradhan
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Checks if the piece can legally move to the specified position.
     * 
     * @param board The current state of the chess board
     * @param to The target position to move to
     * @return true if the move is legal, false otherwise
     * @author Abheek Pradhan
     */
    public abstract boolean canMove(Board board, Position to);
}
