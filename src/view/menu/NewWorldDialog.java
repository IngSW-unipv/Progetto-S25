package view.menu;

import controller.event.EventBus;
import controller.event.MenuEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * Dialog for creating new game worlds with custom names and seeds.
 * Provides name and seed input fields with validation.
 */
public class NewWorldDialog extends JDialog {
    /** World generation seed */
    private long seed;

    /** Seed input field */
    private final JTextField seedField;

    /** World name input field */
    private final JTextField nameField;

    /** UI styling constants */
    private final Color BACKGROUND_COLOR = new Color(24, 20, 37);
    private final Color BUTTON_COLOR = new Color(65, 65, 65);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Font MAIN_FONT = new Font("Minecraft", Font.PLAIN, 16);


    /**
     * Creates dialog with input fields and buttons
     *
     * @param parent Parent window for modal dialog
     */
    public NewWorldDialog(Frame parent) {
        // Initialize dialog
        super(parent, "New World", true);
        setBackground(BACKGROUND_COLOR);

        // Create main panel with background
        JPanel mainPanel = getjPanel();

        // Set layout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add title section
        Font TITLE_FONT = new Font("Minecraft", Font.BOLD, 24);
        JLabel titleLabel = new JLabel("Create New World", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        mainPanel.add(titleLabel, gbc);

        // Add world name input section
        JPanel namePanel = new JPanel(new GridBagLayout());
        namePanel.setOpaque(false);

        // Create name label
        JLabel nameLabel = new JLabel("World Name:");
        nameLabel.setFont(MAIN_FONT);
        nameLabel.setForeground(TEXT_COLOR);

        // Create name field with custom background
        nameField = new JTextField("New World", 20) {
            @Override
            protected void paintComponent(Graphics g) {
                // Draw rounded background
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(BUTTON_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                super.paintComponent(g);
            }
        };
        configureTextField(nameField);

        // Add name field listeners
        nameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                // Select all text on focus
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

        // Add name components to panel
        GridBagConstraints nameGbc = new GridBagConstraints();
        nameGbc.insets = new Insets(5, 5, 5, 5);
        namePanel.add(nameLabel, nameGbc);
        nameGbc.gridx = 1;
        nameGbc.weightx = 1;
        nameGbc.fill = GridBagConstraints.HORIZONTAL;
        namePanel.add(nameField, nameGbc);

        mainPanel.add(namePanel, gbc);

        // Generate initial random seed
        seed = new Random().nextLong();

        // Add seed input section
        JPanel seedPanel = new JPanel(new GridBagLayout());
        seedPanel.setOpaque(false);

        JLabel seedLabel = new JLabel("Seed:");
        seedLabel.setFont(MAIN_FONT);
        seedLabel.setForeground(TEXT_COLOR);

        // Create seed field with custom background
        seedField = new JTextField(String.valueOf(seed), 20) {
            @Override
            protected void paintComponent(Graphics g) {
                // Draw rounded background
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(BUTTON_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                super.paintComponent(g);
            }
        };
        configureTextField(seedField);

        // Add enter key handler
        seedField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    attemptWorldCreation();
                }
            }
        });

        // Add random seed button
        JButton randomButton = createStyledButton("Random");
        randomButton.addActionListener(e -> {
            // Generate and display new random seed
            seed = new Random().nextLong();
            seedField.setText(String.valueOf(seed));
        });

        // Add seed components to panel
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

        mainPanel.add(seedPanel, gbc);

        // Add action buttons section
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);

        JButton confirmButton = createStyledButton("Create");
        JButton cancelButton = createStyledButton("Cancel");

        confirmButton.addActionListener(e -> attemptWorldCreation());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, gbc);

        // Configure dialog
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private JPanel getjPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Fill panel background
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(BACKGROUND_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return mainPanel;
    }

    /**
     * Helper to configure text field appearance
     */
    private void configureTextField(JTextField field) {
        field.setFont(MAIN_FONT);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBackground(new Color(0, 0, 0, 0));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    /**
     * Creates custom styled button with hover effects
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                // Configure rendering
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

                // Draw button shape
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
     * Validates input and creates new world
     * Shows error if validation fails
     */
    private void attemptWorldCreation() {
        try {
            // Get and validate input values
            String worldName = nameField.getText().trim();
            String seedText = seedField.getText().trim();

            if (worldName.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a world name.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (seedText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a world seed.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse seed and start game
            seed = Long.parseLong(seedText);
            EventBus.getInstance().post(MenuEvent.startGame(worldName, seed));
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Invalid seed. Please enter a valid number.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Gets current world generation seed
     * @return Seed value
     */
    public long getSeed() {
        return seed;
    }
}