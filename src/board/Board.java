package board;

import pieces.Piece;
import utils.Position;

public class Board {
    private Piece[][] squares;

    public Board() {
        squares = new Piece[8][8];
        initialize();
    }

    private void initialize() {
        squares[0][0] = new pieces.Rook("black", new Position(0, 0));
        squares[0][1] = new pieces.Knight("black", new Position(0, 1));
        squares[0][2] = new pieces.Bishop("black", new Position(0, 2));
        squares[0][3] = new pieces.Queen("black", new Position(0, 3));
        squares[0][4] = new pieces.King("black", new Position(0, 4));
        squares[0][5] = new pieces.Bishop("black", new Position(0, 5));
        squares[0][6] = new pieces.Knight("black", new Position(0, 6));
        squares[0][7] = new pieces.Rook("black", new Position(0, 7));
        for (int i = 0; i < 8; i++) {
            squares[1][i] = new pieces.Pawn("black", new Position(1, i));
        }

        squares[7][0] = new pieces.Rook("white", new Position(7, 0));
        squares[7][1] = new pieces.Knight("white", new Position(7, 1));
        squares[7][2] = new pieces.Bishop("white", new Position(7, 2));
        squares[7][3] = new pieces.Queen("white", new Position(7, 3));
        squares[7][4] = new pieces.King("white", new Position(7, 4));
        squares[7][5] = new pieces.Bishop("white", new Position(7, 5));
        squares[7][6] = new pieces.Knight("white", new Position(7, 6));
        squares[7][7] = new pieces.Rook("white", new Position(7, 7));
        for (int i = 0; i < 8; i++) {
            squares[6][i] = new pieces.Pawn("white", new Position(6, i));
        }
    }

    public Piece getPiece(Position position) {
        return squares[position.getRow()][position.getColumn()];
    }

    public boolean movePiece(Position from, Position to) {
        Piece piece = getPiece(from);
    
        if (piece == null) {
            System.out.println("There is no piece on the source square.");
            return false;
        }

        if (!piece.canMove(this, to)) {
            System.out.println("Invalid move for " + piece.getClass().getSimpleName() + ".");
            return false;
        }

        Piece targetPiece = getPiece(to);
        
        if (targetPiece != null) {
            if (piece.getColor().equals(targetPiece.getColor())) {
                System.out.println("Invalid move: You cannot move to a square occupied by your own piece.");
                return false;
            } else {
                System.out.println("Capturing the opponent's piece on " + to);
            }
        }

        squares[to.getRow()][to.getColumn()] = piece;
        squares[from.getRow()][from.getColumn()] = null;
        piece.setPosition(to);
        
        return true;
    }
    public void placePiece(Piece piece, Position pos) {
        squares[pos.getRow()][pos.getColumn()] = piece;
        piece.setPosition(pos);
}
    // Helper method to check if a path is clear (no pieces in the way)
    public boolean isPathClear(Position from, Position to) {
        int rowDiff = to.getRow() - from.getRow();
        int colDiff = to.getColumn() - from.getColumn();

        int rowStep = Integer.signum(rowDiff);  // Step for row direction (-1, 0, or 1)
        int colStep = Integer.signum(colDiff);  // Step for column direction (-1, 0, or 1)

        int currentRow = from.getRow() + rowStep;
        int currentCol = from.getColumn() + colStep;

        while (currentRow != to.getRow() || currentCol != to.getColumn()) {
            if (getPiece(new Position(currentRow, currentCol)) != null) {
                return false;  // Path is blocked
            }
            currentRow += rowStep;
            currentCol += colStep;
        }

        return true;  // Path is clear
    }

    // Display the chessboard
    public void display() {
        String horizontalLine = "   +-----+-----+-----+-----+-----+-----+-----+-----+";

        System.out.println();
        for (int row = 0; row < 8; row++) {
            int displayRow = 8 - row;
            System.out.println(horizontalLine);
            System.out.print(" " + displayRow + " |");
            
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                String square = (piece != null) ? String.format(" %-3s ", piece.toString()) : "     ";
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

