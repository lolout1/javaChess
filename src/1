package utils;

/**
 * Represents a position on the chessboard with row and column.
 */
public class Position {
    private int row;
    private int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public static Position fromAlgebraic(String notation) {
        notation = notation.trim().toUpperCase();
        if (notation.length() != 2) return null;
        char file = notation.charAt(0);
        char rank = notation.charAt(1);
        int column = file - 'A';
        int row = 8 - (rank - '0');
        if (column < 0 || column > 7 || row < 0 || row > 7) return null;
        return new Position(row, column);
    }

    @Override
    public String toString() {
        char file = (char) ('A' + column);
        int rank = 8 - row;
        return "" + file + rank;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position other = (Position) obj;
        return row == other.row && column == other.column;
    }

    @Override
    public int hashCode() {
        return 31 * row + column;
    }
}

