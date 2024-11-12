package gui;

import board.Board;
import pieces.Piece;
import utils.Position;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import java.io.File;

public class ChessGUI extends JFrame {
    private final int BOARD_SIZE = 8;
    private final int SQUARE_SIZE = 60;
    private final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private final Color DARK_SQUARE = new Color(181, 136, 99);
    
    private Board board;
    private JPanel boardPanel;
    private Position selectedPosition;
    private JLabel[][] squareLabels;
    private Map<String, ImageIcon> pieceImages;
    private String currentPlayer;  // Track current player's turn
    private JLabel statusLabel;    // Display current player's turn
    
    public ChessGUI() {
        board = new Board();
        pieceImages = new HashMap<>();
        currentPlayer = "white";  // White moves first
        loadPieceImages();
        initializeGUI();
        updateBoardDisplay();
    }
    
    private void loadPieceImages() {
        try {
            String[] pieces = {"pawn", "rook", "knight", "bishop", "queen", "king"};
            String[] colors = {"white", "black"};
            
            for (String color : colors) {
                for (String piece : pieces) {
                    String filename = color + "_" + piece + ".png";
                    String resourcePath = "resources/pieces/" + filename;
                    
                    File imageFile = new File(resourcePath);
                    if (imageFile.exists()) {
                        Image image = ImageIO.read(imageFile);
                        pieceImages.put(color + "_" + piece, new ImageIcon(image));
                    } else {
                        System.err.println("Missing image file: " + resourcePath);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading piece images: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeGUI() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Create status panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("White's turn", SwingConstants.CENTER);
        statusLabel.setPreferredSize(new Dimension(480, 30));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(statusLabel, BorderLayout.NORTH);
        
        // Create board panel
        boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        squareLabels = new JLabel[BOARD_SIZE][BOARD_SIZE];
        
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                JLabel square = new JLabel("", SwingConstants.CENTER);
                square.setPreferredSize(new Dimension(SQUARE_SIZE, SQUARE_SIZE));
                square.setOpaque(true);
                square.setBackground((row + col) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE);
                
                final int finalRow = row;
                final int finalCol = col;
                square.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleSquareClick(new Position(finalRow, finalCol));
                    }
                });
                
                squareLabels[row][col] = square;
                boardPanel.add(square);
            }
        }
        
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }
    
    private void updateBoardDisplay() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Position pos = new Position(row, col);
                Piece piece = board.getPiece(pos);
                JLabel square = squareLabels[row][col];
                
                if (piece != null) {
                    String color = piece.getColor();
                    String pieceName = piece.getClass().getSimpleName().toLowerCase();
                    ImageIcon icon = pieceImages.get(color + "_" + pieceName);
                    square.setIcon(icon);
                } else {
                    square.setIcon(null);
                }
            }
        }
    }
    
    private void handleSquareClick(Position clickedPosition) {
        if (selectedPosition == null) {
            // First click - select piece
            Piece piece = board.getPiece(clickedPosition);
            if (piece != null && piece.getColor().equals(currentPlayer)) {
                selectedPosition = clickedPosition;
                highlightSquare(clickedPosition);
            } else if (piece != null && !piece.getColor().equals(currentPlayer)) {
                // Show message if wrong player tries to move
                JOptionPane.showMessageDialog(this, 
                    "It's " + currentPlayer + "'s turn!", 
                    "Wrong Turn", 
                    JOptionPane.WARNING_MESSAGE);
            }
        } else {
            // Second click - move piece
            boolean moveSuccessful = board.movePiece(selectedPosition, clickedPosition);
            
            if (moveSuccessful) {
                // Check for captured king
                Piece capturedPiece = board.getPiece(clickedPosition);
                if (capturedPiece != null && capturedPiece.getClass().getSimpleName().equals("King")) {
                    showGameOverDialog(currentPlayer);
                }
                
                // Switch turns
                currentPlayer = currentPlayer.equals("white") ? "black" : "white";
                statusLabel.setText(currentPlayer.substring(0, 1).toUpperCase() + 
                                 currentPlayer.substring(1) + "'s turn");
            }
            
            unhighlightSquare(selectedPosition);
            selectedPosition = null;
            updateBoardDisplay();
        }
    }
    
    private void highlightSquare(Position pos) {
        squareLabels[pos.getRow()][pos.getColumn()].setBorder(
            BorderFactory.createLineBorder(Color.YELLOW, 2));
    }
    
    private void unhighlightSquare(Position pos) {
        squareLabels[pos.getRow()][pos.getColumn()].setBorder(null);
    }
    
    private void showGameOverDialog(String winner) {
        String message = winner.substring(0, 1).toUpperCase() + winner.substring(1) + " wins the game!";
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessGUI gui = new ChessGUI();
            gui.setVisible(true);
        });
    }
}
