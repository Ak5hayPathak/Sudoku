public class Main {
    public static void main(String[] args) {


        System.out.println("Difficulty: Evil");
        Board b = SudokuGenerator.generate(SudokuGenerator.Difficulty.EVIL, SudokuGenerator.DiggingSequence.RANDOM);
        b.printBoard();

//        System.out.println("\nSolution");
//        b.solveSudoku();
//        b.printBoard();

    }
}