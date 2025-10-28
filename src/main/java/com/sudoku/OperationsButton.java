package com.sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OperationsButton extends JButton {

    private Color defaultColor;// = Color.WHITE;
    private Color hoverColor;// = new Color(0xEBEBEB);
    private Color activeColor = new Color(0xDBDBDB);
    private Color borderColor;// = new Color(0xDBDBDB);
    private int borderRadius;// = 10;
    private int borderThickness;// = 2;
    private int paddingBetweenImageAndText;
    private int contentAlignment;
    private boolean toggleButton;
    private boolean active = false;
    private int iconheight = 20;
    private int iconWidth = 20;


    public OperationsButton(String text, ImageIcon icon, int textSize, int paddingBetweenImageAndText, int contentAlignment) {
        this(Color.WHITE, new Color(0xEBEBEB), new Color(0xDBDBDB),  new Color(0xDBDBDB), 10, 2, text, icon, 20, 20, textSize, paddingBetweenImageAndText, contentAlignment);
    }
    public OperationsButton(Color activeColor, String text, ImageIcon icon, int iconheight, int iconWidth, int textSize, int paddingBetweenImageAndText, int contentAlignment) {
        this(Color.WHITE, new Color(0xEBEBEB), activeColor, new Color(0xDBDBDB), 10, 2, text, icon, iconheight, iconWidth, textSize, paddingBetweenImageAndText, contentAlignment);
    }

    public OperationsButton(Color defaultColor, Color hoverColor, Color activeColor, Color borderColor, int borderRadius, int borderThickness, String text, ImageIcon icon, int iconheight, int iconWidth, int textSize, int paddingBetweenImageAndText, int contentAlignment){
        super(text);

        this.defaultColor = (defaultColor != null) ? defaultColor : new Color(0,0,0,0); // transparent
        this.hoverColor   = (hoverColor != null)   ? hoverColor   : new Color(0,0,0,0);
        this.activeColor  = (activeColor != null)  ? activeColor  : new Color(0,0,0,0);
        this.borderColor  = (borderColor != null)  ? borderColor  : new Color(0,0,0,0);
        this.borderRadius = borderRadius;
        this.borderThickness = borderThickness;
        this.paddingBetweenImageAndText = paddingBetweenImageAndText; //To maintain the spaces between the icon and the text
        this.contentAlignment = contentAlignment; //To maintain the alignment of the icon and the text in the button

        //Scale the icon (e.g., 20x20)
        if (icon != null) {
            Image img = icon.getImage().getScaledInstance(iconheight, iconWidth, Image.SCALE_SMOOTH);
            setIcon(new ImageIcon(img));
        }

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setForeground(Color.BLACK);
        setFont(new Font("Poppins", Font.PLAIN, textSize));
        setHorizontalAlignment(SwingConstants.CENTER);

        setPreferredSize(new Dimension(220, 60));

        // Effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if(!active) {
                    setBackground(hoverColor);
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(!active) {
                    setBackground(defaultColor);
                    repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(!active) {
                    setBackground(activeColor); // click color
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(toggleButton) {
                    setActive(!isActive(), activeColor); // toggles active state and sets background properly
                } else if(!active) {
                    setBackground(hoverColor); // restore hover if still inactive
                }
                repaint();
            }
        });

        setBackground(defaultColor); // initial background
    }

    public void setToggle(boolean toggle){
        this.toggleButton = toggle;
    }

    public boolean getToggle(){
        return this.toggleButton;
    }

    public void setActive(boolean active, Color activeColor) {
        this.active = active;
        this.activeColor = activeColor;
        setBackground(active ? activeColor : defaultColor);
        repaint();
    }

    public void setActiveColor(Color activeColor) {
        this.activeColor = activeColor;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), borderRadius, borderRadius);

        // Draw border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(borderThickness));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, borderRadius, borderRadius);

        // Draw icon and text
        int padding = paddingBetweenImageAndText;
        int iconWidth = 0;
        int iconHeight = 0;

        int shiftLeft = contentAlignment; // shift everything slightly left

        if (getIcon() != null) {
            Icon icon = getIcon();
            iconWidth = icon.getIconWidth();
            iconHeight = icon.getIconHeight();
            int yIcon = (getHeight() - iconHeight) / 2;
            int xIcon = (getWidth() - (iconWidth + padding + g2.getFontMetrics().stringWidth(getText()))) / 2 - shiftLeft;
            icon.paintIcon(this, g2, xIcon, yIcon);
        }

        // Draw text
        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(getText());
        int textHeight = fm.getAscent();
        int xText = (getWidth() - (iconWidth + padding + textWidth)) / 2 + iconWidth + padding - shiftLeft;
        int yText = (getHeight() + textHeight) / 2 - 3;
        g2.drawString(getText(), xText, yText);

        g2.dispose();
    }

}
