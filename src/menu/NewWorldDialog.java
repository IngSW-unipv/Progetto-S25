package menu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Random;

public class NewWorldDialog extends JDialog {
    private boolean confirmed = false;
    private long seed;
    private JTextField seedField;
    private final Color BACKGROUND_COLOR = new Color(24, 20, 37);
    private final Color BUTTON_COLOR = new Color(65, 65, 65);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Font TITLE_FONT = new Font("Minecraft", Font.BOLD, 24);
    private final Font MAIN_FONT = new Font("Minecraft", Font.PLAIN, 16);

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

        // Random seed by default
        seed = new Random().nextLong();

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
            seed = new Random().nextLong();
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

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);

        JButton confirmButton = createStyledButton("Create");
        JButton cancelButton = createStyledButton("Cancel");

        confirmButton.addActionListener(e -> {
            try {
                seed = Long.parseLong(seedField.getText());
                confirmed = true;
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid seed. Please enter a valid number.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, gbc);

        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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

    public boolean isConfirmed() {
        return confirmed;
    }

    public long getSeed() {
        return seed;
    }
}