package game;

import board.Board;
import utils.Position;
import pieces.Piece;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

/**
 * Console-based chess game implementation.
 * Handles game flow, move validation, and player interaction.
 * Includes comprehensive chess rules enforcement and game state management.
 * Supports save/load functionality and maintains move history.
 * 
 * @author Abheek Pradhan
 */
public class Game {
    private Board board;
    private String currentPlayer;
    private boolean gameOver;
    private List<String> moveHistory;
    private boolean isInCheck;
    private static final String WHITE = "white";
    private static final String BLACK = "black";

    public Game() {
        initializeGame();
    }

    private void initializeGame() {
        board = new Board();
        currentPlayer = WHITE;
        gameOver = false;
        moveHistory = new ArrayList<>();
        isInCheck = false;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        displayWelcomeMessage();

        while (!gameOver) {
            try {
                displayGameState();
                String input = getPlayerInput(scanner);
                
                if (input.equalsIgnoreCase("exit")) {
                    handleGameExit();
                    break;
                }

                if (input.equalsIgnoreCase("save")) {
                    saveGame();
                    continue;
                }

                if (input.equalsIgnoreCase("load")) {
                    loadGame();
                    continue;
                }

                if (input.equalsIgnoreCase("undo")) {
                    undoLastMove();
                    continue;
                }

                if (processMove(input)) {
                    updateGameState();
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Please try again.");
            }
        }
        scanner.close();
    }

    private void displayWelcomeMessage() {
        System.out.println("Welcome to Chess!");
        System.out.println("Commands:");
        System.out.println(" - Move: e2 e4");
        System.out.println(" - Save game: save");
        System.out.println(" - Load game: load");
        System.out.println(" - Undo move: undo");
        System.out.println(" - Exit game: exit");
        System.out.println();
    }

    private void displayGameState() {
        board.display();
        
        // Show check warning if applicable
        if (isInCheck) {
            System.out.println("\nWARNING: " + currentPlayer.toUpperCase() + " is in CHECK!");
        }

        // Display current player's turn
        System.out.println("\n" + currentPlayer + "'s turn.");
        
        // Display move history
        if (!moveHistory.isEmpty()) {
            System.out.println("\nMove history:");
            for (int i = 0; i < moveHistory.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, moveHistory.get(i));
            }
        }
    }

    private String getPlayerInput(Scanner scanner) {
        System.out.print("\nEnter move (e.g., 'e2 e4') or command: ");
        return scanner.nextLine().trim();
    }

    private boolean processMove(String input) {
        String[] tokens = input.split(" ");
        if (tokens.length != 2) {
            System.out.println("Invalid input. Use format 'e2 e4'");
            return false;
        }

        Position from = Position.fromAlgebraic(tokens[0]);
        Position to = Position.fromAlgebraic(tokens[1]);
        
        if (from == null || to == null) {
            System.out.println("Invalid position format.");
            return false;
        }

        // Validate piece selection
        Piece piece = board.getPiece(from);
        if (piece == null) {
            System.out.println("No piece at source position.");
            return false;
        }
        
        if (!piece.getColor().equals(currentPlayer)) {
            System.out.println("That's not your piece!");
            return false;
        }

        // Check if move is legal and doesn't leave king in check
        if (!isMoveLegal(from, to)) {
            return false;
        }

        // Execute the move
        Piece capturedPiece = board.getPiece(to);
        boolean moveSuccessful = board.movePiece(from, to);
        
        if (moveSuccessful) {
            // Record the move
            String moveNotation = createMoveNotation(piece, from, to, capturedPiece);
            moveHistory.add(moveNotation);
            
            // Check opponent's king status
            String opponent = currentPlayer.equals(WHITE) ? BLACK : WHITE;
            if (board.isCheckmate(opponent)) {
                handleCheckmate();
            } else if (board.isInCheck(opponent)) {
                System.out.println("CHECK!");
                isInCheck = true;
            } else {
                isInCheck = false;
            }
        }

        return moveSuccessful;
    }

    private boolean isMoveLegal(Position from, Position to) {
        Piece piece = board.getPiece(from);
        
        // Basic move validation
        if (!piece.canMove(board, to)) {
            System.out.println("Invalid move for " + piece.getClass().getSimpleName());
            return false;
        }

        // Check if move would leave/put own king in check
        Board tempBoard = board.copyBoard();
        tempBoard.movePiece(from, to);
        if (tempBoard.isInCheck(currentPlayer)) {
            System.out.println("Invalid move: Would leave/put your king in check!");
            return false;
        }

        return true;
    }

    private void updateGameState() {
        currentPlayer = currentPlayer.equals(WHITE) ? BLACK : WHITE;
    }

    private void handleCheckmate() {
        gameOver = true;
        board.display();
        System.out.println("\nCHECKMATE!");
        System.out.println(currentPlayer + " wins the game!");
        displayFinalScore();
    }

    private void handleGameExit() {
        System.out.println("\nGame ended.");
        displayFinalScore();
    }

    private void displayFinalScore() {
        System.out.println("\nFinal move count: " + moveHistory.size());
        System.out.println("Thank you for playing!");
    }

    private String createMoveNotation(Piece piece, Position from, Position to, Piece captured) {
        StringBuilder notation = new StringBuilder();
        notation.append(piece.getClass().getSimpleName())
               .append(": ")
               .append(from.toString())
               .append(" → ")
               .append(to.toString());
        
        if (captured != null) {
            notation.append(" (captures ")
                   .append(captured.getClass().getSimpleName())
                   .append(")");
        }
        
        if (board.isInCheck(currentPlayer.equals(WHITE) ? BLACK : WHITE)) {
            notation.append(" +");
        }
        
        return notation.toString();
    }

    private void saveGame() {
        try {
            System.out.print("Enter filename to save: ");
            String filename = new Scanner(System.in).nextLine().trim();
            if (!filename.endsWith(".txt")) {
                filename += ".txt";
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
                writer.println("CurrentPlayer: " + currentPlayer);
                writer.println("IsInCheck: " + isInCheck);
                
                // Save board state
                writer.println("Board:");
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        Piece piece = board.getPiece(new Position(row, col));
                        if (piece != null) {
                            writer.printf("%d,%d,%s,%s%n", row, col, 
                                piece.getColor(), piece.getClass().getSimpleName());
                        }
                    }
                }
                
                // Save move history
                writer.println("MoveHistory:");
                for (String move : moveHistory) {
                    writer.println(move);
                }
            }
            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving game: " + e.getMessage());
        }
    }

    private void loadGame() {
        try {
            System.out.print("Enter filename to load: ");
            String filename = new Scanner(System.in).nextLine().trim();
            if (!filename.endsWith(".txt")) {
                filename += ".txt";
            }

            board = new Board(); // Start with fresh board
            moveHistory.clear();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                boolean readingBoard = false;
                boolean readingMoves = false;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("CurrentPlayer:")) {
                        currentPlayer = line.split(": ")[1];
                    } else if (line.startsWith("IsInCheck:")) {
                        isInCheck = Boolean.parseBoolean(line.split(": ")[1]);
                    } else if (line.equals("Board:")) {
                        readingBoard = true;
                        continue;
                    } else if (line.equals("MoveHistory:")) {
                        readingBoard = false;
                        readingMoves = true;
                        continue;
                    }

                    if (readingBoard && line.contains(",")) {
                        String[] parts = line.split(",");
                        Position pos = new Position(
                            Integer.parseInt(parts[0]),
                            Integer.parseInt(parts[1])
                        );
                        createAndPlacePiece(parts[2], parts[3], pos);
                    } else if (readingMoves) {
                        moveHistory.add(line);
                    }
                }
            }
            System.out.println("Game loaded successfully!");
        } catch (IOException e) {
            System.out.println("Error loading game: " + e.getMessage());
        }
    }

    private void createAndPlacePiece(String color, String pieceType, Position pos) {
        Piece piece = null;
        switch (pieceType) {
            case "King": piece = new pieces.King(color, pos); break;
            case "Queen": piece = new pieces.Queen(color, pos); break;
            case "Rook": piece = new pieces.Rook(color, pos); break;
            case "Bishop": piece = new pieces.Bishop(color, pos); break;
            case "Knight": piece = new pieces.Knight(color, pos); break;
            case "Pawn": piece = new pieces.Pawn(color, pos); break;
        }
        if (piece != null) {
            board.placePiece(piece, pos);
        }
    }

    private void undoLastMove() {
        if (moveHistory.isEmpty()) {
            System.out.println("No moves to undo!");
            return;
        }

        moveHistory.remove(moveHistory.size() - 1);
        board = new Board();
        currentPlayer = WHITE;
        
        // Replay all moves except the last one
        for (String move : moveHistory) {
            String[] parts = move.split(" → ");
            Position from = Position.fromAlgebraic(parts[0].split(": ")[1]);
            Position to = Position.fromAlgebraic(parts[1].split(" ")[0]);
            board.movePiece(from, to);
            currentPlayer = currentPlayer.equals(WHITE) ? BLACK : WHITE;
        }
        
        System.out.println("Move undone!");
    }
}