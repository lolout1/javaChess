package board;

import pieces.Piece;
import pieces.*;
import utils.Position;

/**
 * Class representing the chessboard.
 */
public class Board {
    private Piece[][] squares; // 8x8 array representing the board

    /**
     * Constructs a new Board and initializes the pieces.
     */
    public Board() {
        squares = new Piece[8][8];
        initialize();
    }

    /**
     * Initializes the board with pieces in their initial positions.
     */
    private void initialize() {
        // Place black pieces
        squares[0][0] = new Rook("black", new Position(0, 0));
        squares[0][1] = new Knight("black", new Position(0, 1));
        squares[0][2] = new Bishop("black", new Position(0, 2));
        squares[0][3] = new Queen("black", new Position(0, 3));
        squares[0][4] = new King("black", new Position(0, 4));
        squares[0][5] = new Bishop("black", new Position(0, 5));
        squares[0][6] = new Knight("black", new Position(0, 6));
        squares[0][7] = new Rook("black", new Position(0, 7));
        for (int i = 0; i < 8; i++) {
            squares[1][i] = new Pawn("black", new Position(1, i));
        }

        // Place white pieces
        squares[7][0] = new Rook("white", new Position(7, 0));
        squares[7][1] = new Knight("white", new Position(7, 1));
        squares[7][2] = new Bishop("white", new Position(7, 2));
        squares[7][3] = new Queen("white", new Position(7, 3));
        squares[7][4] = new King("white", new Position(7, 4));
        squares[7][5] = new Bishop("white", new Position(7, 5));
        squares[7][6] = new Knight("white", new Position(7, 6));
        squares[7][7] = new Rook("white", new Position(7, 7));
        for (int i = 0; i < 8; i++) {
            squares[6][i] = new Pawn("white", new Position(6, i));
        }

        // Empty squares are null
    }

    /**
     * Gets the piece at the specified position.
     * @param position The position to get the piece from.
     * @return The piece at the position, or null if empty.
     */
    public Piece getPiece(Position position) {
        return squares[position.getRow()][position.getColumn()];
    }

    /**
     * Moves a piece from one position to another.
     * @param from The starting position.
     * @param to The destination position.
     * @return True if move was successful, false otherwise.
     */
    public boolean movePiece(Position from, Position to) {
        Piece piece = getPiece(from);
        if (piece == null) {
            return false; // No piece at source position
        }
        // For phase one, we assume the move is valid
        // Update the board
        squares[to.getRow()][to.getColumn()] = piece;
        squares[from.getRow()][from.getColumn()] = null;
        piece.setPosition(to);
        return true;
    }

    /**
     * Displays the board to the console.
     */
    public void display() {
        System.out.println();
        for (int row = 0; row < 8; row++) {
            int displayRow = 8 - row;
            System.out.print(displayRow + " ");
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                String square;
                if (piece != null) {
                    square = piece.toString();
                } else {
                    // Alternate between "##" and "  " for squares
                    if ((row + col) % 2 == 0) {
                        square = "##";
                    } else {
                        square = "  ";
                    }
                }
                System.out.print(square + " ");
            }
            System.out.println();
        }
        // Print column labels
        System.out.print("  ");
        for (char c = 'A'; c <= 'H'; c++) {
            System.out.print(" " + c + " ");
        }
        System.out.println();
    }

}
