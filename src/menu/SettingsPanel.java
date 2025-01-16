package menu;

import config.ConfigManager;
import config.GameConfig;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private final Color BACKGROUND_COLOR = new Color(24, 20, 37);    // Background color of the panel
    private final Color TEXT_COLOR = new Color(255, 255, 255);        // Text color
    private final Font TITLE_FONT = new Font("Minecraft", Font.BOLD, 32); // Font for the title
    private final Font HEADER_FONT = new Font("Minecraft", Font.BOLD, 20); // Font for category headers
    private final Font LABEL_FONT = new Font("Minecraft", Font.PLAIN, 16); // Font for setting labels

    private final JTextField renderDistanceField;  // Text field for render distance setting
    private final JTextField moveSpeedField;       // Text field for movement speed setting
    private final JTextField mouseSensitivityField; // Text field for mouse sensitivity setting
    private final JTextField movementIncrementField; // Text field for movement increment setting
    private final JTextField gravityField;         // Text field for gravity setting
    private final JTextField jumpForceField;       // Text field for jump force setting
    private final JTextField terminalVelocityField; // Text field for terminal velocity setting

    public SettingsPanel(MenuController controller) {
        setLayout(new BorderLayout());  // Set layout manager for the panel
        setPreferredSize(new Dimension(854, 480)); // Set preferred size of the panel

        // Main container panel with custom background gradient
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, BACKGROUND_COLOR,
                        0, getHeight(), new Color(65, 41, 90)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40)); // Set border for the main panel

        // Title label
        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER); // Title text
        titleLabel.setFont(TITLE_FONT); // Set font for the title
        titleLabel.setForeground(TEXT_COLOR); // Set text color
        titleLabel.setBorder(new EmptyBorder(0, 0, 30, 0)); // Set border for the title
        mainPanel.add(titleLabel, BorderLayout.NORTH); // Add title to the top of the main panel

        // Settings container panel with vertical layout
        JPanel settingsContainer = new JPanel();
        settingsContainer.setLayout(new BoxLayout(settingsContainer, BoxLayout.Y_AXIS)); // Vertical layout for settings
        settingsContainer.setOpaque(false); // Set transparency for the container

        // World Settings category panel
        JPanel worldPanel = createCategoryPanel("World Settings");
        renderDistanceField = addSettingField(worldPanel, "Render Distance", String.valueOf(GameConfig.RENDER_DISTANCE)); // Add render distance setting

        // Camera Settings category panel
        JPanel cameraPanel = createCategoryPanel("Camera Settings");
        moveSpeedField = addSettingField(cameraPanel, "Move Speed", String.valueOf(GameConfig.CAMERA_MOVE_SPEED)); // Add move speed setting
        mouseSensitivityField = addSettingField(cameraPanel, "Mouse Sensitivity", String.valueOf(GameConfig.CAMERA_MOUSE_SENSITIVITY)); // Add mouse sensitivity setting
        movementIncrementField = addSettingField(cameraPanel, "Movement Increment", String.valueOf(GameConfig.CAMERA_MOVEMENT_INCREMENT)); // Add movement increment setting

        // Physics Settings category panel
        JPanel physicsPanel = createCategoryPanel("Physics Settings");
        gravityField = addSettingField(physicsPanel, "Gravity", String.valueOf(GameConfig.GRAVITY)); // Add gravity setting
        jumpForceField = addSettingField(physicsPanel, "Jump Force", String.valueOf(GameConfig.JUMP_FORCE)); // Add jump force setting
        terminalVelocityField = addSettingField(physicsPanel, "Terminal Velocity", String.valueOf(GameConfig.TERMINAL_VELOCITY)); // Add terminal velocity setting

        // Add category panels to the settings container
        settingsContainer.add(worldPanel);
        settingsContainer.add(Box.createVerticalStrut(20)); // Spacer between panels
        settingsContainer.add(cameraPanel);
        settingsContainer.add(Box.createVerticalStrut(20)); // Spacer between panels
        settingsContainer.add(physicsPanel);

        // Scrollable panel for settings
        JScrollPane scrollPane = new JScrollPane(settingsContainer);
        scrollPane.setOpaque(false); // Set transparency for the scroll pane
        scrollPane.getViewport().setOpaque(false); // Set transparency for the viewport
        scrollPane.setBorder(null); // Remove border
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Set scroll speed
        mainPanel.add(scrollPane, BorderLayout.CENTER); // Add scrollable settings container to the center

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); // Center-aligned buttons
        buttonPanel.setOpaque(false); // Set transparency for the button panel
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0)); // Set border for the button panel

        // Save and Back buttons
        JButton saveButton = createStyledButton("Save"); // Create save button
        JButton backButton = createStyledButton("Back"); // Create back button

        // Save button action
        saveButton.addActionListener(e -> {
            saveSettings(); // Save the settings
            controller.onBackPressed(); // Notify controller to go back
        });

        // Back button action
        backButton.addActionListener(e -> controller.onBackPressed()); // Notify controller to go back

        // Add buttons to the button panel
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH); // Add button panel to the bottom

        // Add the main panel to the SettingsPanel
        add(mainPanel);
    }

    // Create a category panel with a header
    private JPanel createCategoryPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Vertical layout for the category
        panel.setOpaque(false); // Set transparency for the panel
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Set border for the panel

        JLabel headerLabel = new JLabel(title); // Create header label
        headerLabel.setFont(HEADER_FONT); // Set font for the header
        headerLabel.setForeground(TEXT_COLOR); // Set text color
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align header to the left
        headerLabel.setBorder(new EmptyBorder(0, 0, 10, 0)); // Set border for the header
        panel.add(headerLabel); // Add header to the panel

        return panel;
    }

    // Add a setting field with a label and text field
    private JTextField addSettingField(JPanel panel, String labelText, String value) {
        JPanel settingPanel = new JPanel(new BorderLayout(20, 0)); // Horizontal layout for label and field
        settingPanel.setOpaque(false); // Set transparency for the setting panel
        settingPanel.setMaximumSize(new Dimension(600, 40)); // Set maximum size for the setting panel
        settingPanel.setBorder(new EmptyBorder(5, 10, 5, 10)); // Set border for the setting panel

        JLabel label = new JLabel(labelText); // Create label for the setting
        label.setFont(LABEL_FONT); // Set font for the label
        label.setForeground(TEXT_COLOR); // Set text color for the label

        JTextField field = new JTextField(value) { // Create text field for the setting
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(65, 65, 65)); // Set background color for the field
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10); // Draw rounded background
                super.paintComponent(g); // Call default painting
            }
        };
        field.setFont(LABEL_FONT); // Set font for the field
        field.setForeground(TEXT_COLOR); // Set text color for the field
        field.setCaretColor(TEXT_COLOR); // Set caret color for the field
        field.setOpaque(false); // Set transparency for the field
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK), // Black border for the field
            BorderFactory.createEmptyBorder(5, 10, 5, 10) // Padding inside the field
        ));
        field.setPreferredSize(new Dimension(150, 30)); // Set preferred size for the field

        settingPanel.add(label, BorderLayout.WEST); // Add label to the left side of the setting panel
        settingPanel.add(field, BorderLayout.EAST); // Add field to the right side of the setting panel
        panel.add(settingPanel); // Add setting panel to the category panel

        return field; // Return the text field for further use
    }

    // Create a styled button with custom paint
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Enable anti-aliasing

                Color buttonColor = new Color(65, 65, 65); // Button color
                if (getModel().isPressed()) {
                    g2d.setColor(buttonColor.darker()); // Darker color when pressed
                } else if (getModel().isRollover()) {
                    g2d.setColor(buttonColor.brighter()); // Brighter color when hovered
                } else {
                    g2d.setColor(buttonColor); // Default button color
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10); // Draw rounded button background
                g2d.setColor(Color.BLACK); // Set border color
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10); // Draw button border

                g2d.setColor(TEXT_COLOR); // Set text color
                g2d.setFont(getFont()); // Set font for the button text
                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2; // Calculate X position for centered text
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent(); // Calculate Y position for centered text
                g2d.drawString(getText(), x, y); // Draw the button text
                g2d.dispose(); // Dispose of the graphics object
            }
        };

        button.setFont(LABEL_FONT); // Set font for the button
        button.setForeground(TEXT_COLOR); // Set text color for the button
        button.setPreferredSize(new Dimension(120, 35)); // Set preferred size for the button
        button.setBorderPainted(false); // Remove border
        button.setContentAreaFilled(false); // Remove background fill
        button.setFocusPainted(false); // Remove focus indicator
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Set cursor to hand when hovered

        return button; // Return the styled button
    }

    // Save the settings to the configuration
    private void saveSettings() {
        try {
            GameConfig.RENDER_DISTANCE = Integer.parseInt(renderDistanceField.getText()); // Save render distance
            GameConfig.CAMERA_MOVE_SPEED = Float.parseFloat(moveSpeedField.getText()); // Save move speed
            GameConfig.CAMERA_MOUSE_SENSITIVITY = Float.parseFloat(mouseSensitivityField.getText()); // Save mouse sensitivity
            GameConfig.CAMERA_MOVEMENT_INCREMENT = Float.parseFloat(movementIncrementField.getText()); // Save movement increment
            GameConfig.GRAVITY = Float.parseFloat(gravityField.getText()); // Save gravity
            GameConfig.JUMP_FORCE = Float.parseFloat(jumpForceField.getText()); // Save jump force
            GameConfig.TERMINAL_VELOCITY = Float.parseFloat(terminalVelocityField.getText()); // Save terminal velocity

            ConfigManager.saveConfig(); // Save the updated configuration
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid input values. Please check your settings.", // Error message for invalid input
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}