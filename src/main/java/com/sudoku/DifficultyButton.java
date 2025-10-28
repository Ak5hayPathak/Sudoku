package com.sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DifficultyButton extends JButton {
    private Color defaultColor;
    private Color hoverColor;
    private int cornerRadius = 10;

    private ImageIcon leftIcon;   // left-side icon
    private int stars = 0;        // number of stars on right
    private ImageIcon starIcon;   // star icon for right-side

    private boolean active = false; // track active/inactive state

    public DifficultyButton(String text, Color panelColor, ImageIcon leftIcon, int stars, ImageIcon starIcon) {
        super(text);

        this.defaultColor = panelColor;
        this.hoverColor = new Color(0xE0E0E0);
        this.leftIcon = leftIcon;
        this.stars = stars;
        this.starIcon = starIcon;

        setBackground(defaultColor);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setForeground(Color.BLACK);
        setFont(new Font("Poppins", Font.PLAIN, 14));
        setHorizontalAlignment(SwingConstants.LEFT);

        // Hover and press effects + toggle active state
        // Inside DifficultyButton constructor
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!active) setBackground(hoverColor);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!active) setBackground(defaultColor);
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!active) setBackground(hoverColor.darker());
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // REMOVE internal toggle!
                // do nothing here; external ActionListener will control activation
                if (!active) setBackground(hoverColor);
                repaint();
            }
        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int padding = 10;

        // Draw rounded background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);

        // Draw left icon
        int iconSize = (int) (h - 1.5 * padding);
        int iconX = padding;
        int iconY = (h - iconSize) / 2;
        if (leftIcon != null) {
            g2.drawImage(leftIcon.getImage(), iconX, iconY, 30, 30, null);
        }

        // Draw text
        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();
        int textX = padding + (leftIcon != null ? iconSize + 10 : 0);
        int textY = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), textX, textY);

        // Draw stars on right
        if (starIcon != null) {
            int starSize = iconSize - 12;
            int startX = w - padding - starSize * stars;
            int starY = (h - starSize) / 2;
            for (int i = 0; i < stars; i++) {
                int starX = startX + starSize * i;
                g2.drawImage(starIcon.getImage(), starX, starY, starSize, starSize, null);
            }
        }

        g2.dispose();
    }

    public void setRelativeBounds(int panelWidth, int panelHeight, double widthPercent, double heightPercent, int yOffset) {
        int w = (int) (panelWidth * widthPercent);
        int h = (int) (panelHeight * heightPercent);
        int x = (panelWidth - w) / 2;
        setBounds(x, yOffset, w, h);
    }

    // Optional: programmatically set active state
    public void setActive(boolean active) {
        this.active = active;
        setBackground(active ? hoverColor : defaultColor);
        repaint();
    }

    public boolean isActive() {
        return active;
    }
}
