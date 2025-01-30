package view.menu;

import controller.event.MenuAction;
import view.menu.MenuView;

import javax.swing.*;
import java.awt.*;

public class WorldSelectPanel extends JPanel {
    private final MenuView menuView;

    public WorldSelectPanel(MenuView menuView) {
        this.menuView = menuView;
        setLayout(new GridBagLayout());
        setBackground(new Color(24, 20, 37));
        setupComponents();
    }

    private void setupComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel titleLabel = new JLabel("Select World", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Minecraft", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, gbc);

        add(Box.createVerticalStrut(50), gbc);

        JButton newWorldButton = menuView.createStyledButton("New World", MenuAction.SHOW_NEW_WORLD_DIALOG);
        JButton loadWorldButton = menuView.createStyledButton("Load World", MenuAction.SHOW_LOAD_WORLD_DIALOG);
        JButton backButton = menuView.createStyledButton("Back", MenuAction.BACK_TO_MAIN);

        add(newWorldButton, gbc);
        add(Box.createVerticalStrut(10), gbc);
        add(loadWorldButton, gbc);
        add(Box.createVerticalStrut(10), gbc);
        add(backButton, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(24, 20, 37),
                0, getHeight(), new Color(65, 41, 90)
        );

        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}