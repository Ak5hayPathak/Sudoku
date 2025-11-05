package com.sudoku.panels;

import com.sudoku.core.Board;
import com.sudoku.core.Cell;
import com.sudoku.core.Move;
import com.sudoku.listener.BoardChangeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

public class BoardPanel extends JPanel {

    private static final int SIZE = 9;
    private int cornerRadius = 10;
    private boolean isNotes;
    private BoardChangeListener listener;

    private int panelWidth;
    private int panelHeight;

    private Board board = new Board();
    private Board solvedBoard;
    private Board copyOfBoard;
    private int selectedRow = -1;
    private int selectedCol = -1;

    static Stack<Move> undoStack = new Stack<>();
    static Stack<Move> redoStack = new Stack<>();

    public BoardPanel(int frameWidth, int frameHeight) {
        setBackground(new Color(0xF9F9F9));
        setLayout(null);
        setOpaque(false);

        this.panelWidth = (int) (frameWidth * 0.57);
        this.panelHeight = (int) (frameHeight * 0.80);

        // Position relative to panel dimensions
        int x = (int) (frameWidth * 0.203);   // Example: 39% of panel width
        int y = (int) (frameHeight * 0.122);  // Example: 10% of panel height

        setBounds(x, y, panelWidth, panelHeight);

        setFocusable(true);

        // Mouse listener for selecting cells
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                locateSelectedCell(e.getX(), e.getY());
                repaint();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (board.isEmpty() || selectedRow == -1 || selectedCol == -1) return;

                Cell cell = board.getCell(selectedRow, selectedCol);
                if (cell.isFixed()) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }

                char ch = e.getKeyChar();
                Cell oldCell = Move.copyCell(cell);

                if (ch >= '1' && ch <= '9') {
                    int num = ch - '0';

                    if (isNotes) {
                        // Notes mode: toggle candidate
                        cell.setValue(0);
                        if (cell.getCandidates().contains(num)) {
                            cell.getCandidates().remove(num);
                        } else {
                            cell.getCandidates().add(num);
                        }

                    } else {
                        // Normal mode: main value
                        if (board.getNumCount(num) >= 9) {
                            Toolkit.getDefaultToolkit().beep();
                            return;
                        }

                        cell.setValue(num);
                        cell.getCandidates().clear();
                        if(board.isComplete()){
                            OperatorsPanel.showCompletionDialog();
                        }
                        if (listener != null) {
                            listener.onCellValueChanged(selectedRow, selectedCol, cell.getValue());
                        }
                    }
                } else if (ch == '0' || ch == KeyEvent.VK_BACK_SPACE || ch == KeyEvent.VK_DELETE) {
                    // deleting main value or candidates
                    cell.getCandidates().clear();
                    cell.setValue(0);
                    board.isCorrect(selectedRow, selectedCol);
                    if (listener != null) {
                        listener.onCellValueChanged(selectedRow, selectedCol, cell.getValue());
                    }
                }
                repaint();
                recordMove(selectedRow, selectedCol, oldCell, cell);
            }
        });

    }

    public void setBoardChangeListener(BoardChangeListener listener) {
        this.listener = listener;
    }

    public void toggleNotesMode() {
        isNotes = !isNotes;
        if (listener != null) listener.onNotesModeChanged(isNotes);
        repaint();
        requestFocusInWindow(); // ensures key events work immediately
    }

    /** Sets the Board object */
    public void setBoard(Board board) {
        this.board = board;
        repaint();

        copyOfBoard = new Board(board);
        copyOfBoard.refreshBoard();
        solvedBoard = new Board(copyOfBoard);
        solvedBoard.solveSudoku();
    }

    public Board getSolvedBoard(){
        return this.solvedBoard;
    }

    public void setValueOfCell(int row, int col, int val){
        this.board.getCell(row, col).setValue(val);
        repaint();
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public void setNotes(boolean notes){
        this.isNotes = notes;
    }

    public boolean getNotes(){
        return this.isNotes;
    }

    public int getSelectedCol() {
        return selectedCol;
    }

    public Board getBoard(){
        return this.board;
    }

    public Board getCopyOfBoard(){return this.copyOfBoard;}

    public void recordMove(int row, int col, Cell oldCell, Cell newCell) {
        Move move = new Move(row, col, oldCell, newCell);
        undoStack.push(move);
        redoStack.clear(); // New move invalidates redo history
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            Move lastMove = undoStack.pop();
            int row = lastMove.getRow();
            int col = lastMove.getCol();

            selectedRow = row;
            selectedCol = col;
            board.setCell(row, col, lastMove.getOldCell()); // Restore the old cell
            redoStack.push(lastMove); // Push this move onto the redo stack

            repaint(); // refresh UI if needed
        } else {
            Toolkit.getDefaultToolkit().beep(); // optional feedback
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            Move lastUndo = redoStack.pop();
            int row = lastUndo.getRow();
            int col = lastUndo.getCol();

            selectedRow = row;
            selectedCol = col;
            // Restore the new cell
            if (lastUndo.getNewCell() != null) {
                board.setCell(row, col, lastUndo.getNewCell());
            }

            undoStack.push(lastUndo); // Push this move back onto the undo stack
            repaint(); // refresh UI
        } else {
            Toolkit.getDefaultToolkit().beep(); // optional feedback
        }
    }

    public static void clearStacks(){
        undoStack.clear();
        redoStack.clear();
    }

    public void refreshPanel(){
        selectedCol = -1;
        selectedRow = -1;
        repaint();
    }

    private void locateSelectedCell(int x, int y) {
        int w = this.panelWidth;
        int h = this.panelHeight;

        // Make constants relative to panel size
        int padding = (int) (w * 0.03);      // ~3% of width
        int topOffset = (int) (h * 0.012);   // ~1.2% of height
        int innerGap = (int) (w * 0.008);    // replaces the "+10" constant

        // Compute grid boundaries and cell sizes
        int gridX = padding + innerGap;
        int gridY = padding + topOffset;
        int gridWidth = w - 3 * padding;
        int gridHeight = h - 2 * padding;

        double cellWidth = (double) gridWidth / SIZE;
        double cellHeight = (double) gridHeight / SIZE;

        // Check if (x, y) is within grid boundaries
        if (x < gridX || x > gridX + gridWidth || y < gridY || y > gridY + gridHeight) {
            selectedRow = selectedCol = -1;
            return;
        }

        // Determine which cell is selected
        selectedCol = (int) ((x - gridX) / cellWidth);
        selectedRow = (int) ((y - gridY) / cellHeight);

        // Notify listener
        if (listener != null && selectedRow != -1 && selectedCol != -1) {
            listener.onCellSelected(selectedRow, selectedCol);
        }
    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (board == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = this.panelWidth;
        int h = this.panelHeight;

        // --- Scaled constants (based on 624x535 reference panel) ---
        int padding = (int) (w * 0.032);         // ≈20px originally
        int gridOffset = (int) (w * 0.016);      // ≈10px originally
        float thinStroke = (float) (w * 0.0016); // ≈1px originally
        float thickStroke = (float) (w * 0.0032);// ≈2px originally
        int cornerRadius = (int) (w * 0.022);    // ≈14px originally
        int fontYOffset = (int) (h * 0.0056);    // ≈3px originally

        // --- Panel Background ---
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
        g2.setColor(new Color(0xDBDBDB));
        g2.setStroke(new BasicStroke(thickStroke));
        g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        // --- Grid Configuration ---
        int gridX = padding + gridOffset;
        int gridY = padding;
        int gridWidth = w - 3 * padding;
        int gridHeight = h - 2 * padding;
        double cellWidth = (double) gridWidth / SIZE;
        double cellHeight = (double) gridHeight / SIZE;

        // --- Highlights for Selected Cell ---
        if (selectedRow != -1 && selectedCol != -1) {
            int selectedValue = board.getCell(selectedRow, selectedCol).getValue();

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));

            int boxRow = (selectedRow / 3) * 3;
            int boxCol = (selectedCol / 3) * 3;
            int boxX = (int) Math.round(gridX + boxCol * cellWidth);
            int boxY = (int) Math.round(gridY + boxRow * cellHeight);
            int boxW = (int) Math.round(gridX + (boxCol + 3) * cellWidth) - boxX;
            int boxH = (int) Math.round(gridY + (boxRow + 3) * cellHeight) - boxY;

            g2.setColor(new Color(0xEAF3FC));
            g2.fillRect(boxX, boxY, boxW, boxH);

            int rowY = (int) Math.round(gridY + selectedRow * cellHeight);
            int colX = (int) Math.round(gridX + selectedCol * cellWidth);
            int rowH = (int) Math.round(gridY + (selectedRow + 1) * cellHeight) - rowY;
            int colW = (int) Math.round(gridX + (selectedCol + 1) * cellWidth) - colX;

            g2.setColor(new Color(0xE3EBF3));
            g2.fillRect(gridX, rowY, gridWidth, rowH);
            g2.fillRect(colX, gridY, colW, gridHeight);

            if (selectedValue != 0) {
                g2.setColor(new Color(0xC3D7EA));
                for (int r = 0; r < SIZE; r++) {
                    for (int c = 0; c < SIZE; c++) {
                        if (board.getCell(r, c).getValue() == selectedValue && !(r == selectedRow && c == selectedCol)) {
                            int x = (int) Math.round(gridX + c * cellWidth);
                            int y = (int) Math.round(gridY + r * cellHeight);
                            int wCell = (int) Math.round(gridX + (c + 1) * cellWidth) - x;
                            int hCell = (int) Math.round(gridY + (r + 1) * cellHeight) - y;
                            g2.fillRect(x, y, wCell, hCell);
                        }
                    }
                }
            }

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            int selX = (int) Math.round(gridX + selectedCol * cellWidth);
            int selY = (int) Math.round(gridY + selectedRow * cellHeight);
            int selW = (int) Math.round(gridX + (selectedCol + 1) * cellWidth) - selX;
            int selH = (int) Math.round(gridY + (selectedRow + 1) * cellHeight) - selY;

            g2.setColor(new Color(0x8BC5FF));
            g2.fillRect(selX, selY, selW, selH);
        }

        // --- Grid Lines ---
        g2.setColor(new Color(0xB4B4B4));
        g2.setStroke(new BasicStroke(thinStroke));
        for (int i = 1; i < SIZE; i++) {
            int x = (int) Math.round(gridX + i * cellWidth);
            int y = (int) Math.round(gridY + i * cellHeight);
            if (i % 3 != 0) {
                g2.drawLine(x, gridY, x, gridY + gridHeight);
                g2.drawLine(gridX, y, gridX + gridWidth, y);
            }
        }

        // --- Block Borders ---
        g2.setColor(new Color(0x0658AE));
        g2.setStroke(new BasicStroke(thickStroke));
        g2.drawRoundRect(gridX, gridY, gridWidth - 1, gridHeight - 1, cornerRadius, cornerRadius);
        for (int i = 3; i < SIZE; i += 3) {
            int x = (int) Math.round(gridX + i * cellWidth);
            int y = (int) Math.round(gridY + i * cellHeight);
            g2.drawLine(x, gridY, x, gridY + gridHeight);
            g2.drawLine(gridX, y, gridX + gridWidth, y);
        }

        // --- Draw Numbers or Notes ---
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cell = board.getCell(row, col);
                int value = cell.getValue();
                int x1 = (int) Math.round(gridX + col * cellWidth);
                int y1 = (int) Math.round(gridY + row * cellHeight);
                int cw = (int) Math.round(cellWidth);
                int ch = (int) Math.round(cellHeight);

                if (isNotes && cell.hasCandidates()) {
                    int noteSize = (int) (ch * 0.25);
                    g2.setFont(new Font("Roboto", Font.PLAIN, noteSize));
                    g2.setColor(Color.BLACK);

                    int paddingX = (int)(cw * 0.1);
                    int paddingY = (int)(ch * 0.1);
                    int miniCellW = cw / 3;
                    int miniCellH = ch / 3;

                    for (int n = 1; n <= 9; n++) {
                        if (cell.getCandidates().contains(n)) {
                            int colPos = (n - 1) % 3;
                            int rowPos = (n - 1) / 3;

                            int nx = x1 + colPos * miniCellW + paddingX;
                            int ny = y1 + rowPos * miniCellH + paddingY;

                            String text = String.valueOf(n);
                            FontMetrics fm = g2.getFontMetrics();
                            int textWidth = fm.stringWidth(text);
                            int textHeight = fm.getAscent();

                            int tx = nx + (miniCellW - 2 * paddingX - textWidth) / 2;
                            int ty = ny + (miniCellH - 2 * paddingY + textHeight) / 2;

                            g2.drawString(text, tx, ty);
                        }
                    }
                } else if (value != 0) {
                    int fontSize = (int) (ch * 0.52);
                    g2.setFont(new Font("Roboto", Font.PLAIN, fontSize));

                    FontMetrics fm = g2.getFontMetrics();
                    String text = String.valueOf(value);
                    int textWidth = fm.stringWidth(text);
                    int textHeight = fm.getAscent();
                    int textX = x1 + (cw - textWidth) / 2;
                    int textY = y1 + (ch + textHeight) / 2 - fontYOffset;

                    Color numColor;
                    if (cell.isFixed()) {
                        numColor = Color.BLACK;
                    } else {
                        if (board.isCorrect(row, col)) {
                            if (cell.isGuessed()) numColor = new Color(0xE6AB01);
                            else if (cell.isCertain()) numColor = new Color(0x00C417);
                            else numColor = new Color(0x1E88E5);
                        } else {
                            numColor = new Color(0xFF7171);
                        }
                    }

                    g2.setColor(numColor);
                    g2.drawString(text, textX, textY);
                }
            }
        }

        g2.dispose();
    }


}