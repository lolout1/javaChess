package gui;

import board.Board;
import pieces.Piece;
import utils.Position;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.List;
/**
 * ChessGUI class implements the graphical user interface for the chess game.
 * Features include:
 * - Turn-based gameplay with white moving first
 * - Visual piece movement and capture
 * - Game history tracking
 * - Save/Load functionality
 * - Board customization settings
 */
public class ChessGUI extends JFrame {
    private final int BOARD_SIZE = 8;
    private int SQUARE_SIZE = 60;
    private Color LIGHT_SQUARE = new Color(240, 217, 181);
    private Color DARK_SQUARE = new Color(181, 136, 99);
    
    private Board board;
    private JPanel boardPanel;
    private Position selectedPosition;
    private JLabel[][] squareLabels;
    private Map<String, ImageIcon> pieceImages;
    private String currentPlayer;
    private JLabel statusLabel;
    
    private DefaultListModel<String> moveHistoryModel;
    private JList<String> moveHistoryList;
    private JPanel capturedPiecesPanel;
    private JPanel whiteCapturedPanel;
    private JPanel blackCapturedPanel;
    private ArrayList<String> moveHistory;

    public ChessGUI() {
        board = new Board();
        pieceImages = new HashMap<>();
        currentPlayer = "white";
        moveHistory = new ArrayList<>();
        moveHistoryModel = new DefaultListModel<>();
        
        initializeGUI();
        createMenuBar();
        loadPieceImages();
        updateBoardDisplay();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Game Menu
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGame = new JMenuItem("New Game");
        JMenuItem saveGame = new JMenuItem("Save Game");
        JMenuItem loadGame = new JMenuItem("Load Game");
        JMenuItem exit = new JMenuItem("Exit");
        
        newGame.addActionListener(e -> newGame());
        saveGame.addActionListener(e -> saveGame());
        loadGame.addActionListener(e -> loadGame());
        exit.addActionListener(e -> System.exit(0));
        
        gameMenu.add(newGame);
        gameMenu.add(saveGame);
        gameMenu.add(loadGame);
        gameMenu.addSeparator();
        gameMenu.add(exit);
        
        // Settings Menu
        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem boardSettings = new JMenuItem("Board Settings");
        boardSettings.addActionListener(e -> showSettingsDialog());
        settingsMenu.add(boardSettings);
        
        menuBar.add(gameMenu);
        menuBar.add(settingsMenu);
        setJMenuBar(menuBar);
    }

    private void showSettingsDialog() {
        JDialog settingsDialog = new JDialog(this, "Board Settings", true);
        settingsDialog.setLayout(new BorderLayout());
        
        JPanel settingsPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        // Board Size
        JLabel sizeLabel = new JLabel("Board Size:");
        String[] sizes = {"Small (40px)", "Medium (60px)", "Large (80px)"};
        JComboBox<String> sizeCombo = new JComboBox<>(sizes);
        sizeCombo.setSelectedItem("Medium (60px)");
        
        // Colors
        JLabel lightColorLabel = new JLabel("Light Squares:");
        JButton lightColorButton = new JButton("Choose Color");
        lightColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(
                settingsDialog, "Choose Light Square Color", LIGHT_SQUARE);
            if (newColor != null) {
                LIGHT_SQUARE = newColor;
            }
        });
        
        JLabel darkColorLabel = new JLabel("Dark Squares:");
        JButton darkColorButton = new JButton("Choose Color");
        darkColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(
                settingsDialog, "Choose Dark Square Color", DARK_SQUARE);
            if (newColor != null) {
                DARK_SQUARE = newColor;
            }
        });
        
        settingsPanel.add(sizeLabel);
        settingsPanel.add(sizeCombo);
        settingsPanel.add(lightColorLabel);
        settingsPanel.add(lightColorButton);
        settingsPanel.add(darkColorLabel);
        settingsPanel.add(darkColorButton);
        
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> {
            String sizeStr = (String) sizeCombo.getSelectedItem();
            if (sizeStr.contains("40")) SQUARE_SIZE = 40;
            else if (sizeStr.contains("60")) SQUARE_SIZE = 60;
            else SQUARE_SIZE = 80;
            
            recreateBoard();
            settingsDialog.dispose();
        });
        
        settingsDialog.add(settingsPanel, BorderLayout.CENTER);
        settingsDialog.add(applyButton, BorderLayout.SOUTH);
        settingsDialog.pack();
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.setVisible(true);
    }
    private void initializeGUI() {
       setTitle("Chess Game");
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       setResizable(false);

       JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

       // Status Panel
       statusLabel = new JLabel("White's turn", SwingConstants.CENTER);
       statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
       mainPanel.add(statusLabel, BorderLayout.NORTH);

       // Board Panel
       boardPanel = createBoardPanel();
       mainPanel.add(boardPanel, BorderLayout.CENTER);

       // History Panel
       JPanel historyPanel = createHistoryPanel();
       mainPanel.add(historyPanel, BorderLayout.EAST);

       add(mainPanel);
       pack();
       setLocationRelativeTo(null);
   }

   private JPanel createBoardPanel() {
       JPanel panel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
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
               panel.add(square);
           }
       }
       return panel;
   }

   private JPanel createHistoryPanel() {
       JPanel panel = new JPanel(new BorderLayout(5, 5));
       panel.setPreferredSize(new Dimension(200, SQUARE_SIZE * 8));

       moveHistoryList = new JList<>(moveHistoryModel);
       moveHistoryList.setFont(new Font("Monospaced", Font.PLAIN, 12));
       JScrollPane scrollPane = new JScrollPane(moveHistoryList);

       capturedPiecesPanel = new JPanel(new GridLayout(2, 1, 5, 5));
       whiteCapturedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
       blackCapturedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
       whiteCapturedPanel.setBorder(BorderFactory.createTitledBorder("White Captures"));
       blackCapturedPanel.setBorder(BorderFactory.createTitledBorder("Black Captures"));
       capturedPiecesPanel.add(whiteCapturedPanel);
       capturedPiecesPanel.add(blackCapturedPanel);

       JButton undoButton = new JButton("Undo Move");
       undoButton.addActionListener(e -> undoMove());

       panel.add(new JLabel("Move History", SwingConstants.CENTER), BorderLayout.NORTH);
       panel.add(scrollPane, BorderLayout.CENTER);
       panel.add(capturedPiecesPanel, BorderLayout.SOUTH);
       panel.add(undoButton, BorderLayout.SOUTH);

       return panel;
   }

   private void loadPieceImages() {
       try {
           String[] pieces = {"pawn", "rook", "knight", "bishop", "queen", "king"};
           String[] colors = {"white", "black"};

           for (String color : colors) {
               for (String piece : pieces) {
                   String filename = color + "_" + piece + ".png";
                   File file = new File("resources/pieces/" + filename);

                   if (file.exists()) {
                       Image image = ImageIO.read(file);
                       Image scaled = image.getScaledInstance(
                           SQUARE_SIZE - 10, SQUARE_SIZE - 10,
                           Image.SCALE_SMOOTH
                       );
                       pieceImages.put(color + "_" + piece, new ImageIcon(scaled));
                   }
               }
           }
       } catch (IOException e) {
           System.err.println("Error loading piece images: " + e.getMessage());
       }
   }

   private void handleSquareClick(Position clickedPosition) {
       if (selectedPosition == null) {
           Piece piece = board.getPiece(clickedPosition);
           if (piece != null && piece.getColor().equals(currentPlayer)) {
               selectedPosition = clickedPosition;
               highlightSquare(clickedPosition);
           } else if (piece != null && !piece.getColor().equals(currentPlayer)) {
               JOptionPane.showMessageDialog(this,
                   "It's " + currentPlayer + "'s turn!",
                   "Wrong Turn",
                   JOptionPane.WARNING_MESSAGE);
           }
       } else {
           Piece capturedPiece = board.getPiece(clickedPosition);
           boolean moveSuccessful = board.movePiece(selectedPosition, clickedPosition);

           if (moveSuccessful) {
               // Record move in history
               String move = String.format("%s: %s → %s",
                   board.getPiece(clickedPosition).getClass().getSimpleName(),
                   selectedPosition.toString(),
                   clickedPosition.toString());
               moveHistoryModel.addElement(move);
               moveHistory.add(move);

               // Handle captured piece
               if (capturedPiece != null) {
                   addCapturedPiece(capturedPiece);

                   if (capturedPiece.getClass().getSimpleName().equals("King")) {
                       updateBoardDisplay();
                       showGameOverDialog(currentPlayer);
                       return;
                   }
               }

               currentPlayer = currentPlayer.equals("white") ? "black" : "white";
               statusLabel.setText(currentPlayer.substring(0, 1).toUpperCase() +
                                currentPlayer.substring(1) + "'s turn");
           }

           unhighlightSquare(selectedPosition);
           selectedPosition = null;
           updateBoardDisplay();
       }
   }

   private void updateBoardDisplay() {
       for (int row = 0; row < BOARD_SIZE; row++) {
           for (int col = 0; col < BOARD_SIZE; col++) {
               Position pos = new Position(row, col);
               Piece piece = board.getPiece(pos);
               JLabel square = squareLabels[row][col];
               square.setBackground((row + col) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE);

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

   private void showGameOverDialog(String winner) {
       String capitalizedWinner = winner.substring(0, 1).toUpperCase() + winner.substring(1);
       Object[] options = {"New Game", "Exit"};

       JPanel panel = new JPanel(new BorderLayout(10, 10));
       panel.add(new JLabel(capitalizedWinner + " wins the game!", SwingConstants.CENTER),
                BorderLayout.CENTER);

       if (pieceImages.containsKey(winner + "_king")) {
           JLabel kingLabel = new JLabel(pieceImages.get(winner + "_king"));
           panel.add(kingLabel, BorderLayout.NORTH);
       }

       int choice = JOptionPane.showOptionDialog(
           this,
           panel,
           "Game Over",
           JOptionPane.YES_NO_OPTION,
           JOptionPane.INFORMATION_MESSAGE,
           null,
           options,
           options[0]
       );

       if (choice == 0) {
           newGame();
       } else {
           System.exit(0);
       }
   }

  private void saveGame() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));
    
    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        try {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            
            try (PrintWriter writer = new PrintWriter(file)) {
                // Save current player
                writer.println(currentPlayer);
                
                // Save board state (piece positions)
                for (int row = 0; row < BOARD_SIZE; row++) {
                    for (int col = 0; col < BOARD_SIZE; col++) {
                        Piece piece = board.getPiece(new Position(row, col));
                        if (piece != null) {
                            writer.println(row + "," + col + "," + 
                                         piece.getColor() + "," + 
                                         piece.getClass().getSimpleName());
                        }
                    }
                }
                
                // Save move history
                writer.println("MOVES_START");
                for (String move : moveHistory) {
                    writer.println(move);
                }
                
                // Save captured pieces
                writer.println("CAPTURED_START");
                for (Component comp : whiteCapturedPanel.getComponents()) {
                    if (comp instanceof JLabel) {
                        JLabel label = (JLabel) comp;
                        if (label.getIcon() != null) {
                            writer.println("white," + label.getName());
                        }
                    }
                }
                for (Component comp : blackCapturedPanel.getComponents()) {
                    if (comp instanceof JLabel) {
                        JLabel label = (JLabel) comp;
                        if (label.getIcon() != null) {
                            writer.println("black," + label.getName());
                        }
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "Game saved successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving game: " + e.getMessage());
        }
    }
}

private void loadGame() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));
    
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        try {
            // Reset the game state
            board = new Board();
            moveHistory.clear();
            moveHistoryModel.clear();
            whiteCapturedPanel.removeAll();
            blackCapturedPanel.removeAll();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                // Read current player
                currentPlayer = reader.readLine();
                
                String line;
                boolean readingMoves = false;
                boolean readingCaptured = false;
                
                while ((line = reader.readLine()) != null) {
                    if (line.equals("MOVES_START")) {
                        readingMoves = true;
                        continue;
                    }
                    if (line.equals("CAPTURED_START")) {
                        readingMoves = false;
                        readingCaptured = true;
                        continue;
                    }
                    
                    if (readingMoves) {
                        moveHistory.add(line);
                        moveHistoryModel.addElement(line);
                    } else if (readingCaptured) {
                        String[] parts = line.split(",");
                        addCapturedPiece(parts[0], parts[1]);
                    } else {
                    // Reading board state
                    String[] parts = line.split(",");
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);
                    String color = parts[2];
                    String pieceType = parts[3];

                    // Create and place piece
                    Position pos = new Position(row, col);
                    Piece piece = createPiece(pieceType, color, pos);
                    if (piece != null) {
                        board.placePiece(piece, pos);
                    }
                }
                }
                
                updateBoardDisplay();
                statusLabel.setText(currentPlayer.substring(0, 1).toUpperCase() + 
                                 currentPlayer.substring(1) + "'s turn");
                
                JOptionPane.showMessageDialog(this, "Game loaded successfully!");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading game: " + e.getMessage());
        }
    }
}

private Piece createPiece(String type, String color, Position pos) {
    try {
        Class<?> pieceClass = Class.forName("pieces." + type);
        return (Piece) pieceClass.getConstructor(String.class, Position.class)
                               .newInstance(color, pos);
    } catch (Exception e) {
        System.err.println("Error creating piece: " + e.getMessage());
        return null;
    }
}

private void addCapturedPiece(String color, String pieceType) {
    ImageIcon icon = pieceImages.get(color + "_" + pieceType.toLowerCase());
    JLabel pieceLabel = new JLabel(icon);
    pieceLabel.setName(pieceType);
    pieceLabel.setPreferredSize(new Dimension(SQUARE_SIZE/2, SQUARE_SIZE/2));
    
    if (color.equals("white")) {
        blackCapturedPanel.add(pieceLabel);
    } else {
        whiteCapturedPanel.add(pieceLabel);
    }
    capturedPiecesPanel.revalidate();
    capturedPiecesPanel.repaint();
}

private void undoMove() {
    if (moveHistory.size() > 0) {
        // Remove last move from history
        moveHistory.remove(moveHistory.size() - 1);
        moveHistoryModel.remove(moveHistoryModel.size() - 1);
        
        // Reset board to initial state
        board = new Board();
        currentPlayer = "white";
        
        // Clear captured pieces
        whiteCapturedPanel.removeAll();
        blackCapturedPanel.removeAll();
        
        // Replay all moves except the last one
        for (String moveStr : moveHistory) {
            // Parse move from history format
            String[] parts = moveStr.split(": ");
            String[] positions = parts[1].split(" → ");
            
            Position from = Position.fromAlgebraic(positions[0]);
            Position to = Position.fromAlgebraic(positions[1]);
            
            // Handle captures during replay
            Piece capturedPiece = board.getPiece(to);
            if (capturedPiece != null) {
                addCapturedPiece(capturedPiece.getColor(), 
                               capturedPiece.getClass().getSimpleName());
            }
            
            // Execute the move
            board.movePiece(from, to);
            currentPlayer = currentPlayer.equals("white") ? "black" : "white";
        }
        
            // Update display
            statusLabel.setText(currentPlayer.substring(0, 1).toUpperCase() + 
                            currentPlayer.substring(1) + "'s turn");
            updateBoardDisplay();
            capturedPiecesPanel.revalidate();
            capturedPiecesPanel.repaint();
                }
            }
        private void highlightSquare(Position pos) {
            squareLabels[pos.getRow()][pos.getColumn()].setBorder(
                BorderFactory.createLineBorder(Color.YELLOW, 2)
            );
        }

        private void unhighlightSquare(Position pos) {
            squareLabels[pos.getRow()][pos.getColumn()].setBorder(null);
        }

        private void newGame() {
            board = new Board();
            currentPlayer = "white";
            moveHistory.clear();
            moveHistoryModel.clear();
            whiteCapturedPanel.removeAll();
            blackCapturedPanel.removeAll();
            selectedPosition = null;
            capturedPiecesPanel.revalidate();
            updateBoardDisplay();
            statusLabel.setText("White's turn");
        }

        private void recreateBoard() {
            loadPieceImages();
            remove(boardPanel);
            initializeGUI();
            updateBoardDisplay();
            revalidate();
            repaint();
            pack();
            setLocationRelativeTo(null);
        }

        private void addCapturedPiece(Piece piece) {
            String color = piece.getColor();
            String pieceType = piece.getClass().getSimpleName();
            ImageIcon icon = pieceImages.get(color + "_" + pieceType.toLowerCase());
            JLabel pieceLabel = new JLabel(icon);
            pieceLabel.setName(pieceType);
            pieceLabel.setPreferredSize(new Dimension(SQUARE_SIZE/2, SQUARE_SIZE/2));
            
            if (color.equals("white")) {
                blackCapturedPanel.add(pieceLabel);
            } else {
                whiteCapturedPanel.add(pieceLabel);
            }
            capturedPiecesPanel.revalidate();
            capturedPiecesPanel.repaint();
        }
    }
    