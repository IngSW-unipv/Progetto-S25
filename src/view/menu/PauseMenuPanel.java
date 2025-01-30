package view.menu;

import controller.event.MenuAction;
import javax.swing.*;
import java.awt.*;

public class PauseMenuPanel extends JFrame {
    private final Color BUTTON_COLOR = new Color(65, 65, 65);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Font TITLE_FONT = new Font("Minecraft", Font.BOLD, 48);
    private final Font BUTTON_FONT = new Font("Minecraft", Font.BOLD, 20);

    public PauseMenuPanel() {
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 150));
        setLayout(new GridBagLayout());
        setupComponents();
        setFocusable(false);
    }

    private void setupComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel titleLabel = new JLabel("Game Paused", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        add(titleLabel, gbc);

        add(Box.createVerticalStrut(50), gbc);

        addButton("Resume", MenuAction.RESUME_GAME, gbc);
        addButton("Settings", MenuAction.SHOW_SETTINGS, gbc);
        addButton("Save and Quit", MenuAction.QUIT_GAME, gbc);

        setOpacity(0.9f);
    }

    private void addButton(String text, MenuAction action, GridBagConstraints gbc) {
        JButton button = createStyledButton(text, action);
        add(button, gbc);
        add(Box.createVerticalStrut(10), gbc);
    }

    private JButton createStyledButton(String text, MenuAction action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(BUTTON_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(BUTTON_COLOR.brighter());
                } else {
                    g2d.setColor(BUTTON_COLOR);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(BUTTON_FONT);

                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };

        button.setPreferredSize(new Dimension(200, 40));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> controller.event.EventBus.getInstance().post(new controller.event.MenuActionEvent(action)));

        return button;
    }
}