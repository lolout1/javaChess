package main;

import gui.ChessGUI;
import game.Game;
import javax.swing.SwingUtilities;

/**
 * Main class that serves as the entry point for the Chess application.
 * Supports both GUI and console-based gameplay modes.
 * 
 * @author Abheek Pradhan
 */
public class Main {
    /**
     * Main method that initializes the chess game based on command line arguments.
     * If no arguments are provided or "gui" is specified, launches in GUI mode.
     * If "console" is specified, launches in console mode.
     * 
     * @param args Command line arguments to determine game mode
     * @author Abheek Pradhan
     */
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
