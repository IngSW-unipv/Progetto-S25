package view.menu;

import controller.event.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * In-game pause menu overlay panel with styled buttons.
 * Displays semi-transparent overlay with pause options.
 */
public class PauseMenuPanel extends JFrame {
    /** UI styling constants */
    private final Color BUTTON_COLOR = new Color(65, 65, 65);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Font BUTTON_FONT = new Font("Minecraft", Font.BOLD, 20);


    /**
     * Creates pause menu overlay
     * Initializes window properties and UI components
     */
    public PauseMenuPanel() {
        // Configure window
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 150));
        setLayout(new GridBagLayout());
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        setupComponents();
        setFocusable(true);

        // Subscribe to menu events
        EventBus.getInstance().subscribe(EventType.MENU, this::handleMenuAction);

        // Add focus debug logging
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                System.out.println("PauseMenuPanel gained focus");
            }

            @Override
            public void focusLost(FocusEvent e) {
                System.out.println("PauseMenuPanel lost focus");
            }
        });
    }

    /**
     * Handles received menu action events
     */
    private void handleMenuAction(GameEvent event){
        System.out.println(event.getType());
        if (event instanceof MenuEvent menuEvent && menuEvent.action() != null) {
            //handlePauseMenuAction(menuEvent.action());
        }
    }

    /**
     * Handles received input events
     */
    private void handleInputEvent(GameEvent event) {
        if (event instanceof InputEvent inputEvent) {
            // Log received input events
            System.out.println("Input event received: " + inputEvent.getType());
        }
    }

    /**
     * Initializes and arranges menu components
     */
    private void setupComponents() {
        // Configure layout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Add title
        JLabel titleLabel = new JLabel("Game Paused", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Minecraft", Font.BOLD, 48));
        titleLabel.setForeground(TEXT_COLOR);
        add(titleLabel, gbc);

        add(Box.createVerticalStrut(50), gbc);

        // Add menu buttons
        addButton("Resume", MenuAction.RESUME_GAME, gbc);
        addButton("Settings", MenuAction.SHOW_SETTINGS, gbc);
        addButton("Save and Quit", MenuAction.QUIT_GAME, gbc);

        setOpacity(0.9f);
        setFocusable(true);
    }

    /**
     * Adds styled button with action handler
     */
    private void addButton(String text, MenuAction action, GridBagConstraints gbc) {
        JButton button = createStyledButton(text);
        button.addActionListener(e -> {
            System.out.println("Button clicked: " + action);
            EventBus.getInstance().post(MenuEvent.action(action));
        });
        add(button, gbc);
        add(Box.createVerticalStrut(10), gbc);
    }

    /**
     * Creates custom styled button with hover effects
     */
    private JButton createStyledButton(String text) {
        // Create button with custom rendering
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                // Enable antialiasing
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Set button color based on state
                ButtonModel model = getModel();
                if (model.isPressed()) {
                    g2d.setColor(BUTTON_COLOR.darker().darker());
                } else if (model.isRollover()) {
                    g2d.setColor(BUTTON_COLOR.brighter());
                } else {
                    g2d.setColor(BUTTON_COLOR);
                }

                // Draw button shape
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(new Color(100, 100, 100));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                // Draw text with shadow
                g2d.setFont(BUTTON_FONT);
                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(getText(), x + 1, y + 1);

                g2d.setColor(model.isRollover() ? new Color(255, 255, 150) : TEXT_COLOR);
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };

        // Configure button properties
        button.setPreferredSize(new Dimension(200, 40));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setRolloverEnabled(true);
        button.setFocusable(true);

        // Add hover cursor effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return button;
    }
}