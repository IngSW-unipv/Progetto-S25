package menu;

import controller.GameController;
import controller.InputController;
import model.Model;
import view.View;

public class MenuController {
    private final MenuModel model;
    private final MenuView view;

    public MenuController(MenuModel model, MenuView view) {
        this.model = model;
        this.view = view;
        this.view.setController(this);
        updateView();
    }

    private void updateView() {
        view.updateState(model.getCurrentState());
    }

    public void onPlayPressed() {
        model.setCurrentState(MenuState.WORLD_SELECT);
        updateView();
    }

    public void onBackPressed() {
        model.setCurrentState(MenuState.MAIN);
        updateView();
    }

    public void onNewWorldPressed() {
        NewWorldDialog dialog = new NewWorldDialog(view);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            long seed = dialog.getSeed();
            startNewGame(seed);
        }
    }

    private void startNewGame(long seed) {
        view.dispose(); // Close menu

        // Start game with seed
        Model model = new Model(seed);
        View view = new View();
        view.createDisplay();

        GameController gameController = new GameController(model);
        InputController inputController = new InputController(view.getDisplayManager().getWindow());

        while (model.getGameState().getRunning()) {
            inputController.pollInput();
            gameController.update();
            view.updateDisplay();
        }

        view.closeDisplay();
    }

    public void onLoadWorldPressed() {
        // TODO: Load existing world
        System.out.println("Loading world...");
    }

    public void onSettingsPressed() {
        model.setCurrentState(MenuState.SETTINGS);
        updateView();
    }
}