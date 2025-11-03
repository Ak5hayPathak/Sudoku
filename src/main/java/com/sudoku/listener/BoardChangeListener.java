package com.sudoku.listener;

public interface BoardChangeListener {
    /**
     * Called whenever a cell’s value changes
     */
    void onCellValueChanged(int row, int col, int newValue);

    /**
     * Called when the selected cell changes
     */
    void onCellSelected(int row, int col);

    /**
     * Called when notes mode toggles
     */
    void onNotesModeChanged(boolean isNotes);
}
