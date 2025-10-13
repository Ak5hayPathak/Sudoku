import javax.swing.*;
import java.awt.*;

public class LevelPanel extends JPanel {
    private int cornerRadius = 10; // border radius

    public LevelPanel(int frameWidth, int frameHeight) {
        // Background color
        setBackground(new Color(0xF9F9F9));

        setLayout(null); // <-- important for absolute positioning

        // Opaque false for custom painting
        setOpaque(false);

        // Set size dynamically based on frame size
        int width = (int) (frameWidth * 0.19);   // 19% of frame width
        int height = (int) (frameHeight * 0.908); //91% of frame height
        setBounds(10, 10, width, height);        // x, y, width, height

        // Create a list to store all buttons for toggling
        java.util.List<DifficultyButton> buttonList = new java.util.ArrayList<>();

        String[] difficulties = {"Extremely Easy", "Easy", "Medium", "Hard", "Evil"};
        String[] leftIcons = {"child.png", "easy.png", "medium.png", "hard.png", "evil.png"};
        int[] stars = {1, 2, 3, 4, 5};

        // Load star icon once
        ImageIcon starIcon = new ImageIcon(getClass().getResource("/Icons/star.png"));

        int spacing = 10;
        int currentY = 20;

        for (int i = 0; i < difficulties.length; i++) {
            ImageIcon leftIcon = new ImageIcon(getClass().getResource("/Icons/" + leftIcons[i]));
            DifficultyButton btn = new DifficultyButton(difficulties[i], new Color(0xF9F9F9), leftIcon, stars[i], starIcon);

            // Add click listener to make this button active and deactivate others
            btn.addActionListener(e -> {
                for (DifficultyButton b : buttonList) {
                    b.setActive(false); // deactivate all buttons
                }
                btn.setActive(true); // activate clicked button
            });

            btn.setRelativeBounds(width, height, 0.95, 0.07, currentY);
            add(btn);
            buttonList.add(btn);

            currentY += (int) (height * 0.07) + spacing;
        }

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
