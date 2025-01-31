package view.menu;

import controller.event.MenuAction;

import javax.swing.*;
import java.awt.*;

/**
 * World selection panel for creating or loading game worlds.
 * Provides buttons for world management with gradient background.
 */
public class WorldSelectPanel extends JPanel {
    /** Reference to parent menu view */
    private final MenuView menuView;

    /** UI Colors */
    private static final Color BG_COLOR_START = new Color(24, 20, 37);
    private static final Color BG_COLOR_END = new Color(65, 41, 90);


    /**
     * Creates world select screen with buttons and styling
     */
    public WorldSelectPanel(MenuView menuView) {
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

        // Add world management buttons
        addWorldButtons(gbc);
    }

    private void addTitle(GridBagConstraints gbc) {
        JLabel titleLabel = new JLabel("Select World", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Minecraft", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, gbc);
    }

    private void addWorldButtons(GridBagConstraints gbc) {
        JButton newWorldButton = menuView.createStyledButton("New World", MenuAction.SHOW_NEW_WORLD_DIALOG);
        JButton loadWorldButton = menuView.createStyledButton("Load World", MenuAction.SHOW_LOAD_WORLD_DIALOG);
        JButton backButton = menuView.createStyledButton("Back", MenuAction.BACK_TO_MAIN);

        add(newWorldButton, gbc);
        add(Box.createVerticalStrut(10), gbc);
        add(loadWorldButton, gbc);
        add(Box.createVerticalStrut(10), gbc);
        add(backButton, gbc);
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