import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SudokuFrame::new);

        //SudokuGenerator.generate(SudokuGenerator.Difficulty.EVIL, SudokuGenerator.DiggingSequence.RANDOM).printBoard();
    }
}