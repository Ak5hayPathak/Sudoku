import javax.swing.*;
import java.awt.*;

public class SudokuFrame extends JFrame {
    SudokuFrame() {
        setTitle("Sudoku");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.80);
        int height = (int) (screenSize.height * 0.85);
        setSize(width, height);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0xF3F3F3));
        setResizable(false);

        // Use absolute positioning
        setLayout(null);

        // Load icon image
        ImageIcon icon = new ImageIcon(getClass().getResource("/Icons/fevicon.png"));

        setIconImage(icon.getImage());


        // Add LevelPanel
        LevelPanel levelPanel = new LevelPanel(width, height);
        add(levelPanel);

        //Add menuPanel
        MenuPanel menuPanel = new MenuPanel(width, height);
        add(menuPanel);

        //Add BoardPanel
        BoardPanel boardPanel = new BoardPanel(width, height);
        add(boardPanel);

        //Add BoardPanel
        OperatorsPanel operatorsPanel = new OperatorsPanel(width, height);
        add(operatorsPanel);

        setVisible(true);
    }

}
