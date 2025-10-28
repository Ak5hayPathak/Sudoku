package com.sudoku;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class SaveNLoad {

    // === SAVE GAME ===
    public static void saveGameToJSON(Board board, String difficulty,
                                      boolean isCompleted, int timeElapsed,
                                      int hints, int validations, boolean solveHelp) {

        JSONObject gameData = new JSONObject();
        gameData.put("difficulty", difficulty);
        gameData.put("isCompleted", isCompleted);
        gameData.put("timeElapsed", timeElapsed);
        gameData.put("hints", hints);
        gameData.put("validations", validations);
        gameData.put("solveHelp", solveHelp);

        JSONArray boardArray = new JSONArray();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                Cell cell = board.getCell(row, col);
                JSONObject cellObj = new JSONObject();

                cellObj.put("value", cell.getValue());
                cellObj.put("isFixed", cell.isFixed());
                cellObj.put("isWrong", cell.isWrong());
                cellObj.put("isGuessed", cell.isGuessed());
                cellObj.put("isCertain", cell.isCertain());
                cellObj.put("candidates",
                        (cell.getCandidates() != null && !cell.getCandidates().isEmpty())
                                ? new JSONArray(cell.getCandidates())
                                : JSONObject.NULL);

                boardArray.put(cellObj);
            }
        }
        gameData.put("board", boardArray);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Sudoku Game");
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().endsWith(".json")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".json");
            }

            try (Writer writer = new OutputStreamWriter(
                    new FileOutputStream(fileToSave), StandardCharsets.UTF_8)) {

                writer.write(gameData.toString(4));
                JOptionPane.showMessageDialog(null, "Game saved successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error saving file: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // === LOAD GAME ===
    public static JSONObject loadGameFromJSON() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Sudoku Game");

        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection != JFileChooser.APPROVE_OPTION) return null;

        File fileToOpen = fileChooser.getSelectedFile();
        if (fileToOpen == null || !fileToOpen.exists()) {
            JOptionPane.showMessageDialog(null, "File not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileToOpen), StandardCharsets.UTF_8))) {

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) content.append(line);

            JSONObject data = new JSONObject(content.toString().trim());

            if (!isValidSudokuSave(data)) {
                JOptionPane.showMessageDialog(null, "Invalid Sudoku save file structure.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            return data;

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error reading file: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Invalid JSON format: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return null;
    }

    private static boolean isValidSudokuSave(JSONObject data) {
        if (data == null) return false;

        try {
            String[] requiredKeys = {
                    "difficulty", "isCompleted", "timeElapsed",
                    "hints", "validations", "solveHelp", "board"
            };

            for (String key : requiredKeys)
                if (!data.has(key)) return false;

            if (!(data.opt("difficulty") instanceof String)) return false;
            if (!(data.opt("isCompleted") instanceof Boolean)) return false;
            if (!(data.opt("timeElapsed") instanceof Number)) return false;
            if (!(data.opt("hints") instanceof Number)) return false;
            if (!(data.opt("validations") instanceof Number)) return false;
            if (!(data.opt("solveHelp") instanceof Boolean)) return false;

            JSONArray board = data.optJSONArray("board");
            if (board == null || board.length() != 81) return false;

            for (int i = 0; i < board.length(); i++) {
                JSONObject cell = board.optJSONObject(i);
                if (cell == null) return false;
                if (!cell.has("value") || !cell.has("isFixed")) return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
