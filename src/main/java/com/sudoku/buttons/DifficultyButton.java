package com.sudoku.buttons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DifficultyButton extends JButton {

    private int buttonHeight;
    private int buttonWidth;

    private Color defaultColor;
    private Color hoverColor;
    private int cornerRadius = 10;

    private ImageIcon leftIcon;   // level icon
    private int stars = 0;        // number of stars on right
    private ImageIcon starIcon;   // star icon on the right side

    private boolean active = false; // tracking active/inactive state

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

        int w = this.buttonWidth;
        int h = this.buttonHeight;
        int padding = Math.max(5, (int) (0.05 * Math.min(w, h))); // 5% padding minimum 5px

        // Rounded background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);

        // Left Icon
        int iconSize = (int) (0.75 * h); // 70% of button height
        int iconX = padding;
        int iconY = (h - iconSize) / 2;
        if (leftIcon != null) {
            g2.drawImage(leftIcon.getImage(), iconX, iconY, iconSize, iconSize, null);
        }

        // Font scaled relative to height
        float fontSize = 0.36f * h; // 36% of height
        g2.setFont(getFont().deriveFont(fontSize));
        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();

        // Text position (after icon)
        int textX = padding + (leftIcon != null ? iconSize + (int) (0.05 * w) : 0);
        int textY = (h - fm.getHeight()) / 2 + fm.getAscent();

        // Prevent text from overlapping stars
        String text = getText() != null ? getText() : "";
        g2.drawString(text, textX, textY);

        // Right-side stars
        if (starIcon != null && stars > 0) {
            int starSize = (int) (0.38 * h); // 38% of button height
            int totalStarsWidth = stars * starSize + (stars - 1) * (int) (0.015 * w);
            int startX = w - padding - totalStarsWidth;
            int starY = (h - starSize) / 2;

            for (int i = 0; i < stars; i++) {
                int starX = startX + i * (starSize + (int) (0.015 * w));
                g2.drawImage(starIcon.getImage(), starX, starY, starSize, starSize, null);
            }
        }

        g2.dispose();
    }

    public void setRelativeBounds(int panelWidth, int panelHeight, double widthPercent, double heightPercent, int yOffset) {

        this.buttonWidth = (int) (panelWidth * widthPercent);
        this.buttonHeight = (int) (panelHeight * heightPercent);
        int x = (panelWidth - this.buttonWidth) / 2;
        setBounds(x, yOffset, this.buttonWidth, this.buttonHeight);
    }

    // programmatically set active state
    public void setActive(boolean active) {
        this.active = active;
        setBackground(active ? hoverColor : defaultColor);
        repaint();
    }

    public boolean isActive() {
        return active;
    }
}