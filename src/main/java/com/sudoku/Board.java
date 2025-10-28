package com.sudoku;

import java.util.Random;

public class Board {

    private Cell[][] grid = new Cell[9][9];
    private int []getNumCount = new int[9];

    public Board(){
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                grid[i][j] = new Cell(0, false);
            }
        }
    }

    public Board(Board other) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int value = other.grid[i][j].getValue();
                boolean fixed = other.grid[i][j].isFixed();
                this.grid[i][j] = new Cell(value, fixed); // deep copy
            }
        }
    }

    public void refreshBoard(){
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(grid[i][j].isFixed()) continue;
                grid[i][j].setValue(0);
                grid[i][j].clearCandidates();
                grid[i][j].setWrong(false);
                grid[i][j].setGuessed(false);
                grid[i][j].setCertain(false);
            }
        }
    }
    public Cell getCell(int i, int j){
        return grid[i][j];
    }

    public void setCell(int i, int j, Cell cell){
        this.grid[i][j] = cell;
    }

    public void setBoard(int[][] arr){
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                this.grid[i][j].setValue(arr[i][j]);
            }
        }
    }

    public void printBoard() {
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0 && i != 0) {
                System.out.println("------+-------+------"); // horizontal separator
            }

            for (int j = 0; j < 9; j++) {
                if (j % 3 == 0 && j != 0) {
                    System.out.print("| "); // vertical separator
                }

                int val = grid[i][j].getValue();
                System.out.print((val == 0 ? "_" : val) + " ");
            }
            System.out.println();
        }
    }

    public boolean isEmpty(){
        boolean isEmpty = true;

        for(int i = 0; i<9; i++){
            for(int j=0; j<9; j++){
                if(this.grid[i][j].getValue() != 0){
                    isEmpty = false;
                    break;
                }
            }
        }

        return isEmpty;
    }

    public boolean isCorrect(int row, int col) {
        int val = grid[row][col].getValue();
        if (val == 0) {
            grid[row][col].setWrong(false);
            return true; // empty cell is always safe
        }

        grid[row][col].setValue(0);
        boolean safe = isSafe(row, col, val);
        grid[row][col].setValue(val);
        grid[row][col].setWrong(!safe);
        return safe;
    }

    public boolean isSafe(int row, int col, int val){

        //row and col check
        for(int i=0; i<9; i++){

            if(this.grid[row][i].getValue() == val || this.grid[i][col].getValue() == val)
                return false;
        }

        //3x3 box check
        int startRow = row - row%3;
        int startCol = col - col%3;

        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                if(grid[startRow + i][startCol + j].getValue() == val)
                    return false;

            }
        }
        return true;
    }

    //Fisher–Yates shuffle for shuffling an array to randomly choose a number to fill the sudoku cell
    private int[] getShuffledDigits(){

        int []digits = {1, 2, 3, 4, 5, 6, 7, 8, 9};

        Random rand = new Random();

        for(int i = digits.length - 1; i >= 0; i--){
            int j = rand.nextInt(i+1);
            int temp = digits[i];
            digits[i] = digits[j];
            digits[j] = temp;

        }
        return digits;
    }

    public boolean solveSudokuRandom() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j].getValue() == 0) {

                    int[] digits = getShuffledDigits(); // get a random order of digits

                    for (int val : digits) {
                        if (isSafe(i, j, val)) {
                            grid[i][j].setValue(val);
                            if (solveSudokuRandom()) return true;
                            grid[i][j].setValue(0);
                        }
                    }

                    return false; // no number fits
                }
            }
        }
        return true; // all cells filled
    }


    public boolean solveSudoku(){

        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                if(grid[i][j].getValue() == 0){
                    for(int k=1; k<=9; k++){
                        if(this.isSafe(i, j, k)){
                            grid[i][j].setValue(k);
                            if(solveSudoku()) return true;
                            grid[i][j].setValue(0);
                        }
                    }
                    return false;
                }

            }
        }
        return true;
    }

    public boolean isComplete() {
        // Check all cells are filled
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (grid[row][col].getValue() == 0)
                    return false;
            }
        }

        // Check all rows, columns, and boxes are valid
        return isValid();
    }

    public boolean isValid() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = grid[row][col].getValue();
                if (value != 0) {              // skip empty cells
                    grid[row][col].setValue(0); // temporarily empty
                    if (!isSafe(row, col, value)) {
                        grid[row][col].setValue(value); // restore
                        return false; // violation found
                    }
                    grid[row][col].setValue(value); // restore
                }
            }
        }
        return true; // all cells are valid
    }

    public void resetBoard(){
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                grid[i][j] = new Cell(0, false);
            }
        }
    }

    public int[] getNumCountArray(){
        int count=0;
        for(int num = 1; num<10; num++){
            for(int i=0; i<9; i++){
                for(int j=0; j<9; j++){
                    if(grid[i][j].getValue() == num){
                        count++;
                    }
                }
            }
            getNumCount[num-1] = count;
            count=0;
        }
        return getNumCount;
    }

    public int getNumCount(int num){
        int count = 0;
        if(num > 9 || num < 1) return -1;
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                if(grid[i][j].getValue() == num){
                    count++;
                }
            }
        }
        return count;
    }

}