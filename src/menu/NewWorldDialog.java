package menu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Random;

public class NewWorldDialog extends JDialog {

    private boolean confirmed = false;                                              // Flag to check if the dialog was confirmed
    private long seed;                                                              // Seed for the new world
    private JTextField seedField;                                                   // Text field to display and edit the seed
    private final Color BACKGROUND_COLOR = new Color(24, 20, 37);           // Background color of the dialog
    private final Color BUTTON_COLOR = new Color(65, 65, 65);               // Color of the buttons
    private final Color TEXT_COLOR = new Color(255, 255, 255);              // Color of the text on buttons and labels
    private final Font TITLE_FONT = new Font("Minecraft", Font.BOLD, 24); // Font used for the title
    private final Font MAIN_FONT = new Font("Minecraft", Font.PLAIN, 16); // Font used for the main text and labels
    private JTextField nameField;
    private String worldName;

    /**
     * Constructs a NewWorldDialog instance, setting up the dialog's UI and behavior.
     *
     * @param parent The parent frame for the dialog.
     */
    public NewWorldDialog(Frame parent) {
        super(parent, "New World", true);
        setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(BACKGROUND_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel("Create New World", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        mainPanel.add(titleLabel, gbc);

        JPanel namePanel = new JPanel(new GridBagLayout());
        namePanel.setOpaque(false);

        JLabel nameLabel = new JLabel("World Name:");
        nameLabel.setFont(MAIN_FONT);
        nameLabel.setForeground(TEXT_COLOR);

        nameField = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(BUTTON_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                super.paintComponent(g);
            }
        };
        nameField.setFont(MAIN_FONT);
        nameField.setForeground(TEXT_COLOR);
        nameField.setCaretColor(TEXT_COLOR);
        nameField.setBackground(new Color(0, 0, 0, 0));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        GridBagConstraints nameGbc = new GridBagConstraints();
        nameGbc.insets = new Insets(5, 5, 5, 5);
        namePanel.add(nameLabel, nameGbc);
        nameGbc.gridx = 1;
        nameGbc.weightx = 1;
        nameGbc.fill = GridBagConstraints.HORIZONTAL;
        namePanel.add(nameField, nameGbc);

        mainPanel.add(namePanel, gbc);

        // Random seed by default
        seed = new Random().nextLong(); // Generate a random seed by default

        // Seed panel
        JPanel seedPanel = new JPanel(new GridBagLayout());
        seedPanel.setOpaque(false);

        JLabel seedLabel = new JLabel("Seed:");
        seedLabel.setFont(MAIN_FONT);
        seedLabel.setForeground(TEXT_COLOR);

        seedField = new JTextField(String.valueOf(seed), 20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(BUTTON_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                super.paintComponent(g);
            }
        };
        seedField.setFont(MAIN_FONT);
        seedField.setForeground(TEXT_COLOR);
        seedField.setCaretColor(TEXT_COLOR);
        seedField.setBackground(new Color(0, 0, 0, 0));
        seedField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JButton randomButton = createStyledButton("Random");
        randomButton.addActionListener(e -> {
            seed = new Random().nextLong(); // Generate a new random seed when pressed
            seedField.setText(String.valueOf(seed));
        });

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

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);

        JButton confirmButton = createStyledButton("Create");
        JButton cancelButton = createStyledButton("Cancel");

        confirmButton.addActionListener(e -> {
            try {
                seed = Long.parseLong(seedField.getText()); // Parse the seed from the text field
                worldName = nameField.getText().trim();
                if(worldName.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter a world name.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                confirmed = true;  // Mark the dialog as confirmed
                dispose();         // Close the dialog
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid seed. Please enter a valid number.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dispose()); // Close the dialog without confirming

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, gbc);

        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
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
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Set button color based on its state (pressed, hovered, or default)
                if (getModel().isPressed()) {
                    g2d.setColor(BUTTON_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(BUTTON_COLOR.brighter());
                } else {
                    g2d.setColor(BUTTON_COLOR);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);

                FontMetrics metrics = g2d.getFontMetrics(getFont());
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

                g2d.setColor(TEXT_COLOR);
                g2d.setFont(getFont());
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };

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
     * Returns whether the dialog was confirmed.
     *
     * @return True if confirmed, false otherwise.
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Returns the seed value entered or generated in the dialog.
     *
     * @return The seed value.
     */
    public long getSeed() {
        return seed;
    }

    public String getWorldName() {
        return worldName;
    }
}