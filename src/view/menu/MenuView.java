package view.menu;

import controller.event.EventBus;
import controller.event.MenuAction;
import config.ConfigManager;
import controller.event.MenuEvent;
import model.world.WorldData;
import model.save.WorldManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Main menu window managing navigation between menu panels.
 * Controls menu state transitions and provides consistent button styling.
 */
public class MenuView extends JFrame {
    /** Panel navigation layout */
    private final CardLayout cardLayout;

    /** Container for all menu panels */
    private final JPanel mainPanel;

    /** Settings configuration panel reference */
    private final SettingsPanel settingsPanel;

    /** Button appearance constants */
    private final Color BUTTON_COLOR = new Color(65, 65, 65);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Font BUTTON_FONT = new Font("Minecraft", Font.BOLD, 20);


    /**
     * Initializes menu window and panels
     * Configures window properties, creates menu panels and config save handler
     */
    public MenuView() {
        // Set window properties
        setTitle("Voxel Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(854, 480);
        setLocationRelativeTo(null);  // Center on screen
        setResizable(false);

        // Initialize layout and container
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create menu panels
        MainMenuPanel mainMenuPanel = new MainMenuPanel(this);
        WorldSelectPanel worldSelectPanel = new WorldSelectPanel(this);
        settingsPanel = new SettingsPanel(this);

        // Add panels to container
        mainPanel.add(mainMenuPanel, "MAIN");
        mainPanel.add(worldSelectPanel, "WORLD_SELECT");
        mainPanel.add(settingsPanel, "SETTINGS");

        setContentPane(mainPanel);

        // Add config save on window close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ConfigManager.saveConfig();  // Save settings before exit
            }
        });
    }

    /**
     * Shows main menu panel
     * Switches card layout to display main menu
     */
    public void showMainMenu() {
        cardLayout.show(mainPanel, "MAIN");
    }

    /**
     * Shows world selection panel
     * Switches card layout to display world select screen
     */
    public void showWorldSelect() {
        cardLayout.show(mainPanel, "WORLD_SELECT");
    }

    /**
     * Shows settings panel and loads current values
     * Switches to settings screen and populates fields
     */
    public void showSettings() {
        cardLayout.show(mainPanel, "SETTINGS");
        settingsPanel.loadCurrentSettings();  // Load current config values
    }

    /**
     * Opens new world creation dialog
     * Creates and shows modal dialog for world setup
     */
    public void showNewWorldDialog() {
        NewWorldDialog dialog = new NewWorldDialog(this);
        dialog.setVisible(true);
    }

    /**
     * Shows world loading dialog or creation prompt
     * Checks for existing worlds and shows appropriate dialog
     */
    public void showLoadWorldDialog() {
        // Get available worlds
        List<WorldData> worlds = WorldManager.getWorlds();

        // Show creation prompt if no worlds exist
        if (worlds.isEmpty()) {
            int choice = JOptionPane.showConfirmDialog(this,
                "No worlds available. Would you like to create a new world?",
                "No Worlds Found",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                // Show world creation dialog
                NewWorldDialog newWorldDialog = new NewWorldDialog(this);
                newWorldDialog.setVisible(true);
            }
        } else {
            // Show world selection dialog
            WorldListDialog dialog = new WorldListDialog(this);
            dialog.setVisible(true);
        }
    }

    /**
     * Saves current settings configuration
     * Triggers settings panel to save values
     */
    public void saveSettings() {
        settingsPanel.saveSettings();
    }

    /**
     * Creates styled menu button with given text and action
     *
     * @param text Button label text
     * @param action Menu action to trigger on click
     * @return Configured button with custom appearance
     */
    public JButton createStyledButton(String text, MenuAction action) {
        // Create button with custom painting
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                // Enable antialiasing
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Set button color based on state
                if (getModel().isPressed()) {
                    g2d.setColor(BUTTON_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(BUTTON_COLOR.brighter());
                } else {
                    g2d.setColor(BUTTON_COLOR);
                }

                // Draw rounded button background
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Draw centered text
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(BUTTON_FONT);

                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };

        // Configure button properties
        button.setPreferredSize(new Dimension(200, 40));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add action handler
        button.addActionListener(e -> EventBus.getInstance().post(MenuEvent.action(action)));

        return button;
    }
}