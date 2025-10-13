import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel{

    private int cornerRadius = 10; // border radius

    public MenuPanel(int frameWidth, int frameHeight){

        // Background color
        setBackground(new Color(0xF9F9F9));

        setLayout(null); // <-- important for absolute positioning

        // Opaque false for custom painting
        setOpaque(false);

        // Set size dynamically based on frame size
        int width = (int) (frameWidth * 0.775);   // 75% of frame width
        int height = (int) (frameHeight * 0.10); //12% of frame height
        setBounds(222, 10, width, height);        // x, y, width, height

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


