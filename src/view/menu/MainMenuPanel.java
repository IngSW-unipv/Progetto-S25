package view.menu;

import controller.event.MenuAction;

import javax.swing.*;
import java.awt.*;

/**
 * Main menu panel providing access to core game functions.
 * Displays title and primary navigation buttons with gradient background.
 */
public class MainMenuPanel extends JPanel {
    /** Reference to parent menu view */
    private final MenuView menuView;

    /** UI Colors */
    private static final Color BG_COLOR_START = new Color(24, 20, 37);
    private static final Color BG_COLOR_END = new Color(65, 41, 90);


    /**
     * Creates main menu with buttons and styling
     */
    public MainMenuPanel(MenuView menuView) {
        this.menuView = menuView;
        setLayout(new GridBagLayout());
        setBackground(BG_COLOR_START);
        setupComponents();
    }

    /**
     * Configures and adds all UI components
     */
    private void setupComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Add title
        addTitle(gbc);
        add(Box.createVerticalStrut(50), gbc);

        // Add navigation buttons
        addNavigationButtons(gbc);
    }

    private void addTitle(GridBagConstraints gbc) {
        JLabel titleLabel = new JLabel("Voxel Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Minecraft", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, gbc);
    }

    private void addNavigationButtons(GridBagConstraints gbc) {
        JButton playButton = menuView.createStyledButton("Play",
                MenuAction.SHOW_WORLD_SELECT);
        JButton settingsButton = menuView.createStyledButton("Settings",
                MenuAction.SHOW_SETTINGS);
        JButton quitButton = menuView.createStyledButton("Quit",
                MenuAction.QUIT_GAME);

        add(playButton, gbc);
        add(Box.createVerticalStrut(10), gbc);
        add(settingsButton, gbc);
        add(Box.createVerticalStrut(10), gbc);
        add(quitButton, gbc);
    }

    /**
     * Renders gradient background
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        GradientPaint gradient = new GradientPaint(
            0, 0, BG_COLOR_START,
            0, getHeight(), BG_COLOR_END
        );

        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}