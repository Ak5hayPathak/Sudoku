package com.sudoku.panels;

import com.sudoku.buttons.DifficultyButton;
import com.sudoku.buttons.OperationsButton;
import com.sudoku.core.Board;
import com.sudoku.core.Cell;
import com.sudoku.io.SaveNLoad;
import com.sudoku.core.SudokuGenerator;
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

        // Set position dynamically based on frame size
        int x = (int) (frameWidth * 0.203);
        int y = (int) (frameHeight * 0.014);

        setBounds(x, y, width, height);        // x, y, width,

        int timerX = (int) (width * 0.07);         // 7% from left
        int timerY = (int) (height * 0.15);        // 15% from top
        int timerW = (int) (width * 0.8);         // 8% of width
        int timerH = (int) (height * 0.75);        // 75% of height
        int timerFontSize = (int) (height * 0.26); // 35% of height

        // Timer label
        timerLabel = new JLabel("00:00:00");
        timerLabel.setFont(new Font("SansSerif", Font.PLAIN, timerFontSize));
        timerLabel.setForeground(Color.BLACK);
        timerLabel.setBounds(timerX, timerY, timerW, timerH);

        // Timer that ticks every second
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedSeconds++;
                updateTimerLabel();
            }
        });

        add(timerLabel);

        // Button dimensions relative to width and height
        int buttonH = (int) (height * 0.75);
        int buttonW_Small = (int) (width * 0.06);  // for play
        int buttonW_Medium = (int) (width * 0.12); // for Restart
        int buttonW_Large = (int) (width * 0.15);  // for New Game, Load, Save

        // Horizontal positioning
        int baseX = (int) (width * 0.384); // starting point for right-side buttons
        int gap = (int) (width * 0.01); // 1.2% gap between buttons
        int vertY = (int) (height * 0.16);   // vertical centering

        // Icon sizes relative to height
        int iconSize = (int) (buttonH * 0.55); // 55% of panel height

        playIcon = new ImageIcon(
                new ImageIcon(getClass().getResource("/Icons/play.png"))
                        .getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)
        );
        pauseIcon = new ImageIcon(
                new ImageIcon(getClass().getResource("/Icons/pause.png"))
                        .getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)
        );
        restartIcon = new ImageIcon(
                new ImageIcon(getClass().getResource("/Icons/restart.png"))
                        .getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)
        );
        newGameIcon = new ImageIcon(
                new ImageIcon(getClass().getResource("/Icons/newGame.png"))
                        .getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)
        );
        loadIcon = new ImageIcon(
                new ImageIcon(getClass().getResource("/Icons/load.png"))
                        .getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)
        );
        saveIcon = new ImageIcon(
                new ImageIcon(getClass().getResource("/Icons/save.png"))
                        .getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)
        );

        // Create buttons
        playButton = new OperationsButton(null, null, null, null, 0, 0, null, playIcon, iconSize, iconSize, 0, 0, 0);
        restartButton = new OperationsButton(null, new Color(0xEBEBEB), new Color(0xDBDBDB), null, borderRadius, 1, "Restart", restartIcon, iconSize, iconSize,(int)(timerFontSize*0.9), 0, 0);
        newGameButton = new OperationsButton(null, new Color(0xEBEBEB), new Color(0xDBDBDB), null, borderRadius, 1, "New Game", newGameIcon, iconSize, iconSize, (int)(timerFontSize*0.9), 0, 1);
        loadButton = new OperationsButton(null, new Color(0xEBEBEB), new Color(0xDBDBDB), null, borderRadius, 1, "Load Game", loadIcon, iconSize, iconSize, (int)(timerFontSize*0.9), 0, 1);
        saveButton = new OperationsButton(null, new Color(0xEBEBEB), new Color(0xDBDBDB), null, borderRadius, 1, "Save Game", saveIcon, iconSize, iconSize, (int)(timerFontSize*0.9), 0, 0);

        // Set button bounds
        playButton.setBounds((int) (width * 0.02), vertY, buttonW_Small, buttonH);
        restartButton.setBounds(baseX, vertY, buttonW_Medium, buttonH);
        newGameButton.setBounds(baseX + buttonW_Medium + gap, vertY, buttonW_Large, buttonH);
        loadButton.setBounds(baseX + buttonW_Medium + buttonW_Large + 2 * gap, vertY, buttonW_Large, buttonH);
        saveButton.setBounds(baseX + buttonW_Medium + 2 * buttonW_Large + 3 * gap, vertY, buttonW_Large, buttonH);

        // Add buttons
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
        newGameButton.addActionListener(e -> {
            // Prevent spam clicks
            newGameButton.setEnabled(false);

            long now = System.currentTimeMillis();
            Object lastClickObj = newGameButton.getClientProperty("lastClickTime");
            if (lastClickObj != null) {
                long last = (long) lastClickObj;
                if (now - last < 800) {
                    // Update even on spam attempt
                    newGameButton.putClientProperty("lastClickTime", now);
                    newGameButton.setEnabled(true);
                    return;
                }
            }
            newGameButton.putClientProperty("lastClickTime", now);

            // Prevent multiple concurrent workers
            if (Boolean.TRUE.equals(newGameButton.getClientProperty("workerRunning"))) {
                newGameButton.setEnabled(true);
                return;
            }
            newGameButton.putClientProperty("workerRunning", true);

            // Start background Sudoku generation
            SwingWorker<Board, Void> worker = new SwingWorker<>() {
                @Override
                protected Board doInBackground() {
                    // Always fetch latest difficulty state
                    DifficultyButton[] diffButtons = levelPanel.getDifficultyButtons();
                    String[] diffs = levelPanel.getDifficulties();

                    OperatorsPanel.clearSolved();
                    OperatorsPanel.clearCountHints();
                    OperatorsPanel.clearCountValidations();

                    for (int i = 0; i < diffButtons.length; i++) {
                        if (diffButtons[i].isActive()) {
                            return SudokuGenerator.generate(
                                    LevelPanel.mapDifficulty(diffs[i]),
                                    SudokuGenerator.DiggingSequence.RANDOM
                            );
                        }
                    }

                    // Default fallback if none active
                    return SudokuGenerator.generate(
                            SudokuGenerator.Difficulty.EXTREMELY_EASY,
                            SudokuGenerator.DiggingSequence.RANDOM
                    );
                }

                @Override
                protected void done() {
                    try {
                        Board board = get();

                        // Apply new board to UI
                        boardPanel.setBoard(board);
                        if (!boardPanel.isVisible()) boardPanel.setVisible(true);
                        boardPanel.repaint();

                        // Reset operator and timer panels
                        operatorsPanel.updateNumberVisibility();
                        operatorsPanel.clearSolved();
                        operatorsPanel.clearCountValidations();
                        BoardPanel.clearStacks();
                        MenuPanel.resetTimer();

                        // Ensure at least one difficulty is active
                        DifficultyButton[] diffButtons = levelPanel.getDifficultyButtons();
                        boolean anyActive = false;
                        for (DifficultyButton b : diffButtons) {
                            if (b.isActive()) {
                                anyActive = true;
                                break;
                            }
                        }
                        if (!anyActive && diffButtons.length > 0)
                            diffButtons[0].setActive(true);

                        // Refresh level panel
                        levelPanel.revalidate();
                        levelPanel.repaint();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(
                                null,
                                "Failed to generate Sudoku: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    } finally {
                        // Always re-enable the button and clear flag
                        newGameButton.putClientProperty("workerRunning", false);
                        newGameButton.setEnabled(true);
                    }
                }
            };

            worker.execute();
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
                    timer.start();
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