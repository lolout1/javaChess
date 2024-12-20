# Java Chess Game

## Team Information
- **Team Name**: Untitled
- **Team Members**: 
  - Abheek
- **Course**: CS 3354
- **Semester**: Fall 2024
- **Section**: [003]

## Project Overview
A fully functional GUI-based chess game implemented in Java, featuring complete chess rules implementation including move validation, piece capture, check and checkmate detection.

## Preview
![Chess Game Interface](GUIgamestate.png)
<!-- Add your game interface screenshot here -->

## Class Diagram
![Class Diagram](plantuml/Chess_Game_Class_Diagram.png)
<!-- Add your UML class diagram here -->

## Features
- [x] Complete chess rules implementation
- [x] Graphical User Interface
- [x] Move validation
- [x] Piece capture
- [x] Check detection
- [x] Checkmate detection
- [x] Turn-based gameplay
- [x] Game state visualization
- [x] Save/Load game functionality - Menu Bar with Game Controls
- [x] Move history
- [x] Undo moves and move logger
- [x] Settings Window for Customization including user customizable board size and board color
- [x] Game History Panel with Undo Button


## How to Run
1. **Prerequisites**:
   - Java JDK 11 or higher
   - Java IDE (Eclipse, IntelliJ IDEA, or similar)

2. **Compilation**:
   ```bash
   javac -d bin src/**/*.java
   ```

3. **Running the Game**:
   ```bash
   java -cp bin main.ChessGame
   ```

## Game Instructions
1. Launch the game using the instructions above
2. Click on a piece to select it
3. Valid moves will be highlighted
4. Click on a highlighted square to move the selected piece
5. Game automatically detects check and checkmate conditions
6. Players alternate turns until checkmate or stalemate

## Development Notes
- Backend chess logic integrated with GUI
- Move validation implemented for all piece types
- Check and checkmate detection fully functional
- Multiple commits with clear documentation
- Well-commented code structure
