package com.sudoku.panels;

import com.sudoku.buttons.OperationsButton;
import com.sudoku.utilities.Board;
import com.sudoku.utilities.Cell;
import com.sudoku.utilities.Move;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OperatorsPanel extends JPanel{

    private int cornerRadius = 10; // border radius
    private static int countHints = 0;
    private static int countValidations = 0;
    private static boolean usedSolvedHelp;
    private static JFrame parentFrame;

    private BoardPanel boardPanel;

    ImageIcon validateIcon;
    ImageIcon hintIcon;
    ImageIcon notesIcon;
    ImageIcon solveIcon;
    ImageIcon undoIcon;
    ImageIcon redoIcon;
    ImageIcon eraseIcon;
    ImageIcon guessIcon;
    ImageIcon certainIcon;

    OperationsButton validateButton;
    OperationsButton hintButton;
    OperationsButton notesButton;
    OperationsButton solveButton;
    OperationsButton undoButton;
    OperationsButton redoButton;
    OperationsButton eraseButton;
    OperationsButton guessButton;
    OperationsButton certainButton;

    OperationsButton []numbers = new OperationsButton[9];

    public OperatorsPanel(JFrame parentFrame, int frameWidth, int frameHeight, BoardPanel boardPanel){
        this.boardPanel = boardPanel;

        this.parentFrame = parentFrame;
        // Background color
        setBackground(new Color(0xF9F9F9));
        setLayout(null); // for absolute positioning
        setOpaque(false); // Opaque false for custom painting

        // Set size dynamically based on frame size
        int width = (int) (frameWidth * 0.20);   // 23% of frame width
        int height = (int) (frameHeight * 0.80); //91% of frame height
        setBounds(849, 81, width, height);        // x, y, width, height

        validateIcon = new ImageIcon(getClass().getResource("/Icons/validate.png"));
        hintIcon = new ImageIcon(getClass().getResource("/Icons/hint.png"));
        notesIcon = new ImageIcon(getClass().getResource("/Icons/notes.png"));
        solveIcon = new ImageIcon(getClass().getResource("/Icons/solution.png"));

        validateButton = new OperationsButton("Validate", validateIcon, 16, 2, 4);
        hintButton = new OperationsButton("Hint", hintIcon, 16, 0, 6);
        notesButton = new OperationsButton("Notes", notesIcon, 16, 1, 6);
        solveButton = new OperationsButton("Solution", solveIcon, 16, 1, 0);

        validateButton.setBounds(20, 40, 180, 40); // x, y, width, height
        hintButton.setBounds(20, 85, 180, 40); // x, y, width, height
        notesButton.setBounds(20, 130, 180, 40); // x, y, width, height
        solveButton.setBounds(20, 175, 180, 40); // x, y, width, height


        for(int i=0; i<9; i++){
            numbers[i] = new OperationsButton(String.valueOf(i+1), null, 20, 0, 0);
        }

        numbers[0].setBounds(20, 230, 54, 45);
        numbers[1].setBounds(82, 230, 54, 45);
        numbers[2].setBounds(145, 230, 54, 45);
        numbers[3].setBounds(20, 282, 54, 45);
        numbers[4].setBounds(82, 282, 54, 45);
        numbers[5].setBounds(145, 282, 54, 45);
        numbers[6].setBounds(20, 334, 54, 45);
        numbers[7].setBounds(82, 334, 54, 45);
        numbers[8].setBounds(145, 334, 54, 45);

        undoIcon = new ImageIcon(getClass().getResource("/Icons/undo.png"));
        redoIcon = new ImageIcon(getClass().getResource("/Icons/redo.png"));
        eraseIcon = new ImageIcon(getClass().getResource("/Icons/erase.png"));
        guessIcon = new ImageIcon(getClass().getResource("/Icons/guess.png"));
        certainIcon = new ImageIcon(getClass().getResource("/Icons/certain.png"));

        undoButton = new OperationsButton("Undo", undoIcon, 15, 2, 0);
        redoButton = new OperationsButton("Redo", redoIcon, 15, 2, 0);
        eraseButton = new OperationsButton("Erase", eraseIcon, 15, 2, 0);
        guessButton = new OperationsButton(null, null, guessIcon, 25, 25, 0, 0, 0);
        certainButton = new OperationsButton(null,null, certainIcon, 23, 23, 0, 0, 0);

        undoButton.setBounds(20, 396, 85, 40); // x, y, width, height
        redoButton.setBounds(112, 396, 85, 40); // x, y, width, height
        eraseButton.setBounds(20, 445, 85, 40); // x, y, width, height
        guessButton.setBounds(112, 445, 40, 40); // x, y, width, height
        certainButton.setBounds(158, 445, 40, 40); // x, y, width, height


        // Set tooltip with HTML for styling
        guessButton.setToolTipText(
                "<html><div style='"
                        + "background-color:#000000;"
                        + "color:#FFFFFF;"
                        +"border: 2px solid black;"
                        + "border-radius: 10px;"
                        + "font-size:10px;"
                        + "min-width:100px;"
                        + "text-align:center;"
                        + "'>Set as a guess</div></html>"
        );

        // Set tooltip with HTML for styling
        certainButton.setToolTipText(
                "<html><div style='"
                        + "background-color:#000000;"
                        + "color:#FFFFFF;"
                        +"border: 2px solid black;"
                        + "border-radius: 10px;"
                        + "font-size:10px;"
                        + "min-width:100px;"
                        + "text-align:center;"
                        + "'>Set as certain</div></html>"
        );

        add(validateButton);
        add(hintButton);
        add(notesButton);
        add(solveButton);


        for(int i=0; i<9; i++){
            add(numbers[i]);
        }

        add(undoButton);
        add(redoButton);
        add(eraseButton);
        add(guessButton);
        add(certainButton);

        validateButton.addActionListener(e -> {
            if (!boardPanel.getBoard().isEmpty()) {
                boolean isValid = boardPanel.getBoard().isValid();
                countValidations++;

                if (isValid) {
                    showStyledMessage("All is Good!", true);
                    Toolkit.getDefaultToolkit().beep();
                }
                else{
                    Toolkit.getDefaultToolkit().beep();
                    showStyledMessage("There are Mistakes!", false);
                }
            } else {
                Toolkit.getDefaultToolkit().beep();
                showStyledMessage("The Board is Empty!", false);
            }
        });
        hintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Board board = boardPanel.getBoard();
                if (board.isEmpty()) return;

                int row = boardPanel.getSelectedRow();
                int col = boardPanel.getSelectedCol();
                if (row == -1 || col == -1) return;

                Cell cellRef = board.getCell(row, col);
                if (cellRef.isFixed()) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }

                Cell oldCell = Move.copyCell(cellRef);


                int hintValue = boardPanel.getSolvedBoard().getCell(row, col).getValue();

                // Apply the hint to the original cell
                cellRef.setValue(hintValue);
                cellRef.setCertain(true); // optional: mark it as a certain/hint

                boardPanel.recordMove(row, col, oldCell, cellRef);

                updateNumberVisibility();
                countHints++;

                boardPanel.repaint();
                if(boardPanel.getBoard().isComplete()){
                    showCompletionDialog();
                }
            }
        });
        notesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!boardPanel.getBoard().isEmpty()){

                    boolean newState = !notesButton.isActive();
                    notesButton.setActive(newState, new Color(0xDBDBDB));
                    boardPanel.setNotes(newState);
                    boardPanel.repaint();
                }
            }
        });
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!boardPanel.getBoard().isEmpty()){
                    boardPanel.getBoard().solveSudoku();
                    BoardPanel.clearStacks();
                    updateNumberVisibility();
                    usedSolvedHelp = true;
                    boardPanel.repaint();               // refresh the board UI
                    if(boardPanel.getBoard().isComplete()){
                        showCompletionDialog();
                    }
                }
            }
        });

        for (int i = 0; i < 9; i++) {
            int finalI = i;

            numbers[i].addActionListener(e -> {
                if (boardPanel.getBoard().isEmpty()) return;

                int row = boardPanel.getSelectedRow();
                int col = boardPanel.getSelectedCol();
                if (row == -1 || col == -1) return;
                if(boardPanel.getBoard().getCell(row, col).isFixed()){
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }

                Cell cellRef = boardPanel.getBoard().getCell(row, col);
                Cell oldCell = Move.copyCell(cellRef);

                if (boardPanel.getNotes()) {
                    int candidate = finalI + 1;
                    if (cellRef.getCandidates().contains(candidate)) {
                        cellRef.removeCandidate(candidate);
                    } else {
                        cellRef.addCandidate(candidate);
                    }
                } else {
                    int value = finalI + 1;

                    if (cellRef.getValue() == value) {
                        cellRef.setValue(0); // clear cell
                    } else {
                        cellRef.setValue(value); // set new value
                    }
                }

                // Update button visibility **after** changing the board
                updateNumberVisibility();
                // Refresh UI
                numbers[finalI].getParent().revalidate();
                numbers[finalI].getParent().repaint();
                boardPanel.repaint();
                boardPanel.recordMove(row, col, oldCell, cellRef);
                if(boardPanel.getBoard().isComplete()){
                    showCompletionDialog();
                }
            });
        }
        boardPanel.setBoardChangeListener(new BoardChangeListener() {
            @Override
            public void onCellValueChanged(int row, int col, int newValue) {
                // Update button visibility based on number counts
                for (int i = 0; i < 9; i++) {
                    int countNum = boardPanel.getBoard().getNumCount(i + 1);
                    numbers[i].setVisible(countNum < 9); // hide if that number already appears 9 times
                }

                revalidate();
                repaint();
            }

            @Override
            public void onCellSelected(int row, int col) {
                // Optional: you can display cell coordinates or highlight the selected number
            }

            @Override
            public void onNotesModeChanged(boolean isNotes) {
                // Optional: could change Notes button color or state
            }
        });

        eraseButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {

                if(!boardPanel.getBoard().isEmpty()){

                    int row = boardPanel.getSelectedRow();
                    int col = boardPanel.getSelectedCol();
                    Cell cellRef = boardPanel.getBoard().getCell(row, col);
                    Cell oldCell = Move.copyCell(cellRef);

                    if(boardPanel.getNotes()){
                        cellRef.clearCandidates();
                    }else{
                        if(!(cellRef.getValue()==0)){
                            if(!cellRef.isFixed()) {
                                cellRef.setValue(0);
                                updateNumberVisibility();
                            }else{
                                Toolkit.getDefaultToolkit().beep();
                                return;
                            }
                        }
                        boardPanel.repaint();               // refresh the board UI
                        boardPanel.recordMove(row, col, oldCell, cellRef);
                    }

                }
            }
        });
        guessButton.addActionListener(e -> {
            if (!boardPanel.getBoard().isEmpty()) {
                int row = boardPanel.getSelectedRow();
                int col = boardPanel.getSelectedCol();
                Cell cellRef = boardPanel.getBoard().getCell(row, col);

                if (cellRef.isFixed() || cellRef.getValue() == 0 || cellRef.isWrong()) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }

                Cell oldCell = Move.copyCell(cellRef);
                boolean newGuessState = !cellRef.isGuessed();

                // toggle guess only
                cellRef.setGuessed(newGuessState);

                // if guessed is true, remove certain (mutual exclusion)
                if (newGuessState) cellRef.setCertain(false);

                boardPanel.repaint();
                boardPanel.recordMove(row, col, oldCell, cellRef);
            }
        });
        certainButton.addActionListener(e -> {
            if (!boardPanel.getBoard().isEmpty()) {
                int row = boardPanel.getSelectedRow();
                int col = boardPanel.getSelectedCol();
                Cell cellRef = boardPanel.getBoard().getCell(row, col);

                if (cellRef.isFixed() || cellRef.getValue() == 0 || cellRef.isWrong()) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }

                Cell oldCell = Move.copyCell(cellRef);
                boolean newCertainState = !cellRef.isCertain();

                // toggle certain only
                cellRef.setCertain(newCertainState);

                // if certain is true, remove guess (mutual exclusion)
                if (newCertainState) cellRef.setGuessed(false);

                boardPanel.repaint();
                boardPanel.recordMove(row, col, oldCell, cellRef);
            }
        });
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!boardPanel.getBoard().isEmpty()){
                    boardPanel.undo();
                    updateNumberVisibility();
                    boardPanel.repaint();
                }
            }
        });
        redoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!boardPanel.getBoard().isEmpty()){
                    boardPanel.redo();
                    updateNumberVisibility();
                    boardPanel.repaint();
                }
            }
        });

    }

    public static int getCountHints(){
        return countHints;
    }

    public static void setCountHints(int n){
        countHints = n;
    }

    public static void clearCountHints(){
        countHints = 0;
    }

    public static int getCountValidations(){
        return countValidations;
    }

    public static void setCountValidations(int n){
        countValidations = n;
    }

    public static void clearCountValidations(){
        countValidations = 0;
    }

    public static boolean getSolved(){
        return usedSolvedHelp;
    }

    public static void setSolved(boolean n){
        usedSolvedHelp = n;
    }

    public static void clearSolved(){
        usedSolvedHelp = false;
    }

    public void updateNumberVisibility() {
        if (boardPanel == null || boardPanel.getBoard().isEmpty()) return;

        for (int i = 0; i < 9; i++) {
            int count = boardPanel.getBoard().getNumCount(i + 1);
            numbers[i].setVisible(count < 9);
        }

        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        updateNumberVisibility();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill rounded rectangle
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        // Draw border
        g2.setColor(new Color(0xDBDBDB));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        g2.dispose();
    }

    private void showStyledMessage(String message, boolean success) {
        JDialog dialog = new JDialog(parentFrame, "Validation Result", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(360, 220);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setUndecorated(true);
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(0xDBDBDB), 2));

        // === Main message ===
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Poppins", Font.PLAIN, 20));
        messageLabel.setForeground(success ? new Color(0x1CB123) : new Color(0x0658AE));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        // === Game statistics panel ===
        JPanel statsPanel = new JPanel(new GridLayout(3, 1));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel hintsLabel = new JLabel("Hints used: " + getCountHints(), SwingConstants.CENTER);
        hintsLabel.setFont(new Font("Poppins", Font.PLAIN, 15));

        JLabel validationsLabel = new JLabel("Validations used: " + getCountValidations(), SwingConstants.CENTER);
        validationsLabel.setFont(new Font("Poppins", Font.PLAIN, 15));

        JLabel helpLabel = new JLabel("", SwingConstants.CENTER);
        helpLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        helpLabel.setForeground(new Color(0xC0392B)); // red tone for warning/help text
        if (getSolved()) {
            helpLabel.setText("You used help to solve the Sudoku.");
        }

        statsPanel.add(hintsLabel);
        statsPanel.add(validationsLabel);
        statsPanel.add(helpLabel);

        // === OK Button ===
        OperationsButton okButton = new OperationsButton("OK", null, 14, 2, 0);
        okButton.setPreferredSize(new Dimension(100, 35));
        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);

        // === Add everything to dialog ===
        dialog.add(messageLabel, BorderLayout.NORTH);
        dialog.add(statsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public static void showCompletionDialog() {

        MenuPanel.getTimer().stop();
        BoardPanel.clearStacks();
        
        JDialog dialog = new JDialog(parentFrame, "Sudoku Completed!", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(420, 300);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setUndecorated(true);
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(0xDBDBDB), 2));

        // === Title ===
        JLabel titleLabel = new JLabel("Congratulations!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 26));
        titleLabel.setForeground(new Color(0x0658AE));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 0, 10));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // === Subtitle ===
        JLabel subtitleLabel = new JLabel("You’ve successfully completed the Sudoku puzzle!", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Poppins", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(0x444444));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // === Stats panel ===
        JPanel statsPanel = new JPanel();
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel timeLabel = new JLabel("Time: " + MenuPanel.getFormattedTime(), SwingConstants.CENTER);
        timeLabel.setFont(new Font("Poppins", Font.PLAIN, 15));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hintsLabel = new JLabel("Hints used: " + OperatorsPanel.getCountHints(), SwingConstants.CENTER);
        hintsLabel.setFont(new Font("Poppins", Font.PLAIN, 15));
        hintsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel validationsLabel = new JLabel("Validations used: " + OperatorsPanel.getCountValidations(), SwingConstants.CENTER);
        validationsLabel.setFont(new Font("Poppins", Font.PLAIN, 15));
        validationsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel helpLabel = new JLabel("", SwingConstants.CENTER);
        helpLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        helpLabel.setForeground(new Color(0xC0392B));
        helpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (OperatorsPanel.getSolved()) {
            helpLabel.setText("You used help to solve this Sudoku.");
        }

        statsPanel.add(timeLabel);
        statsPanel.add(Box.createVerticalStrut(5));
        statsPanel.add(hintsLabel);
        statsPanel.add(Box.createVerticalStrut(5));
        statsPanel.add(validationsLabel);
        statsPanel.add(Box.createVerticalStrut(5));
        statsPanel.add(helpLabel);

        // === Button panel ===
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));

        OperationsButton closeButton = new OperationsButton("Close", null, 14, 2, 0);
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);

        // === Message panel (for title + subtitle) ===
        JPanel messagePanel = new JPanel();
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        messagePanel.add(titleLabel);
        messagePanel.add(Box.createVerticalStrut(5));
        messagePanel.add(subtitleLabel);

        // === Add all sections ===
        dialog.add(messagePanel, BorderLayout.NORTH);
        dialog.add(statsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }


}