package main;

import gui.ChessGUI;
import game.Game;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Check if GUI mode is explicitly requested or no arguments provided
        if (args.length == 0 || (args.length > 0 && args[0].equalsIgnoreCase("gui"))) {
            // Launch GUI mode
            SwingUtilities.invokeLater(() -> {
                ChessGUI gui = new ChessGUI();
                gui.setVisible(true);
            });
        } else if (args.length > 0 && args[0].equalsIgnoreCase("console")) {
            // Launch console mode
            Game consoleGame = new Game();
            consoleGame.start();
        } else {
            // Default to console mode if unknown argument
            System.out.println("Usage: java Main [gui|console]");
            System.out.println("Defaulting to console mode.");
            Game consoleGame = new Game();
            consoleGame.start();
        }
    }
}
