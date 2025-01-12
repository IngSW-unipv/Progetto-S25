package menu;

import config.ConfigManager;
import config.GameConfig;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private final Color BACKGROUND_COLOR = new Color(24, 20, 37);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Font TITLE_FONT = new Font("Minecraft", Font.BOLD, 32);
    private final Font HEADER_FONT = new Font("Minecraft", Font.BOLD, 20);
    private final Font LABEL_FONT = new Font("Minecraft", Font.PLAIN, 16);

    private final JTextField renderDistanceField;
    private final JTextField moveSpeedField;
    private final JTextField mouseSensitivityField;
    private final JTextField movementIncrementField;
    private final JTextField gravityField;
    private final JTextField jumpForceField;
    private final JTextField terminalVelocityField;

    public SettingsPanel(MenuController controller) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(854, 480));

        // Main container
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
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Title
        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Settings container
        JPanel settingsContainer = new JPanel();
        settingsContainer.setLayout(new BoxLayout(settingsContainer, BoxLayout.Y_AXIS));
        settingsContainer.setOpaque(false);

        // World Settings
        JPanel worldPanel = createCategoryPanel("World Settings");
        renderDistanceField = addSettingField(worldPanel, "Render Distance", String.valueOf(GameConfig.RENDER_DISTANCE));

        // Camera Settings
        JPanel cameraPanel = createCategoryPanel("Camera Settings");
        moveSpeedField = addSettingField(cameraPanel, "Move Speed", String.valueOf(GameConfig.CAMERA_MOVE_SPEED));
        mouseSensitivityField = addSettingField(cameraPanel, "Mouse Sensitivity", String.valueOf(GameConfig.CAMERA_MOUSE_SENSITIVITY));
        movementIncrementField = addSettingField(cameraPanel, "Movement Increment", String.valueOf(GameConfig.CAMERA_MOVEMENT_INCREMENT));

        // Physics Settings
        JPanel physicsPanel = createCategoryPanel("Physics Settings");
        gravityField = addSettingField(physicsPanel, "Gravity", String.valueOf(GameConfig.GRAVITY));
        jumpForceField = addSettingField(physicsPanel, "Jump Force", String.valueOf(GameConfig.JUMP_FORCE));
        terminalVelocityField = addSettingField(physicsPanel, "Terminal Velocity", String.valueOf(GameConfig.TERMINAL_VELOCITY));

        settingsContainer.add(worldPanel);
        settingsContainer.add(Box.createVerticalStrut(20));
        settingsContainer.add(cameraPanel);
        settingsContainer.add(Box.createVerticalStrut(20));
        settingsContainer.add(physicsPanel);

        JScrollPane scrollPane = new JScrollPane(settingsContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton saveButton = createStyledButton("Save");
        JButton backButton = createStyledButton("Back");

        saveButton.addActionListener(e -> {
            saveSettings();
            controller.onBackPressed();
        });
        backButton.addActionListener(e -> controller.onBackPressed());

        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createCategoryPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setForeground(TEXT_COLOR);
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(headerLabel);

        return panel;
    }

    private JTextField addSettingField(JPanel panel, String labelText, String value) {
        JPanel settingPanel = new JPanel(new BorderLayout(20, 0));
        settingPanel.setOpaque(false);
        settingPanel.setMaximumSize(new Dimension(600, 40));
        settingPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);

        JTextField field = new JTextField(value) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(65, 65, 65));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        field.setFont(LABEL_FONT);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setPreferredSize(new Dimension(150, 30));

        settingPanel.add(label, BorderLayout.WEST);
        settingPanel.add(field, BorderLayout.EAST);
        panel.add(settingPanel);

        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color buttonColor = new Color(65, 65, 65);
                if (getModel().isPressed()) {
                    g2d.setColor(buttonColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(buttonColor.brighter());
                } else {
                    g2d.setColor(buttonColor);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);

                g2d.setColor(TEXT_COLOR);
                g2d.setFont(getFont());
                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };

        button.setFont(LABEL_FONT);
        button.setForeground(TEXT_COLOR);
        button.setPreferredSize(new Dimension(120, 35));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void saveSettings() {
        try {
            GameConfig.RENDER_DISTANCE = Integer.parseInt(renderDistanceField.getText());
            GameConfig.CAMERA_MOVE_SPEED = Float.parseFloat(moveSpeedField.getText());
            GameConfig.CAMERA_MOUSE_SENSITIVITY = Float.parseFloat(mouseSensitivityField.getText());
            GameConfig.CAMERA_MOVEMENT_INCREMENT = Float.parseFloat(movementIncrementField.getText());
            GameConfig.GRAVITY = Float.parseFloat(gravityField.getText());
            GameConfig.JUMP_FORCE = Float.parseFloat(jumpForceField.getText());
            GameConfig.TERMINAL_VELOCITY = Float.parseFloat(terminalVelocityField.getText());

            ConfigManager.saveConfig();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid input values. Please check your settings.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}