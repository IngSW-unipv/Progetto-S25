package menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuView extends JFrame {

    private JPanel mainPanel;                                                           // The main panel displaying the main menu options
    private JPanel worldSelectPanel;                                                    // The panel displaying the world selection options
    private SettingsPanel settingsPanel;                                                // The settings panel for adjusting application settings
    private MenuController controller;                                                  // The controller that handles interactions between the view and the model
    private final Color BUTTON_COLOR = new Color(65, 65, 65);                   // The color used for buttons in their default state
    private final Color BUTTON_HOVER_COLOR = new Color(96, 96, 96);             // The color used for buttons when hovered over
    private final Color TEXT_COLOR = new Color(255, 255, 255);                  // The color used for button text
    private final Font TITLE_FONT = new Font("Minecraft", Font.BOLD, 48);     // The font used for the title of the menu
    private final Font BUTTON_FONT = new Font("Minecraft", Font.BOLD, 20);    // The font used for the text on buttons

    /**
     * Constructs a MenuView instance, setting up the window and initializing the panels.
     */
    public MenuView() {
        setTitle("Voxel Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(854, 480);
        setLocationRelativeTo(null);
        setResizable(false);
        initializePanels();
    }

    /**
     * Creates a styled button with custom appearance and hover/press effects.
     *
     * @param text The text displayed on the button.
     * @return A styled JButton.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Set button color based on its state (pressed, hovered, or default)
                if (getModel().isPressed()) {
                    g2.setColor(BUTTON_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(BUTTON_HOVER_COLOR);
                } else {
                    g2.setColor(BUTTON_COLOR);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Draw border
                g2.setColor(Color.BLACK);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);

                // Draw text
                FontMetrics metrics = g2.getFontMetrics(getFont());
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

                g2.setColor(TEXT_COLOR);
                g2.setFont(getFont());
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setPreferredSize(new Dimension(200, 40));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    /**
     * Initializes the panels for different menu states (main menu, world selection, settings).
     */
    private void initializePanels() {
        settingsPanel = new SettingsPanel(controller);

        // Main Menu Panel
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(24, 20, 37),
                        0, getHeight(), new Color(65, 41, 90));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Title
                g2d.setFont(TITLE_FONT);
                g2d.setColor(TEXT_COLOR);
                String title = "Voxel Game";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(title)) / 2;
                g2d.drawString(title, x, 100);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Buttons for the main menu
        JButton playButton = createStyledButton("Play");
        JButton settingsButton = createStyledButton("Settings");
        JButton quitButton = createStyledButton("Quit");

        playButton.addActionListener(e -> controller.onPlayPressed());
        settingsButton.addActionListener(e -> controller.onSettingsPressed());
        quitButton.addActionListener(e -> System.exit(0));

        mainPanel.add(Box.createVerticalStrut(120), gbc);
        mainPanel.add(playButton, gbc);
        mainPanel.add(Box.createVerticalStrut(10), gbc);
        mainPanel.add(settingsButton, gbc);
        mainPanel.add(Box.createVerticalStrut(10), gbc);
        mainPanel.add(quitButton, gbc);

        // World Select Panel
        worldSelectPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(24, 20, 37),
                        0, getHeight(), new Color(65, 41, 90));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        worldSelectPanel.setLayout(new GridBagLayout());

        // Buttons for world selection
        JButton newWorldButton = createStyledButton("New World");
        JButton loadWorldButton = createStyledButton("Load World");
        JButton backButton = createStyledButton("Back");

        newWorldButton.addActionListener(e -> controller.onNewWorldPressed());
        loadWorldButton.addActionListener(e -> controller.onLoadWorldPressed());
        backButton.addActionListener(e -> controller.onBackPressed());

        worldSelectPanel.add(Box.createVerticalStrut(120), gbc);
        worldSelectPanel.add(newWorldButton, gbc);
        worldSelectPanel.add(Box.createVerticalStrut(10), gbc);
        worldSelectPanel.add(loadWorldButton, gbc);
        worldSelectPanel.add(Box.createVerticalStrut(10), gbc);
        worldSelectPanel.add(backButton, gbc);

        // Initial setup
        setContentPane(mainPanel);
    }

    /**
     * Sets the controller for the view and initializes the settings panel.
     *
     * @param controller The controller to set for the view.
     */
    public void setController(MenuController controller) {
        this.controller = controller;
        settingsPanel = new SettingsPanel(controller); // Create the settings panel after having the controller
    }

    /**
     * Updates the view based on the specified menu state.
     *
     * @param state The menu state to display.
     */
    public void updateState(MenuState state) {
        switch (state) {
            case MAIN -> setContentPane(mainPanel);
            case WORLD_SELECT -> setContentPane(worldSelectPanel);
            case SETTINGS -> setContentPane(settingsPanel);
        }
        revalidate();
        repaint();
    }
}