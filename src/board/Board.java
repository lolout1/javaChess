package board;

import pieces.Piece;
import utils.Position;

/**
 * Represents the chess board and manages game state.
 * Handles piece movement, check/checkmate detection, and board state validation.
 * Maintains an 8x8 grid of pieces and provides methods for game mechanics.
 * 
 * @author Abheek Pradhan
 */
public class Board {
    private Piece[][] squares;
    private static final int BOARD_SIZE = 8;

    public Board() {
        squares = new Piece[BOARD_SIZE][BOARD_SIZE];
        initialize();
    }

    private void initialize() {
        // Set up black pieces
        squares[0][0] = new pieces.Rook("black", new Position(0, 0));
        squares[0][1] = new pieces.Knight("black", new Position(0, 1));
        squares[0][2] = new pieces.Bishop("black", new Position(0, 2));
        squares[0][3] = new pieces.Queen("black", new Position(0, 3));
        squares[0][4] = new pieces.King("black", new Position(0, 4));
        squares[0][5] = new pieces.Bishop("black", new Position(0, 5));
        squares[0][6] = new pieces.Knight("black", new Position(0, 6));
        squares[0][7] = new pieces.Rook("black", new Position(0, 7));
        for (int i = 0; i < BOARD_SIZE; i++) {
            squares[1][i] = new pieces.Pawn("black", new Position(1, i));
        }

        // Set up white pieces
        squares[7][0] = new pieces.Rook("white", new Position(7, 0));
        squares[7][1] = new pieces.Knight("white", new Position(7, 1));
        squares[7][2] = new pieces.Bishop("white", new Position(7, 2));
        squares[7][3] = new pieces.Queen("white", new Position(7, 3));
        squares[7][4] = new pieces.King("white", new Position(7, 4));
        squares[7][5] = new pieces.Bishop("white", new Position(7, 5));
        squares[7][6] = new pieces.Knight("white", new Position(7, 6));
        squares[7][7] = new pieces.Rook("white", new Position(7, 7));
        for (int i = 0; i < BOARD_SIZE; i++) {
            squares[6][i] = new pieces.Pawn("white", new Position(6, i));
        }
    }

    public Piece getPiece(Position position) {
        return squares[position.getRow()][position.getColumn()];
    }

    /**
     * Attempts to move a piece from one position to another.
     * Validates move legality including check conditions.
     */
    public boolean movePiece(Position from, Position to) {
        Piece piece = getPiece(from);
        
        if (piece == null) {
            return false;
        }

        // Basic move validation
        if (!piece.canMove(this, to)) {
            return false;
        }

        // Check for moving onto own piece
        Piece targetPiece = getPiece(to);
        if (targetPiece != null && piece.getColor().equals(targetPiece.getColor())) {
            return false;
        }

        // Simulate move to check if it would result in check
        Board tempBoard = copyBoard();
        tempBoard.squares[to.getRow()][to.getColumn()] = piece;
        tempBoard.squares[from.getRow()][from.getColumn()] = null;
        
        // Prevent moves that would put/leave own king in check
        if (tempBoard.isInCheck(piece.getColor())) {
            return false;
        }

        // Execute the move
        squares[to.getRow()][to.getColumn()] = piece;
        squares[from.getRow()][from.getColumn()] = null;
        piece.setPosition(to);
        
        return true;
    }

    /**
     * Checks if the specified color's king is in check.
     */
    public boolean isInCheck(String color) {
        Position kingPos = findKing(color);
        if (kingPos == null) return false;

        // Check if any opponent's piece can attack the king
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = squares[row][col];
                if (piece != null && !piece.getColor().equals(color)) {
                    if (piece.canMove(this, kingPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the specified color is in checkmate.
     */
    public boolean isCheckmate(String color) {
        if (!isInCheck(color)) {
            return false;
        }

        // Try all possible moves for all pieces
        for (int fromRow = 0; fromRow < BOARD_SIZE; fromRow++) {
            for (int fromCol = 0; fromCol < BOARD_SIZE; fromCol++) {
                Piece piece = squares[fromRow][fromCol];
                if (piece != null && piece.getColor().equals(color)) {
                    // Try moving this piece to every square
                    for (int toRow = 0; toRow < BOARD_SIZE; toRow++) {
                        for (int toCol = 0; toCol < BOARD_SIZE; toCol++) {
                            Position from = new Position(fromRow, fromCol);
                            Position to = new Position(toRow, toCol);
                            
                            // If any legal move exists, it's not checkmate
                            if (canMoveWithoutCheck(from, to)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Tests if a move can be made without resulting in check.
     */
    private boolean canMoveWithoutCheck(Position from, Position to) {
        Piece piece = getPiece(from);
        if (piece == null) return false;

        if (!piece.canMove(this, to)) return false;

        Board tempBoard = copyBoard();
        tempBoard.squares[to.getRow()][to.getColumn()] = piece;
        tempBoard.squares[from.getRow()][from.getColumn()] = null;
        
        return !tempBoard.isInCheck(piece.getColor());
    }
    
    /**
     * Creates a deep copy of the board for move simulation.
     */
    public Board copyBoard() {
        Board copy = new Board();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece original = this.squares[row][col];
                if (original != null) {
                    copy.squares[row][col] = createPieceCopy(original);
                }
            }
        }
        return copy;
    }

    /**
     * Creates a deep copy of a piece.
     */
    private Piece createPieceCopy(Piece original) {
        String color = original.getColor();
        Position pos = new Position(original.getPosition().getRow(), 
                                  original.getPosition().getColumn());
        
        switch (original.getClass().getSimpleName()) {
            case "King": return new pieces.King(color, pos);
            case "Queen": return new pieces.Queen(color, pos);
            case "Rook": return new pieces.Rook(color, pos);
            case "Bishop": return new pieces.Bishop(color, pos);
            case "Knight": return new pieces.Knight(color, pos);
            case "Pawn": return new pieces.Pawn(color, pos);
            default: return null;
        }
    }

    /**
     * Finds the king of the specified color.
     */
    private Position findKing(String color) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = squares[row][col];
                if (piece != null && 
                    piece instanceof pieces.King && 
                    piece.getColor().equals(color)) {
                    return new Position(row, col);
                }
            }
        }
        return null;
    }

    /**
     * Checks if a move would expose the king to check.
     */
    public boolean wouldMoveExposeCheck(Position from, Position to) {
        Board tempBoard = copyBoard();
        tempBoard.movePiece(from, to);
        return tempBoard.isInCheck(getPiece(from).getColor());
    }

    // Helper methods for move validation
    public boolean isPathClear(Position from, Position to) {
        int rowDiff = to.getRow() - from.getRow();
        int colDiff = to.getColumn() - from.getColumn();

        int rowStep = Integer.signum(rowDiff);
        int colStep = Integer.signum(colDiff);

        int currentRow = from.getRow() + rowStep;
        int currentCol = from.getColumn() + colStep;

        while (currentRow != to.getRow() || currentCol != to.getColumn()) {
            if (getPiece(new Position(currentRow, currentCol)) != null) {
                return false;
            }
            currentRow += rowStep;
            currentCol += colStep;
        }

        return true;
    }

    public void placePiece(Piece piece, Position pos) {
        squares[pos.getRow()][pos.getColumn()] = piece;
        piece.setPosition(pos);
    }

    // Board display for console mode
    public void display() {
        String horizontalLine = "   +-----+-----+-----+-----+-----+-----+-----+-----+";
        System.out.println();
        
        for (int row = 0; row < BOARD_SIZE; row++) {
            System.out.println(horizontalLine);
            System.out.print(" " + (8 - row) + " |");
            
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = squares[row][col];
                String square = piece != null ? String.format(" %-3s ", piece.toString()) : "     ";
                System.out.print(square + "|");
            }
            System.out.println();
        }
        
        System.out.println(horizontalLine);
        System.out.print("     ");
        for (char c = 'A'; c <= 'H'; c++) {
            System.out.print("  " + c + "   ");
        }
        System.out.println();
    }
}