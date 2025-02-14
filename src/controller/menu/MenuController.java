package controller.menu;

import controller.event.*;
import controller.game.GameController;
import model.save.WorldManager;
import model.statistics.DatabaseManager;
import view.menu.MenuView;
import view.menu.StatisticsDialog;

import javax.swing.*;
import java.util.List;

/**
 * Controls menu system flow and manages menu state transitions.
 * Routes menu actions and handles game initialization.
 */
public class MenuController {
    /** View component for menu rendering */
    private final MenuView view;

    /** Event system for menu actions */
    private final EventBus eventBus;


    /**
     * Initializes menu controller and world storage.
     * Sets up event handling and shows initial menu.
     */
    public MenuController() {
        // Initialize core components
        this.eventBus = EventBus.getInstance();
        this.view = new MenuView();

        // Set up world storage and event handling
        WorldManager.initialize();
        eventBus.subscribe(EventType.MENU, this::handleMenuAction);
        showMainMenu();
    }

    /**
     * Routes menu events to appropriate handlers.
     * Handles game start and menu navigation.
     *
     * @param event Menu event to process
     */
    private void handleMenuAction(GameEvent event) {
        if (event instanceof MenuEvent e) {
            if (e.worldName() != null) {
                startGame(e.worldName(), e.seed());
            } else if (e.action() != null) {
                switch (e.action()) {
                    case SHOW_STATISTICS -> view.showStatistics();
                    case SHOW_MAIN_MENU, BACK_TO_MAIN -> view.showMainMenu();
                    case SHOW_WORLD_SELECT -> view.showWorldSelect();
                    case SHOW_SETTINGS -> view.showSettings();
                    case SHOW_NEW_WORLD_DIALOG -> view.showNewWorldDialog();
                    case SHOW_LOAD_WORLD_DIALOG -> view.showLoadWorldDialog();
                    case SAVE_SETTINGS -> view.saveSettings();
                    case QUIT_GAME -> System.exit(0);
                }
            }
        }
    }

    /**
     * Shows main menu and posts menu event.
     */
    public void showMainMenu() {
        view.setVisible(true);
        eventBus.post(MenuEvent.action(MenuAction.SHOW_MAIN_MENU));
    }

    /**
     * Initializes and starts new game.
     *
     * @param worldName World to load/create
     * @param seed World generation seed
     */
    public void startGame(String worldName, long seed) {
        view.dispose();
        new GameController(worldName, seed);
    }
}