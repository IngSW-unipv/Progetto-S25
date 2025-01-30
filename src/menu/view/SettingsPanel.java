package menu.view;

import config.GameConfig;
import config.ConfigManager;
import controller.event.MenuAction;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SettingsPanel extends JPanel {
    private final MenuView menuView;
    private final Map<String, JTextField> settingsFields = new HashMap<>();

    public SettingsPanel(MenuView menuView) {
        this.menuView = menuView;
        setLayout(new BorderLayout());
        Color BACKGROUND_COLOR = new Color(24, 20, 37);
        setBackground(BACKGROUND_COLOR);
        setupComponents();
    }
    
    private void setupComponents() {
        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Minecraft", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        JPanel settingsContainer = new JPanel();
        settingsContainer.setLayout(new BoxLayout(settingsContainer, BoxLayout.Y_AXIS));
        settingsContainer.setOpaque(false);
        settingsContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        addSettingField(settingsContainer, "Render Distance", "RENDER_DISTANCE");
        addSettingField(settingsContainer, "Mouse Sensitivity", "CAMERA_MOUSE_SENSITIVITY");
        addSettingField(settingsContainer, "Movement Speed", "CAMERA_MOVE_SPEED");
        addSettingField(settingsContainer, "Jump Force", "JUMP_FORCE");
        addSettingField(settingsContainer, "Gravity", "GRAVITY");

        JScrollPane scrollPane = new JScrollPane(settingsContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        JButton saveButton = menuView.createStyledButton("Save", MenuAction.SAVE_SETTINGS);
        JButton backButton = menuView.createStyledButton("Back", MenuAction.BACK_TO_MAIN);

        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addSettingField(JPanel container, String label, String field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);

        JLabel nameLabel = new JLabel(label);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Minecraft", Font.PLAIN, 16));
        nameLabel.setPreferredSize(new Dimension(200, 30));

        JTextField textField = getjTextField();
        settingsFields.put(field, textField);

        panel.add(nameLabel);
        panel.add(textField);
        container.add(panel);
        container.add(Box.createVerticalStrut(10));
    }

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

    public void loadCurrentSettings() {
        settingsFields.get("RENDER_DISTANCE").setText(String.valueOf(GameConfig.RENDER_DISTANCE));
        settingsFields.get("CAMERA_MOUSE_SENSITIVITY").setText(String.valueOf(GameConfig.CAMERA_MOUSE_SENSITIVITY));
        settingsFields.get("CAMERA_MOVE_SPEED").setText(String.valueOf(GameConfig.CAMERA_MOVE_SPEED));
        settingsFields.get("JUMP_FORCE").setText(String.valueOf(GameConfig.JUMP_FORCE));
        settingsFields.get("GRAVITY").setText(String.valueOf(GameConfig.GRAVITY));
    }

    public void saveSettings() {
        try {
            GameConfig.RENDER_DISTANCE = Integer.parseInt(settingsFields.get("RENDER_DISTANCE").getText());
            GameConfig.CAMERA_MOUSE_SENSITIVITY = Float.parseFloat(settingsFields.get("CAMERA_MOUSE_SENSITIVITY").getText());
            GameConfig.CAMERA_MOVE_SPEED = Float.parseFloat(settingsFields.get("CAMERA_MOVE_SPEED").getText());
            GameConfig.JUMP_FORCE = Float.parseFloat(settingsFields.get("JUMP_FORCE").getText());
            GameConfig.GRAVITY = Float.parseFloat(settingsFields.get("GRAVITY").getText());

            ConfigManager.saveConfig();
            JOptionPane.showMessageDialog(this, "Settings saved successfully!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input values. Please check your settings.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
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