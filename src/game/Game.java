package game;

import board.Board;
import utils.Position;
import pieces.Piece;
import java.util.Scanner;

/**
 * Class to manage the game flow.
 */
public class Game {
    private Board board;
    private String currentPlayer;

    public Game() {
        board = new Board();
        currentPlayer = "white";
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            board.display();
            
            // Check for checkmate before starting the turn
            if (board.isCheckmate(currentPlayer)) {
                System.out.println("Checkmate! " + (currentPlayer.equals("white") ? "Black" : "White") + " wins!");
                break;
            }
            
            // Inform if the current player is in check
            if (board.isKingInCheck(currentPlayer)) {
                System.out.println("WARNING: " + currentPlayer + " king is in CHECK!");
            }
            
            System.out.println(currentPlayer + "'s turn.");
            System.out.print("Enter your move (e.g., 'e2 e4'): ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) break;

            String[] tokens = input.split(" ");
            if (tokens.length != 2) {
                System.out.println("Invalid input. Please enter move in 'from to' format.");
                continue;
            }

            Position from = Position.fromAlgebraic(tokens[0]);
            Position to = Position.fromAlgebraic(tokens[1]);
            if (from == null || to == null) {
                System.out.println("Invalid position.");
                continue;
            }

            Piece piece = board.getPiece(from);
            if (piece == null || !piece.getColor().equals(currentPlayer)) {
                System.out.println("Invalid move: Not your piece or no piece at source.");
                continue;
            }

            // Validate the move
            if (piece.canMove(board, to)) {
                // Check if the move would expose the king to check
                if (board.wouldMoveExposeCheck(from, to)) {
                    System.out.println("Invalid move: Cannot move into check!");
                    continue;
                }

                // Perform the move
                if (board.movePiece(from, to)) {
                    // Switch players
                    currentPlayer = currentPlayer.equals("white") ? "black" : "white";
                } else {
                    System.out.println("Move failed.");
                }
            } else {
                System.out.println("Invalid move for this piece.");
            }
        }
        scanner.close();
    }
}
