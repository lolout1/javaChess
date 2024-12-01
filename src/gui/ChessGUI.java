package gui;

import board.Board;
import pieces.Piece;
import utils.Position;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

/**
 * ChessGUI class provides the graphical interface for the chess game.
 * Includes features for:
 * - Visual board representation and piece movement
 * - Move history and captured pieces tracking
 * - Check/Checkmate detection and notification
 * - Game state management (save/load/undo)
 */
public class ChessGUI extends JFrame {
    // Constants
    private static final int BOARD_SIZE = 8;
    private static final String WHITE = "white";
    private static final String BLACK = "black";

    // Board appearance settings
    private int squareSize = 60;
    private Color lightSquareColor = new Color(240, 217, 181);
    private Color darkSquareColor = new Color(181, 136, 99);
    private Color checkHighlightColor = new Color(255, 0, 0, 50);

    // Game state
    private Board board;
    private Position selectedPosition;
    private String currentPlayer;
    private boolean isInCheck;

    // Move history tracking
    private ArrayList<String> moveHistory;
    private ArrayList<Board> boardStates;

    // GUI components
    private JPanel boardPanel;
    private JLabel[][] squareLabels;
    private JLabel statusLabel;
    private JList<String> moveHistoryList;
    private DefaultListModel<String> moveHistoryModel;
    private JPanel capturedPiecesPanel;
    private JPanel whiteCapturedPanel;
    private JPanel blackCapturedPanel;
    private Map<String, ImageIcon> pieceImages;

    public ChessGUI() {
        initializeGame();
        initializeGUI();
        loadPieceImages();
        updateBoardDisplay();
    }

    private void initializeGame() {
        board = new Board();
        currentPlayer = WHITE;
        selectedPosition = null;
        isInCheck = false;
        moveHistory = new ArrayList<>();
        boardStates = new ArrayList<>();
        pieceImages = new HashMap<>();
        saveGameState(); // Save initial state
    }

    private void initializeGUI() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Create main components
        createStatusPanel();
        createBoardPanel();
        createSidePanel();
        setJMenuBar(createMenuBar());

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void createStatusPanel() {
        statusLabel = new JLabel("White's turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(statusLabel, BorderLayout.NORTH);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Game Menu
        JMenu gameMenu = new JMenu("Game");
        addMenuItem(gameMenu, "New Game", e -> newGame());
        addMenuItem(gameMenu, "Save Game", e -> saveGame());
        addMenuItem(gameMenu, "Load Game", e -> loadGame());
        gameMenu.addSeparator();
        addMenuItem(gameMenu, "Exit", e -> System.exit(0));

        // Settings Menu
        JMenu settingsMenu = new JMenu("Settings");
        addMenuItem(settingsMenu, "Board Settings", e -> showSettingsDialog());

        menuBar.add(gameMenu);
        menuBar.add(settingsMenu);
        return menuBar;
    }

    private void addMenuItem(JMenu menu, String title, ActionListener action) {
        JMenuItem item = new JMenuItem(title);
        item.addActionListener(action);
        menu.add(item);
    }

    private void createBoardPanel() {
        boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        squareLabels = new JLabel[BOARD_SIZE][BOARD_SIZE];

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                JLabel square = new JLabel("", SwingConstants.CENTER);
                square.setPreferredSize(new Dimension(squareSize, squareSize));
                square.setOpaque(true);
                setSquareColor(square, row, col);

                final Position pos = new Position(row, col);
                square.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleSquareClick(pos);
                    }
                });

                squareLabels[row][col] = square;
                boardPanel.add(square);
            }
        }
        add(boardPanel, BorderLayout.CENTER);
    }

    private void createSidePanel() {
        JPanel sidePanel = new JPanel(new BorderLayout(5, 5));
        sidePanel.setPreferredSize(new Dimension(200, BOARD_SIZE * squareSize));

        // Move history
        moveHistoryModel = new DefaultListModel<>();
        moveHistoryList = new JList<>(moveHistoryModel);
        moveHistoryList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(moveHistoryList);

        // Captured pieces panels
        capturedPiecesPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        whiteCapturedPanel = createCapturedPanel("White Captures");
        blackCapturedPanel = createCapturedPanel("Black Captures");
        capturedPiecesPanel.add(whiteCapturedPanel);
        capturedPiecesPanel.add(blackCapturedPanel);

        JButton undoButton = new JButton("Undo Move");
        undoButton.addActionListener(e -> undoMove());

        sidePanel.add(new JLabel("Move History", SwingConstants.CENTER), BorderLayout.NORTH);
        sidePanel.add(scrollPane, BorderLayout.CENTER);
        sidePanel.add(capturedPiecesPanel, BorderLayout.SOUTH);

        add(sidePanel, BorderLayout.EAST);
    }

    private JPanel createCapturedPanel(String title) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    /**
     * Loads piece images and scales them to fit the current board size.
     * Images are stored in the pieces map for quick access during board updates.
     */
    private void loadPieceImages() {
        String[] pieces = {"pawn", "rook", "knight", "bishop", "queen", "king"};
        String[] colors = {WHITE, BLACK};

        for (String color : colors) {
            for (String piece : pieces) {
                String filename = color + "_" + piece + ".png";
                try {
                    Image img = ImageIO.read(new File("src/resources/pieces/" + filename));
                    Image scaled = img.getScaledInstance(
                        squareSize - 10, 
                        squareSize - 10, 
                        Image.SCALE_SMOOTH
                    );
                    pieceImages.put(color + "_" + piece, new ImageIcon(scaled));
                } catch (IOException e) {
                    System.err.println("Failed to load image: " + filename);
                }
            }
        }
    }

    /**
     * Handles click events on board squares.
     * Manages piece selection, movement, and turn progression.
     */
    private void handleSquareClick(Position clickedPosition) {
        if (selectedPosition == null) {
            // First click - select piece
            Piece piece = board.getPiece(clickedPosition);
            if (piece != null && piece.getColor().equals(currentPlayer)) {
                selectedPosition = clickedPosition;
                highlightSquare(clickedPosition);
                highlightLegalMoves(piece);
            }
        } else {
            // Second click - attempt to move piece
            Piece sourcePiece = board.getPiece(selectedPosition);
            Piece targetPiece = board.getPiece(clickedPosition);
            
            boolean moveSuccessful = board.movePiece(selectedPosition, clickedPosition);
            
            if (moveSuccessful) {
                // Record move in history
                String moveNotation = createMoveNotation(sourcePiece, selectedPosition, 
                                                       clickedPosition, targetPiece);
                moveHistoryModel.addElement(moveNotation);
                moveHistory.add(moveNotation);

                // Handle captured pieces
                if (targetPiece != null) {
                    addCapturedPiece(targetPiece);
                }

                // Check for checkmate or check
                String nextPlayer = currentPlayer.equals(WHITE) ? BLACK : WHITE;
                if (board.isCheckmate(nextPlayer)) {
                    updateBoardDisplay();
                    showGameOverDialog(currentPlayer, "Checkmate!");
                    return;
                } else if (board.isInCheck(nextPlayer)) {
                    isInCheck = true;
                    showCheckNotification();
                } else {
                    isInCheck = false;
                }

                // Switch turns
                currentPlayer = nextPlayer;
                updateStatusLabel();
                saveGameState();
            }

            // Clean up highlights and selection
            clearHighlights();
            selectedPosition = null;
            updateBoardDisplay();
        }
    }

    /**
     * Creates a descriptive notation for a move, including piece type,
     * source and destination squares, and capture information.
     */
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

    /**
     * Highlights legal moves for the selected piece.
     * Shows possible destinations and captures.
     */
    private void highlightLegalMoves(Piece piece) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Position pos = new Position(row, col);
                if (piece.canMove(board, pos) && 
                    !board.wouldMoveExposeCheck(piece.getPosition(), pos)) {
                    
                    Piece targetPiece = board.getPiece(pos);
                    if (targetPiece != null && !targetPiece.getColor().equals(piece.getColor())) {
                        // Highlight capture squares differently
                        highlightCaptureSquare(pos);
                    } else if (targetPiece == null) {
                        // Highlight empty squares
                        highlightLegalSquare(pos);
                    }
                }
            }
        }
    }

    /**
     * Saves the current game state for undo functionality and game restoration.
     */
    private void saveGameState() {
        boardStates.add(board.copyBoard());
        if (boardStates.size() > 50) {  // Limit history size
            boardStates.remove(0);
        }
    }

    /**
     * Implements the undo move functionality.
     * Restores the previous board state and updates the display.
     */
    private void undoMove() {
        if (boardStates.size() > 1) {  // Keep initial state
            boardStates.remove(boardStates.size() - 1);
            board = boardStates.get(boardStates.size() - 1).copyBoard();
            
            if (!moveHistory.isEmpty()) {
                moveHistory.remove(moveHistory.size() - 1);
                moveHistoryModel.remove(moveHistoryModel.size() - 1);
            }
            
            currentPlayer = currentPlayer.equals(WHITE) ? BLACK : WHITE;
            isInCheck = board.isInCheck(currentPlayer);
            
            updateBoardDisplay();
            updateStatusLabel();
            updateCapturedPieces();
        }
    }

    /**
     * Shows the game over dialog with the result and options to continue.
     */
    private void showGameOverDialog(String winner, String reason) {
        String message = String.format("%s wins by %s!", 
            winner.substring(0, 1).toUpperCase() + winner.substring(1), reason);
            
        Object[] options = {"New Game", "Save Game", "Exit"};
        int choice = JOptionPane.showOptionDialog(
            this,
            message,
            "Game Over",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            new ImageIcon(pieceImages.get(winner + "_king").getImage()),
            options,
            options[0]
        );
        
        switch (choice) {
            case 0: newGame(); break;
            case 1: saveGame(); break;
            case 2: System.exit(0); break;
        }
    }

    /**
     * Saves the current game state to a file.
     * Includes board position, move history, and game metadata.
     */
    private void saveGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(
            new javax.swing.filechooser.FileNameExtensionFilter("Chess Game (.txt)", "txt")
        );
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".txt")) {
                    file = new File(file.getPath() + ".txt");
                }
                
                try (PrintWriter writer = new PrintWriter(file)) {
                    // Save game metadata
                    writer.println("CurrentPlayer: " + currentPlayer);
                    writer.println("IsInCheck: " + isInCheck);
                    
                    // Save board state
                    writer.println("Board:");
                    for (int row = 0; row < BOARD_SIZE; row++) {
                        for (int col = 0; col < BOARD_SIZE; col++) {
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
                
                JOptionPane.showMessageDialog(this, "Game saved successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving game: " + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // Continue with load game and utility methods...
/**
     * Loads a saved game from a text file.
     * Reconstructs the entire game state including board position,
     * move history, and current player.
     */
    private void loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(
            new javax.swing.filechooser.FileNameExtensionFilter("Chess Game (.txt)", "txt")
        );

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(
                    new FileReader(fileChooser.getSelectedFile()))) {
                
                // Reset current game state
                board = new Board();
                moveHistory.clear();
                moveHistoryModel.clear();
                whiteCapturedPanel.removeAll();
                blackCapturedPanel.removeAll();
                
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
                        readingMoves = false;
                        continue;
                    } else if (line.equals("MoveHistory:")) {
                        readingBoard = false;
                        readingMoves = true;
                        continue;
                    }

                    if (readingBoard) {
                        String[] parts = line.split(",");
                        if (parts.length == 4) {
                            Position pos = new Position(
                                Integer.parseInt(parts[0]),
                                Integer.parseInt(parts[1])
                            );
                            createAndPlacePiece(parts[2], parts[3], pos);
                        }
                    } else if (readingMoves) {
                        moveHistory.add(line);
                        moveHistoryModel.addElement(line);
                    }
                }

                updateBoardDisplay();
                updateStatusLabel();
                revalidate();
                repaint();

                JOptionPane.showMessageDialog(this, "Game loaded successfully!");
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Error loading game: " + e.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /**
     * Shows the settings dialog for board customization.
     * Allows changing board colors and piece size.
     */
    private void showSettingsDialog() {
        JDialog dialog = new JDialog(this, "Board Settings", true);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel settingsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Board size settings
        String[] sizes = {"Small (40px)", "Medium (60px)", "Large (80px)"};
        JComboBox<String> sizeCombo = new JComboBox<>(sizes);
        sizeCombo.setSelectedItem(squareSize == 40 ? sizes[0] : 
                                 squareSize == 60 ? sizes[1] : sizes[2]);
        settingsPanel.add(new JLabel("Board Size:"));
        settingsPanel.add(sizeCombo);

        // Color settings
        JButton lightSquareButton = createColorButton("Light Squares", lightSquareColor, 
            c -> lightSquareColor = c);
        JButton darkSquareButton = createColorButton("Dark Squares", darkSquareColor,
            c -> darkSquareColor = c);

        settingsPanel.add(new JLabel("Light Square Color:"));
        settingsPanel.add(lightSquareButton);
        settingsPanel.add(new JLabel("Dark Square Color:"));
        settingsPanel.add(darkSquareButton);

        // Apply button
        JButton applyButton = new JButton("Apply Changes");
        applyButton.addActionListener(e -> {
            String sizeStr = (String) sizeCombo.getSelectedItem();
            squareSize = sizeStr.contains("40") ? 40 : 
                        sizeStr.contains("60") ? 60 : 80;
            
            loadPieceImages();  // Reload images for new size
            recreateBoard();
            dialog.dispose();
        });

        dialog.add(settingsPanel, BorderLayout.CENTER);
        dialog.add(applyButton, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Creates a color chooser button for the settings dialog.
     */
    private JButton createColorButton(String label, Color initialColor, 
            java.util.function.Consumer<Color> onColorChosen) {
        JButton button = new JButton("Choose Color");
        button.setBackground(initialColor);
        button.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(
                this, "Choose " + label, initialColor);
            if (newColor != null) {
                button.setBackground(newColor);
                onColorChosen.accept(newColor);
            }
        });
        return button;
    }

    /**
     * Recreates the board with current settings.
     * Used after applying new board settings.
     */
    private void recreateBoard() {
        remove(boardPanel);
        createBoardPanel();
        updateBoardDisplay();
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Creates and places a piece on the board during game loading.
     */
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

    /**
     * Updates the status label with current game state.
     */
    private void updateStatusLabel() {
        String status = currentPlayer.substring(0, 1).toUpperCase() + 
                       currentPlayer.substring(1) + "'s turn";
        if (isInCheck) {
            status += " (In Check!)";
            statusLabel.setForeground(Color.RED);
        } else {
            statusLabel.setForeground(Color.BLACK);
        }
        statusLabel.setText(status);
    }

    /**
     * Updates the visual display of captured pieces.
     */
    private void updateCapturedPieces() {
        whiteCapturedPanel.removeAll();
        blackCapturedPanel.removeAll();
        for (String move : moveHistory) {
            if (move.contains("captures")) {
                String[] parts = move.split("captures");
                String pieceInfo = parts[1].trim();
                pieceInfo = pieceInfo.substring(0, pieceInfo.length() - 1); // Remove trailing parenthesis
                addCapturedPieceToPanel(pieceInfo);
            }
        }
        capturedPiecesPanel.revalidate();
        capturedPiecesPanel.repaint();
    }

    /**
     * Shows a notification when a king is in check.
     */
    private void showCheckNotification() {
        JOptionPane.showMessageDialog(this,
            currentPlayer.substring(0, 1).toUpperCase() + 
            currentPlayer.substring(1) + "'s king is in check!",
            "Check!",
            JOptionPane.WARNING_MESSAGE
        );
    }
private void updateBoardDisplay() {
    for (int row = 0; row < BOARD_SIZE; row++) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            Position pos = new Position(row, col);
            Piece piece = board.getPiece(pos);
            JLabel square = squareLabels[row][col];
            
            // Update square color
            setSquareColor(square, row, col);
            
            // Update piece image
            if (piece != null) {
                String pieceKey = piece.getColor() + "_" + 
                                piece.getClass().getSimpleName().toLowerCase();
                square.setIcon(pieceImages.get(pieceKey));
            } else {
                square.setIcon(null);
            }
        }
    }
}

private void setSquareColor(JLabel square, int row, int col) {
    square.setBackground((row + col) % 2 == 0 ? lightSquareColor : darkSquareColor);
}

private void newGame() {
    board = new Board();
    currentPlayer = "white";
    selectedPosition = null;
    moveHistory.clear();
    moveHistoryModel.clear();
    whiteCapturedPanel.removeAll();
    blackCapturedPanel.removeAll();
    isInCheck = false;
    updateBoardDisplay();
    updateStatusLabel();
}

private void highlightSquare(Position pos) {
    squareLabels[pos.getRow()][pos.getColumn()].setBorder(
        BorderFactory.createLineBorder(Color.YELLOW, 2)
    );
}

private void highlightCaptureSquare(Position pos) {
    squareLabels[pos.getRow()][pos.getColumn()].setBorder(
        BorderFactory.createLineBorder(Color.RED, 2)
    );
}

private void highlightLegalSquare(Position pos) {
    squareLabels[pos.getRow()][pos.getColumn()].setBorder(
        BorderFactory.createLineBorder(Color.GREEN, 2)
    );
}

private void clearHighlights() {
    for (int row = 0; row < BOARD_SIZE; row++) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            squareLabels[row][col].setBorder(null);
        }
    }
}

private void addCapturedPiece(Piece piece) {
    String pieceKey = piece.getColor() + "_" + 
                     piece.getClass().getSimpleName().toLowerCase();
    ImageIcon icon = pieceImages.get(pieceKey);
    
    JLabel pieceLabel = new JLabel(icon);
    pieceLabel.setPreferredSize(new Dimension(squareSize/2, squareSize/2));
    
    if (piece.getColor().equals("white")) {
        blackCapturedPanel.add(pieceLabel);
        blackCapturedPanel.revalidate();
        blackCapturedPanel.repaint();
    } else {
        whiteCapturedPanel.add(pieceLabel);
        whiteCapturedPanel.revalidate();
        whiteCapturedPanel.repaint();
    }
}

private void addCapturedPieceToPanel(String pieceInfo) {
    String[] parts = pieceInfo.split(" ");
    String pieceType = parts[0];
    String color = parts[1];
    
    String pieceKey = color + "_" + pieceType.toLowerCase();
    ImageIcon icon = pieceImages.get(pieceKey);
    
    JLabel pieceLabel = new JLabel(icon);
    pieceLabel.setPreferredSize(new Dimension(squareSize/2, squareSize/2));
    
    if (color.equals("white")) {
        blackCapturedPanel.add(pieceLabel);
    } else {
        whiteCapturedPanel.add(pieceLabel);
    }
}
    /**
     * Entry point for starting the chess game GUI.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessGUI gui = new ChessGUI();
            gui.setVisible(true);
        });
    }
}