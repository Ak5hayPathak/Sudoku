package com.sudoku;

import javax.swing.*;
import java.awt.*;

public class SudokuFrame extends JFrame {

    private Board board;
    private Board restartBoard;
    private Board solvedBoard;

    public SudokuFrame() {
        setTitle("Sudoku");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.80);
        int height = (int) (screenSize.height * 0.87);
        setSize(width, height);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0xF3F3F3));
        setResizable(false);

        // Use absolute positioning
        setLayout(null);

        // Load icon image
        ImageIcon icon = new ImageIcon(getClass().getResource("/Icons/fevicon.png"));

        setIconImage(icon.getImage());

        BoardPanel boardPanel = new BoardPanel(width, height);
        BoardPanel boardPanel2 = new BoardPanel(width, height);

        OperatorsPanel operatorsPanel = new OperatorsPanel(this, width, height, boardPanel);
        LevelPanel levelPanel = new LevelPanel(width, height, boardPanel, operatorsPanel);

        MenuPanel menuPanel = new MenuPanel(width, height, boardPanel, levelPanel, operatorsPanel);

        add(levelPanel);
        add(menuPanel);
        add(boardPanel);
        add(boardPanel2);
        add(operatorsPanel);

        setVisible(true);
    }

}