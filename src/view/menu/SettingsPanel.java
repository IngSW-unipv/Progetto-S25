package view.menu;

import config.GameConfig;
import config.ConfigManager;
import controller.event.MenuAction;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Settings panel for modifying game configuration.
 * Provides UI controls for adjusting graphics, controls, and physics parameters.
 */
public class SettingsPanel extends JPanel {
    /** Parent menu view for accessing shared UI components */
    private final MenuView menuView;

    /** Maps setting names to their input fields */
    private final Map<String, JTextField> settingsFields = new HashMap<>();

    /**
     * Creates settings panel with fields for modifying game configuration
     */
    public SettingsPanel(MenuView menuView) {
        this.menuView = menuView;
        setLayout(new BorderLayout());
        setBackground(new Color(24, 20, 37));
        setupComponents();
    }

    /**
     * Initializes all panel components and layout
     */
    private void setupComponents() {
        // Title section
        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Minecraft", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        // Settings container
        JPanel settingsContainer = new JPanel(new GridBagLayout());
        settingsContainer.setOpaque(false);
        settingsContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Layout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Add setting fields
        addSettingField(settingsContainer, "Render Distance", "RENDER_DISTANCE", gbc);
        addSettingField(settingsContainer, "Mouse Sensitivity", "CAMERA_MOUSE_SENSITIVITY", gbc);
        addSettingField(settingsContainer, "Movement Speed", "CAMERA_MOVE_SPEED", gbc);

        // Scrollable container
        JScrollPane scrollPane = new JScrollPane(settingsContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        JButton saveButton = menuView.createStyledButton("Save", MenuAction.SAVE_SETTINGS);
        JButton backButton = menuView.createStyledButton("Back", MenuAction.BACK_TO_MAIN);

        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Adds labeled setting field to container
     */
    private void addSettingField(JPanel container, String label, String field, GridBagConstraints gbc) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);

        // Label
        JLabel nameLabel = new JLabel(label);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Minecraft", Font.PLAIN, 16));
        nameLabel.setPreferredSize(new Dimension(200, 30));

        // Input field
        JTextField textField = getjTextField();
        settingsFields.put(field, textField);

        panel.add(nameLabel);
        panel.add(textField);
        container.add(panel, gbc);
        gbc.gridy++;
    }

    /**
     * Creates styled text input field
     */
    private JTextField getjTextField() {
        JTextField textField = new JTextField(10) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(65, 65, 65));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        textField.setFont(new Font("Minecraft", Font.PLAIN, 16));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        textField.setOpaque(false);
        textField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return textField;
    }

    /**
     * Loads current game settings into input fields
     */
    public void loadCurrentSettings() {
        settingsFields.get("RENDER_DISTANCE").setText(String.valueOf(GameConfig.RENDER_DISTANCE));
        settingsFields.get("CAMERA_MOUSE_SENSITIVITY").setText(String.valueOf(GameConfig.CAMERA_MOUSE_SENSITIVITY));
        settingsFields.get("CAMERA_MOVE_SPEED").setText(String.valueOf(GameConfig.CAMERA_MOVE_SPEED));
    }

    /**
     * Validates and saves settings to game configuration
     */
    public void saveSettings() {
        try {
            // Parse and update settings
            GameConfig.RENDER_DISTANCE = Integer.parseInt(settingsFields.get("RENDER_DISTANCE").getText());
            GameConfig.CAMERA_MOUSE_SENSITIVITY = Float.parseFloat(settingsFields.get("CAMERA_MOUSE_SENSITIVITY").getText());
            GameConfig.CAMERA_MOVE_SPEED = Float.parseFloat(settingsFields.get("CAMERA_MOVE_SPEED").getText());

            ConfigManager.saveConfig();
            JOptionPane.showMessageDialog(this, "Settings saved successfully!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid input values. Please check your settings.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Renders panel background with gradient
     */
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