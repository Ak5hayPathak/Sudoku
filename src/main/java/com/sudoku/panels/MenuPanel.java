package com.sudoku.panels;

import com.sudoku.buttons.DifficultyButton;
import com.sudoku.buttons.OperationsButton;
import com.sudoku.utilities.Board;
import com.sudoku.utilities.Cell;
import com.sudoku.utilities.SaveNLoad;
import com.sudoku.utilities.SudokuGenerator;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class MenuPanel extends JPanel{

    BoardPanel boardPanel;
    LevelPanel levelPanel;
    OperatorsPanel operatorsPanel;

    private static JLabel timerLabel;
    private static Timer timer;
    private static int elapsedSeconds = 0;

    private int cornerRadius = 10; // border radius
    ImageIcon playIcon;
    static ImageIcon pauseIcon;
    ImageIcon restartIcon;
    ImageIcon newGameIcon;
    ImageIcon loadIcon;
    ImageIcon saveIcon;

    static OperationsButton playButton;
    OperationsButton restartButton;
    OperationsButton newGameButton;
    OperationsButton loadButton;
    OperationsButton saveButton;
    public static boolean isPlaying = false;

    Board tempBoard;

    DifficultyButton[] difficultyButtons;
    String[] difficulties;
    String difficulty;
    boolean isCompleted;
    int timeElapsed;
    int hints;
    int validations;
    boolean solvedHelp;
    Board saveBoard;

    public MenuPanel(int frameWidth, int frameHeight, BoardPanel boardPanel, LevelPanel levelPanel, OperatorsPanel operatorsPanel){

        this.boardPanel = boardPanel;
        this.levelPanel = levelPanel;
        this.operatorsPanel = operatorsPanel;
        int borderRadius = 12;

        // Background color
        setBackground(new Color(0xF9F9F9));
        setLayout(null); // <-- important for absolute positioning
        setOpaque(false);// Opaque false for custom painting

        // Set size dynamically based on frame size
        int width = (int) (frameWidth * 0.775);   // 75% of frame width
        int height = (int) (frameHeight * 0.10); //12% of frame height
        setBounds(222, 10, width, height);        // x, y, width,

        // Timer label
        timerLabel = new JLabel("00:00:00");
        timerLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        timerLabel.setForeground(Color.BLACK);
        timerLabel.setBounds(60, 11, 80, 45); // Adjust position as needed
        add(timerLabel);

        // Timer that ticks every second
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedSeconds++;
                updateTimerLabel();
            }
        });

        playIcon = new ImageIcon(
                new ImageIcon(getClass().getResource("/Icons/play.png"))
                        .getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)
        );
        pauseIcon = new ImageIcon(
                new ImageIcon(getClass().getResource("/Icons/pause.png"))
                        .getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)
        );
        restartIcon = new ImageIcon(getClass().getResource("/Icons/restart.png"));
        newGameIcon = new ImageIcon(getClass().getResource("/Icons/newGame.png"));
        loadIcon = new ImageIcon(getClass().getResource("/Icons/load.png"));
        saveIcon = new ImageIcon(getClass().getResource("/Icons/save.png"));


        playButton = new OperationsButton(null, null, null, null, 0, 0, null, playIcon, 25, 25, 0, 0, 0);
        restartButton = new OperationsButton(null, new Color(0xEBEBEB), new Color(0xDBDBDB), null, borderRadius, 1, "Restart", restartIcon, 24, 24, 14, 0, 0);
        newGameButton = new OperationsButton(null, new Color(0xEBEBEB), new Color(0xDBDBDB), null, borderRadius, 1, "New Game", newGameIcon, 24, 24, 14, 0, 1);
        loadButton = new OperationsButton(null, new Color(0xEBEBEB), new Color(0xDBDBDB), null, borderRadius, 1, "Load Game", loadIcon, 24, 24, 14, 0, 1);
        saveButton = new OperationsButton(null, new Color(0xEBEBEB), new Color(0xDBDBDB), null, borderRadius, 1, "Save Game", saveIcon, 24, 24, 14, 0, 0);

        playButton.setBounds(20, 11, 45, 45);
        int xPosition = 330;
        restartButton.setBounds(xPosition + 26, 11, 95, 45);
        newGameButton.setBounds(xPosition + 121, 11, 120, 45);
        loadButton.setBounds(xPosition + 247, 11, 120, 45);
        saveButton.setBounds(xPosition + 376, 11, 120, 45);

        add(playButton);
        add(restartButton);
        add(newGameButton);
        add(loadButton);
        add(saveButton);

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(boardPanel.getBoard().isEmpty() || boardPanel.getBoard().isComplete()) return;

                if (isPlaying) {
                    // Game is running → pause it
                    playButton.setIcon(playIcon);
                    isPlaying = false;
                    timer.stop();
                    boardPanel.setVisible(false);

                } else {
                    // Game is paused → resume it
                    playButton.setIcon(pauseIcon);
                    isPlaying = true;
                    timer.start();
                    boardPanel.setVisible(true);
                }
                repaint();
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(boardPanel.getBoard().isEmpty()) return;

                boardPanel.setBoard(boardPanel.getCopyOfBoard());
                operatorsPanel.clearSolved();
                operatorsPanel.clearCountValidations();
                operatorsPanel.clearSolved();
                BoardPanel.clearStacks();
                operatorsPanel.updateNumberVisibility();

                resetTimer();

                boardPanel.setVisible(true);
                boardPanel.repaint();
            }
        });

        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                difficultyButtons = levelPanel.getDifficultyButtons();
                difficulties = levelPanel.getDifficulties();
                Board board;

                resetTimer();

                operatorsPanel.updateNumberVisibility();
                for(int i=0; i<5; i++){
                    if(difficultyButtons[i].isActive()){
                        board = SudokuGenerator.generate(LevelPanel.mapDifficulty(difficulties[i]), SudokuGenerator.DiggingSequence.RANDOM);
                        boardPanel.setBoard(board);
                        boardPanel.setVisible(true);
                        boardPanel.repaint();
                        return;
                    }
                }

                board = SudokuGenerator.generate(SudokuGenerator.Difficulty.EXTREMELY_EASY, SudokuGenerator.DiggingSequence.RANDOM);
                boardPanel.setBoard(board);
                difficultyButtons[0].setActive(true);
                operatorsPanel.clearSolved();
                operatorsPanel.clearCountValidations();
                operatorsPanel.clearSolved();
                BoardPanel.clearStacks();

                boardPanel.setVisible(true);
                boardPanel.repaint();
                levelPanel.repaint();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(boardPanel.getBoard().isEmpty()) return;

                timer.stop();
                saveBoard = boardPanel.getBoard();
                difficultyButtons = levelPanel.getDifficultyButtons();
                difficulties = levelPanel.getDifficulties();

                for(int i=0; i<5; i++){
                    if(difficultyButtons[i].isActive()){
                        difficulty = difficulties[i];
                    }
                }

                isCompleted = saveBoard.isComplete();
                timeElapsed = elapsedSeconds;
                hints = OperatorsPanel.getCountHints();
                validations = OperatorsPanel.getCountValidations();
                solvedHelp = OperatorsPanel.getSolved();

                SaveNLoad.saveGameToJSON(saveBoard, difficulty, isCompleted, timeElapsed, hints, validations, solvedHelp);
                timer.start();

            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                timer.stop();
                JSONObject gameData = SaveNLoad.loadGameFromJSON();

                if (gameData == null) {
                    JOptionPane.showMessageDialog(null,
                            "No valid saved game was loaded.",
                            "Load Game", JOptionPane.WARNING_MESSAGE);
                    return; // stop restoring process
                }


                restoreGame(gameData, boardPanel, levelPanel, timer);

                if(!boardPanel.getBoard().isComplete()) timer.start();

            }
        });

    }

    private static void updateTimerLabel() {
        int hours = elapsedSeconds / 3600;
        int minutes = (elapsedSeconds % 3600) / 60;
        int seconds = elapsedSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    public static void resetTimer(){
        elapsedSeconds = 0;
        updateTimerLabel();
        timer.restart(); // resets and starts immediately
        isPlaying = true;
        playButton.setIcon(pauseIcon);
    }

    public static String getFormattedTime() {
        int hours = elapsedSeconds / 3600;
        int minutes = (elapsedSeconds % 3600) / 60;
        int seconds = elapsedSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static void setTime(int elapsedSec){
        elapsedSeconds = elapsedSec;
    }

    public int getElapsedSeconds() {
        return elapsedSeconds;
    }

    public static Timer getTimer(){
        return timer;
    }

    public void restoreGame(JSONObject gameData, BoardPanel boardPanel, LevelPanel levelPanel, Timer timer) {
        difficulty = gameData.getString("difficulty");
        isCompleted = gameData.getBoolean("isCompleted");
        timeElapsed = gameData.getInt("timeElapsed");
        hints = gameData.getInt("hints");
        validations = gameData.getInt("validations");
        solvedHelp = gameData.getBoolean("solveHelp");

        isPlaying = true;
        playButton.setIcon(pauseIcon);
        repaint();

        difficultyButtons = levelPanel.getDifficultyButtons();
        difficulties = levelPanel.getDifficulties();

        for(int i=0; i<5; i++){
            if(Objects.equals(difficulties[i], difficulty)){
                difficultyButtons[i].setActive(true);
            }else{
                difficultyButtons[i].setActive(false);
            }
        }

        setTime(timeElapsed);
        timerLabel.setText(getFormattedTime());
        timerLabel.repaint();
        OperatorsPanel.setCountHints(hints);
        OperatorsPanel.setCountValidations(validations);
        OperatorsPanel.setSolved(solvedHelp);

        JSONArray boardArray = gameData.getJSONArray("board");
        Board board = new Board();

        for (int i = 0; i < 81; i++) {
            JSONObject cellObj = boardArray.getJSONObject(i);
            int row = i / 9;
            int col = i % 9;

            Cell cell = board.getCell(row, col);
            cell.setValue(cellObj.getInt("value"));
            cell.setFixed(cellObj.getBoolean("isFixed"));
            cell.setWrong(cellObj.getBoolean("isWrong"));
            cell.setGuessed(cellObj.getBoolean("isGuessed"));
            cell.setCertain(cellObj.getBoolean("isCertain"));

            if (!cellObj.isNull("candidates")) {
                JSONArray candidatesArray = cellObj.getJSONArray("candidates");
                for (int j = 0; j < candidatesArray.length(); j++) {
                    cell.addCandidate(candidatesArray.getInt(j));
                }
            }
        }
        boardPanel.setBoard(board);
        boardPanel.repaint();
        levelPanel.repaint();
        operatorsPanel.repaint();
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
}