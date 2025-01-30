package menu.view;

import controller.event.EventBus;
import controller.event.MenuAction;
import controller.event.MenuActionEvent;
import config.ConfigManager;
import menu.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MenuView extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final MainMenuPanel mainMenuPanel;
    private final WorldSelectPanel worldSelectPanel;
    private final SettingsPanel settingsPanel;
    private final Color BUTTON_COLOR = new Color(65, 65, 65);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Font BUTTON_FONT = new Font("Minecraft", Font.BOLD, 20);

    public MenuView() {
        setTitle("Voxel Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(854, 480);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize panels after MenuView is created
        mainMenuPanel = new MainMenuPanel(this);
        worldSelectPanel = new WorldSelectPanel(this);
        settingsPanel = new SettingsPanel(this);

        mainPanel.add(mainMenuPanel, "MAIN");
        mainPanel.add(worldSelectPanel, "WORLD_SELECT");
        mainPanel.add(settingsPanel, "SETTINGS");

        setContentPane(mainPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ConfigManager.saveConfig();
            }
        });
    }

    public void showMainMenu() {
        cardLayout.show(mainPanel, "MAIN");
    }

    public void showWorldSelect() {
        cardLayout.show(mainPanel, "WORLD_SELECT");
    }

    public void showSettings() {
        cardLayout.show(mainPanel, "SETTINGS");
        settingsPanel.loadCurrentSettings();
    }

    public void showNewWorldDialog() {
        NewWorldDialog dialog = new NewWorldDialog(this);
        dialog.setVisible(true);
    }

    public void showLoadWorldDialog() {
        WorldListDialog dialog = new WorldListDialog(this);
        dialog.setVisible(true);
    }

    public void saveSettings() {
        settingsPanel.saveSettings();
    }

    public JButton createStyledButton(String text, MenuAction action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(BUTTON_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(BUTTON_COLOR.brighter());
                } else {
                    g2d.setColor(BUTTON_COLOR);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(BUTTON_FONT);

                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };

        button.setPreferredSize(new Dimension(200, 40));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> EventBus.getInstance().post(new MenuActionEvent(action)));

        return button;
    }
}