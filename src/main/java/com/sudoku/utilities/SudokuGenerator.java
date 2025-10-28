package com.sudoku.utilities;

import java.util.*;

public final class SudokuGenerator {

    private static final int SIZE = 9;
    private static Random rand = new Random();

    private SudokuGenerator() {
        throw new UnsupportedOperationException("Cannot instantiate this class");
    }

    public enum DiggingSequence {
        LEFT_TO_RIGHT, S_SHAPE, JUMP_ONE, RANDOM
    }

    public enum Difficulty {
        EXTREMELY_EASY, EASY, MEDIUM, DIFFICULT, EVIL
    }

    public static Board generate(Difficulty difficulty, DiggingSequence sequence) {
        Board board = null;
        long startTime = System.currentTimeMillis();
        long timeLimit = 1000; // 1 second

        while (board == null) {
            board = digHoles(sequence, difficulty);

            if (board != null) {
                boolean valid = true;

                for (int i = 0; i < SIZE; i++) {
                    for (int j = 0; j < SIZE; j++) {
                        if (board.getCell(i, j).getValue() != 0)
                            board.getCell(i, j).setFixed(true);
                    }
                }

            }

            // Check time limit to prevent infinite loop
            if (System.currentTimeMillis() - startTime > timeLimit) {
                board = null; // reset to retry
                startTime = System.currentTimeMillis(); // reset timer
            }

        }

        return board;
    }


    private static Board randomFill() {
        int row, col, val;
        int fillCells = 11;
        Board board = new Board();
        while (fillCells > 0) {
            row = rand.nextInt(9);
            col = rand.nextInt(9);
            val = rand.nextInt(1, SIZE + 1);
            if (board.getCell(row, col).getValue() == 0) {
                while (!board.isSafe(row, col, val)) val = rand.nextInt(1, SIZE + 1);
                board.getCell(row, col).setValue(val);
                fillCells--;
            }
        }
        return board;
    }


    private static Board generateTerminalPattern(int maxAttempts, int timeLimitMillis) {

        Board board = new Board();

        for (int attempts = 1; attempts <= maxAttempts; attempts++) {
            board = randomFill();
            long start = System.currentTimeMillis();

            if (board.solveSudokuRandom() && (System.currentTimeMillis() - start) <= timeLimitMillis) {
                return board;
            }
        }

        throw new RuntimeException("Failed after " + maxAttempts + " attempts.");
    }

    private static Board digHoles(DiggingSequence sequence, Difficulty difficulty){

        Board board = generateTerminalPattern(1000, 100);

        boolean[][] canBeDug = new boolean[SIZE][SIZE];
        for(int i=0; i<SIZE; i++)
            Arrays.fill(canBeDug[i], true);

        List<int[]> cellSequence = generateCellSequence(sequence);

        for(int[] cell: cellSequence){
            int r = cell[0], c = cell[1];

            if(!canBeDug[r][c]) continue;

            int temp = board.getCell(r, c).getValue();
            board.getCell(r, c).setValue(0);


            if (!checkRestrictions(board, difficulty) || !checkUniqueness(board, r, c, temp)) {
                board.getCell(r, c).setValue(temp);
                canBeDug[r][c] = false;  // prune
            }

        }
        return board;
    }

    private static List<int[]> generateCellSequence(DiggingSequence sequence){
        List<int[]> cells = new ArrayList<>();

        for(int i = 0; i < SIZE; i++)
            for(int j = 0; j < SIZE; j++)
                cells.add(new int[]{i, j});

        switch (sequence) {
            case LEFT_TO_RIGHT -> {} // default
            case S_SHAPE -> cells.sort((a, b) -> (a[0] % 2 == 0 ? a[1] : SIZE - a[1]) - (b[0] % 2 == 0 ? b[1] : SIZE - b[1]));
            case JUMP_ONE -> cells.sort(Comparator.comparingInt(a -> (a[0] + a[1]) % 2));
            case RANDOM -> Collections.shuffle(cells);
        }
        return cells;
    }

    private static boolean checkRestrictions(Board board, Difficulty difficulty){
        int totalGivens = 0;

        // Random bounds for this puzzle
        int minTotal = getRandomTotalGivens(difficulty);
        int minRowCol = getRandomRowColGivens(difficulty);

        for(int i=0; i<SIZE; i++){
            int rowCount = 0, colCount = 0;
            for(int j=0; j<SIZE; j++){
                if(board.getCell(i, j).getValue() != 0) rowCount++;
                if(board.getCell(j, i).getValue() != 0) colCount++;
            }


            if (rowCount < minRowCol || colCount < minRowCol)
                return false;

            totalGivens += rowCount;
        }

        return totalGivens >= minTotal;
    }

    private static boolean checkUniqueness(Board board, int r, int c, int originalVal) {
        for (int i = 1; i <= 9; i++) {
            if (i == originalVal) continue;
            if (!board.isSafe(r, c, i)) continue;

            Board copy = new Board(board);

            copy.getCell(r, c).setValue(i);


            if (copy.solveSudoku()) {
                return false;
            }
        }

        return true;
    }

    private static int getRandomTotalGivens(Difficulty difficulty) {
        Random rand = new Random();
        return switch (difficulty) {
            case EXTREMELY_EASY -> 40 + rand.nextInt(6); // 40–45
            case EASY -> 34 + rand.nextInt(5);          // 34–38
            case MEDIUM -> 30 + rand.nextInt(4);        // 30–33
            case DIFFICULT -> 26 + rand.nextInt(4);     // 26–29
            default -> 18 + rand.nextInt(5);            // 18–23 (Evil)
        };
    }

    private static  int getRandomRowColGivens(Difficulty difficulty) {
        Random rand = new Random();
        int upper = switch (difficulty) {
            case EXTREMELY_EASY -> 4;
            case EASY -> 3;
            case MEDIUM-> 2;
            case DIFFICULT -> 1;
            default -> 1; // Evil level, min 1
        };
        return rand.nextInt(upper); // random between 0 and upper
    }

    public static void main(String[] args) {
        Board b = generate(Difficulty.EXTREMELY_EASY, DiggingSequence.RANDOM);
        b.printBoard();
    }
}
