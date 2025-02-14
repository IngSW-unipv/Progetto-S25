package view.menu;

import controller.event.EventBus;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import controller.event.MenuAction;
import controller.event.MenuEvent;
import model.statistics.DatabaseManager.WorldStats;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import java.awt.*;
import java.util.List;

/**
 * Displays game statistics and world rankings in a styled dialog.
 * Shows tabulated data with sortable columns and custom formatting.
 * Provides visual feedback for sorting and selection.
 *
 * @see WorldStats
 */
public class StatisticsDialog extends JDialog {
    /** Theme colors for UI components */
    private static final Color BACKGROUND_COLOR = new Color(24, 20, 37);
    private static final Color HEADER_COLOR = new Color(45, 45, 60);
    private static final Color SELECTED_COLOR = new Color(75, 110, 175);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Color ALTERNATE_ROW_COLOR = new Color(35, 35, 45);
    private static final Color GRID_COLOR = new Color(70, 70, 90);

    /** UI fonts for different components */
    private static final Font TITLE_FONT = new Font("Minecraft", Font.BOLD, 28);
    private static final Font TABLE_FONT = new Font("Minecraft", Font.PLAIN, 14);
    private static final Font HEADER_FONT = new Font("Minecraft", Font.BOLD, 16);

    /**
     * Creates statistics dialog with world data.
     * Sets up styled table with rankings and metrics.
     *
     * @param parent Parent frame for modal dialog
     * @param statistics List of world statistics to display
     */
    public StatisticsDialog(Frame parent, List<WorldStats> statistics) {
        super(parent, "World Statistics", true);
        setBackground(BACKGROUND_COLOR);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // Close application when dialog closes
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        JPanel mainPanel = createMainPanel();
        setupUI(mainPanel, statistics);
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(1100, 600));
    }

    /**
     * Creates main panel with gradient background.
     */
    private JPanel createMainPanel() {
        // Create panel with custom gradient painting
        JPanel panel = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, BACKGROUND_COLOR,
                        0, getHeight(), new Color(45, 45, 60)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 25));
        return panel;
    }

    /**
     * Sets up dialog UI components.
     * Creates title, table and close button.
     */
    private void setupUI(JPanel mainPanel, List<WorldStats> statistics) {
        // Add title
        JLabel titleLabel = new JLabel("World Rankings", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 25, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Handle empty state
        if (statistics.isEmpty()) {
            JLabel noStatsLabel = new JLabel("No worlds to display yet", SwingConstants.CENTER);
            noStatsLabel.setFont(TABLE_FONT);
            noStatsLabel.setForeground(TEXT_COLOR);
            mainPanel.add(noStatsLabel, BorderLayout.CENTER);
        } else {
            // Create scrollable table
            JTable table = createStatsTable(statistics);
            JScrollPane scrollPane = new JScrollPane(table);
            styleScrollPane(scrollPane);

            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setOpaque(false);
            tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
            tablePanel.add(scrollPane);

            mainPanel.add(tablePanel, BorderLayout.CENTER);
        }

        // Add close button
        JButton closeButton = createStyledButton("Close");
        closeButton.addActionListener(e -> {
            EventBus.getInstance().post(MenuEvent.action(MenuAction.QUIT_GAME));
            dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates statistics table with world rankings.
     * Configures table model with column types and formatting.
     *
     * @param stats List of world statistics to display
     * @return Configured JTable with rankings data
     */
    private JTable createStatsTable(List<WorldStats> stats) {
        String[] columnNames = {"Rank", "World Name", "Blocks Placed", "Blocks Destroyed", "Play Time"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            private final Class<?>[] columnTypes = new Class<?>[] {
                    Integer.class, String.class, Integer.class, Integer.class, String.class
            };

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        };

        for (int i = 0; i < stats.size(); i++) {
            WorldStats stat = stats.get(i);
            model.addRow(new Object[]{
                    i + 1,
                    stat.worldName(),
                    stat.blocksPlaced(),
                    stat.blocksDestroyed(),
                    String.format(Locale.US, "%.1f", stat.playTimeSeconds())
            });
        }

        JTable table = new JTable(model);
        styleTable(table);

        return table;
    }

    /**
     * Applies custom styling to table components.
     * Configures headers, cell renderers, colors and sorting.
     *
     * @param table Table to style
     */
    private void styleTable(JTable table) {
        table.setFont(TABLE_FONT);
        table.setForeground(TEXT_COLOR);
        table.setBackground(BACKGROUND_COLOR);
        table.setGridColor(GRID_COLOR);
        table.setSelectionBackground(SELECTED_COLOR);
        table.setSelectionForeground(TEXT_COLOR);
        table.setCellSelectionEnabled(false);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setRowHeight(40);
        table.setFocusable(false);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(2, 2));

        // Style table header
        JTableHeader header = table.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setForeground(TEXT_COLOR);
        header.setFont(HEADER_FONT);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 50));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);

        // Custom header renderer with sort indicators
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            private final JPanel panel = new JPanel(new BorderLayout(10, 0));
            private final JLabel label = new JLabel();
            private final JPanel arrowPanel = new JPanel(new GridLayout(2, 1, 0, 4));
            private final JLabel upArrow = new JLabel("▲");
            private final JLabel downArrow = new JLabel("▼");

            {
                panel.setOpaque(true);
                arrowPanel.setOpaque(false);
                upArrow.setFont(new Font("Dialog", Font.BOLD, 16));
                downArrow.setFont(new Font("Dialog", Font.BOLD, 16));
                upArrow.setPreferredSize(new Dimension(25, 20));
                downArrow.setPreferredSize(new Dimension(25, 20));
                upArrow.setForeground(TEXT_COLOR);
                downArrow.setForeground(TEXT_COLOR);
                arrowPanel.add(upArrow);
                arrowPanel.add(downArrow);
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                label.setText(value.toString());
                label.setBackground(HEADER_COLOR);
                label.setForeground(TEXT_COLOR);
                label.setFont(HEADER_FONT);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                panel.setBackground(HEADER_COLOR);
                panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                panel.removeAll();
                panel.add(label, BorderLayout.CENTER);

                // Show arrows for all columns
                panel.add(arrowPanel, BorderLayout.EAST);

                if (table.getRowSorter() != null) {
                    List<? extends RowSorter.SortKey> sortKeys = table.getRowSorter().getSortKeys();
                    if (!sortKeys.isEmpty() && sortKeys.getFirst().getColumn() == column) {
                        boolean isAscending = sortKeys.getFirst().getSortOrder() == SortOrder.ASCENDING;
                        upArrow.setForeground(isAscending ? Color.WHITE : Color.GRAY);
                        downArrow.setForeground(isAscending ? Color.GRAY : Color.WHITE);
                    } else {
                        upArrow.setForeground(Color.GRAY);
                        downArrow.setForeground(Color.GRAY);
                    }
                }

                return panel;
            }
        });

        // Configure column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setPreferredWidth(200);

        // Configure sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) table.getModel());
        table.setRowSorter(sorter);

        sorter.setComparator(0, (Object o1, Object o2) -> {
            int r1 = (Integer) o1;
            int r2 = (Integer) o2;
            return Integer.compare(r1, r2);
        });

        // Custom cell renderers for data formatting
        DefaultTableCellRenderer[] renderers = new DefaultTableCellRenderer[5];
        for (int i = 0; i < renderers.length; i++) {
            renderers[i] = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Object formattedValue = switch(column) {
                        case 0 -> formatRank((Integer)value);
                        case 2, 3 -> formatNumber((Integer)value);
                        case 4 -> formatTime(Float.parseFloat((String)value));
                        default -> value;
                    };

                    Component c = super.getTableCellRendererComponent(
                            table, formattedValue, isSelected, hasFocus, row, column);

                    if (!isSelected) {
                        c.setBackground(row % 2 == 0 ? BACKGROUND_COLOR : ALTERNATE_ROW_COLOR);
                    }

                    setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                    setHorizontalAlignment(SwingConstants.CENTER);
                    return c;
                }
            };
            table.getColumnModel().getColumn(i).setCellRenderer(renderers[i]);
        }
    }

    /**
     * Styles scroll pane for table container.
     * Customizes scrollbar appearance and buttons.
     *
     * @param scrollPane ScrollPane to style
     */
    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createLineBorder(GRID_COLOR, 2));

        // Custom vertical scrollbar
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = HEADER_COLOR;
                this.trackColor = BACKGROUND_COLOR;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });

        // Custom horizontal scrollbar
        scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = HEADER_COLOR;
                this.trackColor = BACKGROUND_COLOR;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
    }

    /**
     * Formats rank number with medal emoji for top 3.
     *
     * @param rank Position in rankings
     * @return Formatted rank string
     */
    private String formatRank(int rank) {
        return switch (rank) {
            case 1 -> "🥇 1st";
            case 2 -> "🥈 2nd";
            case 3 -> "🥉 3rd";
            default -> "#" + rank;
        };
    }

    /**
     * Formats number with thousands separators.
     *
     * @param number Number to format
     * @return Formatted number string
     */
    private String formatNumber(int number) {
        return String.format("%,d", number);
    }

    /**
     * Formats time in seconds to readable duration.
     * Shows hours and minutes for longer durations.
     *
     * @param seconds Duration in seconds
     * @return Formatted time string
     */
    private String formatTime(float seconds) {
        int hours = (int) (seconds / 3600);
        int minutes = (int) ((seconds % 3600) / 60);
        int secs = (int) (seconds % 60);

        if (hours > 0) {
            return String.format("%dh %02dm", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%dm %02ds", minutes, secs);
        } else {
            return String.format("%ds", secs);
        }
    }

    /**
     * Creates themed button with hover effects.
     * Customizes appearance with gradient background.
     *
     * @param text Button label
     * @return Styled JButton
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(HEADER_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(HEADER_COLOR.brighter());
                } else {
                    g2d.setColor(HEADER_COLOR);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(GRID_COLOR);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                FontMetrics metrics = g2d.getFontMetrics(getFont());
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

                g2d.setColor(TEXT_COLOR);
                g2d.setFont(getFont());
                g2d.drawString(getText(), x, y);
            }
        };

        button.setFont(TABLE_FONT);
        button.setForeground(TEXT_COLOR);
        button.setPreferredSize(new Dimension(120, 35));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }
}