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
            System.out.println(currentPlayer + "'s turn.");
            System.out.print("Enter your move: ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) break;

            String[] tokens = input.split(" ");
            if (tokens.length != 2) {
                System.out.println("Invalid input.");
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
                System.out.println("Invalid move.");
                continue;
            }

            if (board.movePiece(from, to)) {
                currentPlayer = currentPlayer.equals("white") ? "black" : "white";
            } else {
                System.out.println("Move failed.");
            }
        }
        scanner.close();
    }
}

