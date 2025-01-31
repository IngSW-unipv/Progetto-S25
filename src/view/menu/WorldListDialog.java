package view.menu;

import controller.event.*;
import model.world.WorldData;
import model.save.WorldManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for loading and managing saved game worlds.
 * Provides interface for viewing, loading and deleting world saves.
 */
public class WorldListDialog extends JDialog {
    /** Colors for UI elements */
    private final Color BUTTON_COLOR = new Color(65, 65, 65);
    private final Color TEXT_COLOR = new Color(255, 255, 255);

    /** Fonts used in UI */
    private final Font TITLE_FONT = new Font("Minecraft", Font.BOLD, 24);
    private final Font MAIN_FONT = new Font("Minecraft", Font.PLAIN, 16);

    /** List of available world saves */
    private final List<WorldData> worlds = new ArrayList<>();

    /** Currently selected world save */
    private WorldData selectedWorld = null;


    /**
     * Creates dialog for selecting and managing worlds
     *
     * @param parent Parent frame for modal dialog
     */
    public WorldListDialog(Frame parent) {
        super(parent, "Load World", true);
        Color BACKGROUND_COLOR = new Color(24, 20, 37);
        setBackground(BACKGROUND_COLOR);

        worlds.addAll(WorldManager.getWorlds());
        setupUI();
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Initializes and configures the dialog UI components
     */
    private void setupUI() {
        // Main panel with gradient background
        JPanel mainPanel = getjPanel();

        // Title section
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Load World", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // World list section
        DefaultListModel<WorldData> listModel = new DefaultListModel<>();
        worlds.forEach(listModel::addElement);

        JList<WorldData> worldList = new JList<>(listModel);
        worldList.setCellRenderer(new WorldListRenderer());
        worldList.setFont(MAIN_FONT);
        worldList.setForeground(TEXT_COLOR);
        worldList.setBackground(BUTTON_COLOR);
        worldList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(worldList);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setBorder(BorderFactory.createLineBorder(BUTTON_COLOR));
        scrollPane.getViewport().setBackground(BUTTON_COLOR);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button section
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton loadButton = createStyledButton("Load");
        JButton deleteButton = createStyledButton("Delete");
        JButton cancelButton = createStyledButton("Cancel");

        // Configure button actions
        loadButton.addActionListener(e -> {
            selectedWorld = worldList.getSelectedValue();
            if (selectedWorld != null) {
                setVisible(false);
                EventBus.getInstance().post(MenuEvent.startGame(selectedWorld.name(), selectedWorld.seed()));
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select a world to load.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            WorldData selectedWorld = worldList.getSelectedValue();
            if (selectedWorld != null) {
                int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete world '" + selectedWorld.name() + "'?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

                if (result == JOptionPane.YES_OPTION) {
                    WorldManager.deleteWorld(selectedWorld.name());
                    listModel.removeElement(selectedWorld);

                    if (listModel.isEmpty()) {
                        dispose();
                        EventBus.getInstance().post(MenuEvent.action(MenuAction.SHOW_WORLD_SELECT));
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select a world to delete.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(loadButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private static JPanel getjPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(24, 20, 37),
                    0, getHeight(), new Color(65, 41, 90));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return mainPanel;
    }

    /**
     * Creates button with custom styling and hover effects
     *
     * @param text Button label text
     * @return Styled button component
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Set button color based on state
                if (getModel().isPressed()) {
                    g2d.setColor(BUTTON_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(BUTTON_COLOR.brighter());
                } else {
                    g2d.setColor(BUTTON_COLOR);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

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
     * Custom renderer for world list items
     */
    private class WorldListRenderer extends JPanel implements ListCellRenderer<WorldData> {
        /** Name display label */
        private final JLabel nameLabel;

        /** Seed display label */
        private final JLabel seedLabel;

        /** Container for labels */
        private final JPanel contentPanel;


        /**
         * Creates renderer with styled world information layout
         */
        public WorldListRenderer() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setOpaque(true);

            contentPanel = new JPanel(new GridBagLayout());
            contentPanel.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.anchor = GridBagConstraints.WEST;

            nameLabel = new JLabel();
            nameLabel.setFont(new Font("Minecraft", Font.BOLD, 18));
            nameLabel.setForeground(TEXT_COLOR);

            seedLabel = new JLabel();
            seedLabel.setFont(new Font("Minecraft", Font.PLAIN, 14));
            seedLabel.setForeground(new Color(170, 170, 170));

            contentPanel.add(nameLabel, gbc);
            gbc.insets = new Insets(4, 20, 0, 0);
            contentPanel.add(seedLabel, gbc);

            add(contentPanel, BorderLayout.CENTER);
        }

        /**
         * Renders a world item in the list
         */
        @Override
        public Component getListCellRendererComponent(JList<? extends WorldData> list,
                                                      WorldData world, int index, boolean isSelected, boolean cellHasFocus) {
            nameLabel.setText(world.name());
            seedLabel.setText("Seed: " + world.seed());

            if (isSelected) {
                setBackground(new Color(65, 65, 90));
                contentPanel.setBackground(new Color(65, 65, 90));
            } else {
                setBackground(BUTTON_COLOR);
                contentPanel.setBackground(BUTTON_COLOR);
            }

            return this;
        }
    }
}