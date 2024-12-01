
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

public class ChessGUI extends JFrame {
    private static final int BOARD_SIZE = 8;
    private static final String WHITE = "white";
    private static final String BLACK = "black";

    private int squareSize = 60;
    private Color lightSquareColor = new Color(240, 217, 181);
    private Color darkSquareColor = new Color(181, 136, 99);

    private Board board;
    private Position selectedPosition;
    private String currentPlayer;
    private ArrayList<String> moveHistory;

    private JPanel boardPanel;
    private JLabel[][] squareLabels;
    private JLabel statusLabel;
    private JList<String> moveHistoryList;
    private DefaultListModel<String> moveHistoryModel;
    private JPanel whiteCapturedPanel, blackCapturedPanel;

    private Map<String, ImageIcon> pieceImages;

    public ChessGUI() {
        initializeGame();
        initializeGUI();
        loadPieceImages();
        updateBoardDisplay();
    }

    // Game Initialization
    private void initializeGame() {
        board = new Board();
        currentPlayer = WHITE;
        selectedPosition = null;
        moveHistory = new ArrayList<>();
        pieceImages = new HashMap<>();
    }

    // GUI Initialization
    private void initializeGUI() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("White's turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(statusLabel, BorderLayout.NORTH);

        boardPanel = createBoardPanel();
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        JPanel sidePanel = createSidePanel();
        mainPanel.add(sidePanel, BorderLayout.EAST);

        add(mainPanel);
        setJMenuBar(createMenuBar());

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Game Menu
        JMenu gameMenu = new JMenu("Game");
        gameMenu.add(createMenuItem("New Game", e -> newGame()));
        gameMenu.add(createMenuItem("Save Game", e -> saveGame()));
        gameMenu.add(createMenuItem("Load Game", e -> loadGame()));
        gameMenu.addSeparator();
        gameMenu.add(createMenuItem("Exit", e -> System.exit(0)));

        // Settings Menu
        JMenu settingsMenu = new JMenu("Settings");
        settingsMenu.add(createMenuItem("Board Settings", e -> showSettingsDialog()));

        menuBar.add(gameMenu);
        menuBar.add(settingsMenu);

        return menuBar;
    }

    private JMenuItem createMenuItem(String title, ActionListener action) {
        JMenuItem menuItem = new JMenuItem(title);
        menuItem.addActionListener(action);
        return menuItem;
    }

    private JPanel createBoardPanel() {
        JPanel panel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        squareLabels = new JLabel[BOARD_SIZE][BOARD_SIZE];

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                JLabel square = new JLabel("", SwingConstants.CENTER);
                square.setPreferredSize(new Dimension(squareSize, squareSize));
                square.setOpaque(true);
                square.setBackground((row + col) % 2 == 0 ? lightSquareColor : darkSquareColor);

                final Position position = new Position(row, col);
                square.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleSquareClick(position);
                    }
                });

                squareLabels[row][col] = square;
                panel.add(square);
            }
        }
        return panel;
    }

    private JPanel createSidePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(200, BOARD_SIZE * squareSize));

        moveHistoryModel = new DefaultListModel<>();
        moveHistoryList = new JList<>(moveHistoryModel);
        JScrollPane historyScrollPane = new JScrollPane(moveHistoryList);

        JPanel capturedPiecesPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        whiteCapturedPanel = createCapturedPanel("White Captures");
        blackCapturedPanel = createCapturedPanel("Black Captures");
        capturedPiecesPanel.add(whiteCapturedPanel);
        capturedPiecesPanel.add(blackCapturedPanel);

        JButton undoButton = new JButton("Undo Move");
        undoButton.addActionListener(e -> undoMove());

        panel.add(new JLabel("Move History", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(historyScrollPane, BorderLayout.CENTER);
        panel.add(capturedPiecesPanel, BorderLayout.SOUTH);
        panel.add(undoButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCapturedPanel(String title) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private void loadPieceImages() {
        String[] pieces = {"pawn", "rook", "knight", "bishop", "queen", "king"};
        String[] colors = {WHITE, BLACK};

        for (String color : colors) {
            for (String piece : pieces) {
                String filename = color + "_" + piece + ".png";
                try {
                    Image img = ImageIO.read(new File("src/resources/pieces/" + filename));
                    Image scaledImg = img.getScaledInstance(squareSize - 10, squareSize - 10, Image.SCALE_SMOOTH);
                    pieceImages.put(color + "_" + piece, new ImageIcon(scaledImg));
                } catch (IOException e) {
                    System.err.println("Failed to load image: " + filename);
                }
            }
        }
    }

    private void handleSquareClick(Position position) {
        if (selectedPosition == null) {
            Piece piece = board.getPiece(position);
            if (piece != null && piece.getColor().equals(currentPlayer)) {
                selectedPosition = position;
                highlightSquare(position);
            }
        } else {
            boolean moveSuccessful = board.movePiece(selectedPosition, position);
            if (moveSuccessful) {
                moveHistory.add(selectedPosition + " → " + position);
                moveHistoryModel.addElement(moveHistory.get(moveHistory.size() - 1));
                currentPlayer = currentPlayer.equals(WHITE) ? BLACK : WHITE;
                statusLabel.setText(currentPlayer + "'s turn");
            }
            unhighlightSquare(selectedPosition);
            selectedPosition = null;
            updateBoardDisplay();
        }
    }

    private void highlightSquare(Position pos) {
        squareLabels[pos.getRow()][pos.getColumn()].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
    }

    private void unhighlightSquare(Position pos) {
        squareLabels[pos.getRow()][pos.getColumn()].setBorder(null);
    }

    private void updateBoardDisplay() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Position pos = new Position(row, col);
                Piece piece = board.getPiece(pos);
                JLabel square = squareLabels[row][col];
                square.setBackground((row + col) % 2 == 0 ? lightSquareColor : darkSquareColor);
                square.setIcon(piece == null ? null : pieceImages.get(piece.getColor() + "_" + piece.getClass().getSimpleName().toLowerCase()));
            }
        }
    }

    private void newGame() {
        initializeGame();
        updateBoardDisplay();
        statusLabel.setText("White's turn");
    }

    private void saveGame() {
        // Save game logic
    }

    private void loadGame() {
        // Load game logic
    }

    private void undoMove() {
        // Undo move logic
    }

    private void showSettingsDialog() {
        // Settings dialog logic
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessGUI::new);
    }
}

