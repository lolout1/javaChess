package pieces;

import utils.Position;
import board.Board;
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(String color, Position position) {
        super(color, position);
    }

    public boolean canMove(Board board, Position to) {
        int rowDiff = to.getRow() - this.getPosition().getRow();
        int colDiff = Math.abs(to.getColumn() - this.getPosition().getColumn());

        if (this.getColor().equals("white")) {
            if (rowDiff == -1 && colDiff == 0 && board.getPiece(to) == null) {
                return true;
            }
            if (this.getPosition().getRow() == 6 && rowDiff == -2 && colDiff == 0 && board.getPiece(to) == null) {
                return true;
            }
            if (rowDiff == -1 && colDiff == 1 && board.getPiece(to) != null && !board.getPiece(to).getColor().equals(this.getColor())) {
                return true;
            }
        } else if (this.getColor().equals("black")) {
            if (rowDiff == 1 && colDiff == 0 && board.getPiece(to) == null) {
                return true;
            }
            if (this.getPosition().getRow() == 1 && rowDiff == 2 && colDiff == 0 && board.getPiece(to) == null) {
                return true;
            }
            if (rowDiff == 1 && colDiff == 1 && board.getPiece(to) != null && !board.getPiece(to).getColor().equals(this.getColor())) {
                return true;
            }
        }

        return false;
    }

    public List<Position> possibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        return moves;
    }

    @Override
    public String toString() {
        return (this.getColor().equals("white")) ? "wp" : "bp";
    }
}

