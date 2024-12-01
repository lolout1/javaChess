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

    // Comprehensive method to check if the king of a specific color is in check
    public boolean isKingInCheck(String color) {
        // Find the king's position
        Position kingPosition = findKing(color);
        if (kingPosition == null) {
            return false;  // No king found, which is an impossible scenario
        }

        // Check all opponent pieces to see if they can attack the king
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                
                // Check if the piece belongs to the opponent
                if (piece != null && !piece.getColor().equals(color)) {
                    // Check if this piece can move to the king's position
                    if (canPieceAttackPosition(piece, kingPosition)) {
                        return true;  // King is in check
                    }
                }
            }
        }
        return false;
    }

    // Helper method to check if a piece can attack a specific position
    private boolean canPieceAttackPosition(Piece piece, Position targetPosition) {
        // Special handling for pawns, as they attack diagonally differently from moving
        if (piece instanceof Pawn) {
            return canPawnAttackPosition(piece, targetPosition);
        }
        
        // For other pieces, use their standard canMove method
        return piece.canMove(this, targetPosition);
    }

    // Special method to check pawn attack patterns
    private boolean canPawnAttackPosition(Piece pawn, Position targetPosition) {
        int direction = pawn.getColor().equals("white") ? -1 : 1;
        int currentRow = pawn.getPosition().getRow();
        int currentCol = pawn.getPosition().getColumn();

        // Check diagonal attack positions for pawns
        int[] attackCols = {currentCol - 1, currentCol + 1};
        for (int attackCol : attackCols) {
            if (targetPosition.getRow() == currentRow + direction && 
                targetPosition.getColumn() == attackCol) {
                return true;
            }
        }
        return false;
    }

    // Method to find the king's position for a specific color
    private Position findKing(String color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                if (piece != null && 
                    piece instanceof King && 
                    piece.getColor().equals(color)) {
                    return piece.getPosition();
                }
            }
        }
        return null;
    }

    // Method to check if a move would put or leave the king in check
    public boolean wouldMoveExposeCheck(Position from, Position to) {
        // Create a copy of the board to simulate the move
        Board simulatedBoard = copyBoard();
        
        // Perform the move on the simulated board
        Piece movingPiece = simulatedBoard.getPiece(from);
        if (movingPiece == null) {
            return false;  // No piece to move
        }
        
        // Get the color of the moving piece
        String color = movingPiece.getColor();
        
        // Move the piece
        simulatedBoard.movePiece(from, to);
        
        // Check if the king of the same color is now in check
        return simulatedBoard.isKingInCheck(color);
    }

    // Comprehensive checkmate validation
    public boolean isCheckmate(String color) {
        // First, check if the king is in check
        if (!isKingInCheck(color)) {
            return false;
        }

        // Try all possible moves for pieces of the given color
        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                Piece piece = squares[fromRow][fromCol];
                
                // Check only pieces of the specified color
                if (piece != null && piece.getColor().equals(color)) {
                    Position from = new Position(fromRow, fromCol);
                    
                    // Try moving to all possible squares
                    for (int toRow = 0; toRow < 8; toRow++) {
                        for (int toCol = 0; toCol < 8; toCol++) {
                            Position to = new Position(toRow, toCol);
                            
                            // Check if the move is legal and doesn't expose the king to check
                            if (piece.canMove(this, to) && 
                                !wouldMoveExposeCheck(from, to)) {
                                // Found a move that gets out of check
                                return false;
                            }
                        }
                    }
                }
            }
        }
        
        // No moves can get out of check
        return true;
    }

    // Helper method to create a deep copy of the board for move simulation
    public Board copyBoard() {
        Board copy = new Board();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece originalPiece = squares[row][col];
                if (originalPiece != null) {
                    // Create a new piece with the same properties
                    Piece copiedPiece = createPieceCopy(originalPiece);
                    copy.placePiece(copiedPiece, new Position(row, col));
                }
            }
        }
        return copy;
    }

    public Piece createPieceCopy(Piece original) {
        String color = original.getColor();
        Position pos = original.getPosition();
        
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
}
