package menu.view;

import controller.event.MenuAction;
import menu.view.MenuView;

import javax.swing.*;
import java.awt.*;


public class MainMenuPanel extends JPanel {
    private final MenuView menuView;

    public MainMenuPanel(MenuView menuView) {
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

        JLabel titleLabel = new JLabel("Voxel Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Minecraft", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, gbc);

        add(Box.createVerticalStrut(50), gbc);

        JButton playButton = menuView.createStyledButton("Play", MenuAction.SHOW_WORLD_SELECT);
        JButton settingsButton = menuView.createStyledButton("Settings", MenuAction.SHOW_SETTINGS);
        JButton quitButton = menuView.createStyledButton("Quit", MenuAction.QUIT_GAME);

        add(playButton, gbc);
        add(Box.createVerticalStrut(10), gbc);
        add(settingsButton, gbc);
        add(Box.createVerticalStrut(10), gbc);
        add(quitButton, gbc);
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