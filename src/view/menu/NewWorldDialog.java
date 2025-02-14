package view.menu;

import controller.event.EventBus;
import controller.event.MenuEvent;
import model.save.WorldManager;
import model.world.WorldData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * Dialog for creating new worlds with customizable name and seed
 * Provides input validation and styled UI components following the game's visual theme
 */
public class NewWorldDialog extends JDialog {
    /** Generation seed for world creation */
    private long seed;

    /** Input field for world seed */
    private final JTextField seedField;

    /** Input field for world name */
    private final JTextField nameField;

    /** UI theme colors */
    private static final Color BACKGROUND_COLOR = new Color(24, 20, 37);
    private static final Color BUTTON_COLOR = new Color(65, 65, 65);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);

    /** UI fonts */
    private static final Font MAIN_FONT = new Font("Minecraft", Font.PLAIN, 16);
    private static final Font TITLE_FONT = new Font("Minecraft", Font.BOLD, 24);


    /**
     * Creates dialog with name/seed inputs and validation
     */
    public NewWorldDialog(Frame parent) {
        super(parent, "New World", true);
        setBackground(BACKGROUND_COLOR);

        // Create main container with gradient background
        JPanel mainPanel = createMainPanel();
        GridBagConstraints gbc = configureLayoutConstraints();

        // Add title section
        addTitleSection(mainPanel, gbc);

        // Add name input section
        nameField = new JTextField("New World", 20);
        addNameSection(mainPanel, gbc);

        // Initialize seed and add seed input section
        seed = new Random().nextLong();
        seedField = new JTextField(String.valueOf(seed), 20);
        addSeedSection(mainPanel, gbc);

        // Add action buttons
        addButtonSection(mainPanel, gbc);

        finalizeDialog(mainPanel, parent);
    }

    /**
     * Creates main panel with background styling
     */
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BACKGROUND_COLOR);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    /**
     * Configures grid bag constraints for layout
     */
    private GridBagConstraints configureLayoutConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        return gbc;
    }

    /**
     * Adds title section to dialog
     */
    private void addTitleSection(JPanel panel, GridBagConstraints gbc) {
        JLabel titleLabel = new JLabel("Create New World", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        panel.add(titleLabel, gbc);
    }

    /**
     * Adds name input section with label
     */
    private void addNameSection(JPanel panel, GridBagConstraints gbc) {
        JPanel namePanel = new JPanel(new GridBagLayout());
        namePanel.setOpaque(false);

        JLabel nameLabel = new JLabel("World Name:");
        nameLabel.setFont(MAIN_FONT);
        nameLabel.setForeground(TEXT_COLOR);

        configureTextField(nameField);
        setupNameFieldListeners();

        // Add components to name panel
        GridBagConstraints nameGbc = new GridBagConstraints();
        nameGbc.insets = new Insets(5, 5, 5, 5);
        namePanel.add(nameLabel, nameGbc);
        nameGbc.gridx = 1;
        nameGbc.weightx = 1;
        nameGbc.fill = GridBagConstraints.HORIZONTAL;
        namePanel.add(nameField, nameGbc);

        panel.add(namePanel, gbc);
    }

    /**
     * Adds seed input section with random button
     */
    private void addSeedSection(JPanel panel, GridBagConstraints gbc) {
        JPanel seedPanel = new JPanel(new GridBagLayout());
        seedPanel.setOpaque(false);

        JLabel seedLabel = new JLabel("Seed:");
        seedLabel.setFont(MAIN_FONT);
        seedLabel.setForeground(TEXT_COLOR);

        configureTextField(seedField);
        setupSeedFieldListeners();

        JButton randomButton = createStyledButton("Random");
        randomButton.addActionListener(e -> generateRandomSeed());

        // Add components to seed panel
        GridBagConstraints seedGbc = new GridBagConstraints();
        seedGbc.insets = new Insets(5, 5, 5, 5);
        seedPanel.add(seedLabel, seedGbc);
        seedGbc.gridx = 1;
        seedGbc.weightx = 1;
        seedGbc.fill = GridBagConstraints.HORIZONTAL;
        seedPanel.add(seedField, seedGbc);
        seedGbc.gridx = 2;
        seedGbc.weightx = 0;
        seedGbc.fill = GridBagConstraints.NONE;
        seedPanel.add(randomButton, seedGbc);

        panel.add(seedPanel, gbc);
    }

    /**
     * Adds action buttons section
     */
    private void addButtonSection(JPanel panel, GridBagConstraints gbc) {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);

        JButton confirmButton = createStyledButton("Create");
        JButton cancelButton = createStyledButton("Cancel");

        confirmButton.addActionListener(e -> attemptWorldCreation());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);
    }

    /**
     * Applies styling to text input field
     */
    private void configureTextField(JTextField field) {
        field.setFont(MAIN_FONT);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBackground(new Color(40, 40, 40));
        field.setSelectedTextColor(TEXT_COLOR);
        field.setSelectionColor(new Color(80, 80, 80));
        field.setOpaque(true);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    /**
     * Sets up listeners for name field
     */
    private void setupNameFieldListeners() {
        nameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nameField.selectAll();
            }
        });

        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                attemptWorldCreation();
            }
            }
        });
    }

    /**
     * Sets up listeners for seed field
     */
    private void setupSeedFieldListeners() {
        seedField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    attemptWorldCreation();
                }
            }
        });
    }

    /**
     * Creates styled button with hover effects
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw background based on state
                if (getModel().isPressed()) {
                    g2d.setColor(BUTTON_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(BUTTON_COLOR.brighter());
                } else {
                    g2d.setColor(BUTTON_COLOR);
                }

                // Draw button shape and border
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);

                // Draw centered text
                FontMetrics metrics = g2d.getFontMetrics(getFont());
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

                g2d.setColor(TEXT_COLOR);
                g2d.setFont(getFont());
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };

        // Configure button properties
        button.setFont(MAIN_FONT);
        button.setForeground(TEXT_COLOR);
        button.setPreferredSize(new Dimension(120, 35));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    /**
     * Generates new random seed and updates field
     */
    private void generateRandomSeed() {
        seed = new Random().nextLong();
        seedField.setText(String.valueOf(seed));
    }

    /**
     * Validates input and creates world if valid
     */
    private void attemptWorldCreation() {
        try {
            String worldName = nameField.getText().trim();
            String seedText = seedField.getText().trim();

            // Validate inputs
            if (worldName.isEmpty()) {
                showError("Please enter a world name.");
                return;
            }

            if (seedText.isEmpty()) {
                showError("Please enter a world seed.");
                return;
            }

            // Check if world exists
            java.util.List<WorldData> existingWorlds = WorldManager.getWorlds();
            if (existingWorlds.stream().anyMatch(w -> w.name().equals(worldName))) {
                showError("A world with name '" + worldName + "' already exists");
                return;
            }

            // Create world and close dialog
            seed = Long.parseLong(seedText);
            EventBus.getInstance().post(MenuEvent.startGame(worldName, seed));
            dispose();

        } catch (NumberFormatException ex) {
            showError("Invalid seed. Please enter a valid number.");
        }
    }

    /**
     * Shows error dialog with message
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Finalizes dialog setup and positioning
     */
    private void finalizeDialog(JPanel panel, Frame parent) {
        setContentPane(panel);
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    /**
     * Returns current world generation seed
     */
    public long getSeed() {
        return seed;
    }
}