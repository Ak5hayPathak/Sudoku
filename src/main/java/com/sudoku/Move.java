package com.sudoku;

public class Move {
    private int row;
    private int col;
    private Cell oldCell; // deep copy of the cell before the move
    private Cell newCell; // optional: deep copy after the move for redo

    public Move(int row, int col, Cell oldCell, Cell newCell) {
        this.row = row;
        this.col = col;
        this.oldCell = oldCell;
        this.newCell = newCell != null ? copyCell(newCell) : null;
    }

    // --- Getters ---
    public int getRow() { return row; }
    public int getCol() { return col; }
    public Cell getOldCell() { return oldCell; }
    public Cell getNewCell() { return newCell; }

    // --- Utility: deep copy method ---
    public static Cell copyCell(Cell cell) {
        Cell copy = new Cell(cell.getValue(), cell.isFixed());
        copy.setCertain(cell.isCertain());
        copy.setGuessed(cell.isGuessed());
        copy.setWrong(cell.isWrong());
        for (int n : cell.getCandidates()) {
            copy.addCandidate(n);
        }
        return copy;
    }
}
