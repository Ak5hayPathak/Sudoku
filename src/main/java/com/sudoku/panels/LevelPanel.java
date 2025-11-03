package com.sudoku.panels;

import com.sudoku.core.Board;
import com.sudoku.buttons.DifficultyButton;
import com.sudoku.core.SudokuGenerator;

import javax.swing.*;
import java.awt.*;

public class LevelPanel extends JPanel {
    private int cornerRadius = 10; // border radius
    private ImageIcon starIcon;
    private ImageIcon leftIcon;
    private DifficultyButton[] difficultyButtons;
    private String[] difficulties = {"Extremely Easy", "Easy", "Medium", "Hard", "Evil"};
    Board board;
    OperatorsPanel operatorsPanel;
    BoardPanel boardPanel;

    public LevelPanel(int frameWidth, int frameHeight, BoardPanel boardPanel, OperatorsPanel operatorsPanel) {
        // Background color
        setBackground(new Color(0xF9F9F9));
        setLayout(null); // <-- important for absolute positioning
        setOpaque(false); // Opaque false for custom painting

        this.operatorsPanel = operatorsPanel;
        this.boardPanel = boardPanel;

        // Set size dynamically based on frame size
        int width = (int) (frameWidth * 0.19);    // 19% of frame width
        int height = (int) (frameHeight * 0.908); // 90.8% of frame height

        // Set position dynamically based on frame size
        int x = (int) (frameWidth * 0.01);
        int y = (int) (frameHeight * 0.014);

        setBounds(x, y, width, height);


        // Creates a list to store all buttons for toggling
        java.util.List<DifficultyButton> buttonList = new java.util.ArrayList<>();

        String[] leftIcons = {"child.png", "easy.png", "medium.png", "hard.png", "evil.png"};
        int[] stars = {1, 2, 3, 4, 5};

        difficultyButtons = new DifficultyButton[difficulties.length];
        // Load star icon once
        starIcon = new ImageIcon(getClass().getResource("/Icons/star.png"));

        int spacing = (int) (height * 0.015);
        int currentY = (int) (height * 0.03);

        for (int i = 0; i < difficulties.length; i++) {
            leftIcon = new ImageIcon(getClass().getResource("/Icons/" + leftIcons[i]));
            String difficultyName = difficulties[i];
            this.difficultyButtons[i] = new DifficultyButton(difficulties[i], new Color(0xF9F9F9), leftIcon, stars[i], starIcon);

            int finalI = i;
            difficultyButtons[i].addActionListener(e -> {
                // Prevent double-clicks or re-entry
                if (!difficultyButtons[finalI].isEnabled()) return;

                // 1. Deactivate and disable all buttons
                for (DifficultyButton b : buttonList) {
                    b.setActive(false);
                    b.setEnabled(false);
                }

                // 2. Activate the clicked button
                difficultyButtons[finalI].setActive(true);

                // 3. Run Sudoku generation in the background
                SwingWorker<Board, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Board doInBackground() {
                        return SudokuGenerator.generate(
                                mapDifficulty(difficultyName),
                                SudokuGenerator.DiggingSequence.RANDOM
                        );
                    }

                    @Override
                    protected void done() {
                        try {
                            board = get(); // safely get result
                            boardPanel.setBoard(board);

                            // Reset UI states
                            operatorsPanel.clearCountValidations();
                            operatorsPanel.clearCountHints();
                            operatorsPanel.clearSolved();
                            MenuPanel.resetTimer();

                            // Ensure board panel is visible and refreshed
                            if (!boardPanel.isVisible()) boardPanel.setVisible(true);
                            boardPanel.repaint();

                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Error generating Sudoku: " + ex.getMessage(),
                                    "Generation Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            ex.printStackTrace();

                        } finally {
                            // 4. Re-enable all buttons except the currently active one
                            for (DifficultyButton b : buttonList)
                                b.setEnabled(true);

                            difficultyButtons[finalI].setEnabled(false);

                        }
                    }
                };

                worker.execute();
            });

            difficultyButtons[i].setRelativeBounds(width, height, 0.95, 0.07, currentY);
            add(difficultyButtons[i]);
            buttonList.add(difficultyButtons[i]);

            currentY += (int) (height * 0.07) + spacing;
        }

    }

    public DifficultyButton[] getDifficultyButtons(){
        return difficultyButtons;
    }

    public String[] getDifficulties(){
        return difficulties;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

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

    // Helper method to map button names to generator enums
    public static SudokuGenerator.Difficulty mapDifficulty(String name) {
        return switch (name) {
            case "Extremely Easy" -> SudokuGenerator.Difficulty.EXTREMELY_EASY;
            case "Easy" -> SudokuGenerator.Difficulty.EASY;
            case "Medium" -> SudokuGenerator.Difficulty.MEDIUM;
            case "Hard" -> SudokuGenerator.Difficulty.DIFFICULT;
            case "Evil" -> SudokuGenerator.Difficulty.EVIL;
            default -> SudokuGenerator.Difficulty.MEDIUM;
        };
    }

}