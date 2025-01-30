package controller.menu;

import controller.event.*;
import controller.game.GameController;
import model.save.WorldManager;
import view.menu.MenuView;

public class MenuController {
    private final MenuView view;
    private final EventBus eventBus;

    public MenuController() {
        this.eventBus = EventBus.getInstance();
        this.view = new MenuView();

        WorldManager.initialize();
        eventBus.subscribe(EventType.MENU, this::handleMenuAction);
        showMainMenu();
    }

    private void handleMenuAction(GameEvent event) {
        if (event instanceof StartGameMenuEvent startEvent) {
            startGame(startEvent.worldName(), startEvent.seed());
        } else if ((event instanceof MenuActionEvent menuEvent)) {
            switch (menuEvent.action()) {
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

    public void showMainMenu() {
        view.setVisible(true);
        eventBus.post(new MenuActionEvent(MenuAction.SHOW_MAIN_MENU));
    }

    public void startGame(String worldName, long seed) {
        view.dispose();  // Chiude la finestra del view.menu
        new GameController(worldName, seed);  // Avvia il gioco
    }
}