package menu;

import controller.GameController;
import controller.InputController;
import model.Model;
import model.WorldData;
import model.WorldManager;
import view.View;

/**
 * The MenuController class manages interactions between the menu's view and model,
 * handling user inputs and transitioning to different application states, such as starting a new game.
 */
public class MenuController {
    private final MenuModel model; // The menu model that holds and manages the state of the menu.
    private final MenuView view; // The menu view that displays the menu to the user.

    /**
     * Constructs a MenuController with the specified model and view.
     *
     * @param model The menu model that manages the menu state.
     * @param view  The menu view that displays the menu to the user.
     */
    public MenuController(MenuModel model, MenuView view) {
        this.model = model;
        this.view = view;
        this.view.setController(this);
        WorldManager.initialize();
        updateView();
    }

    /**
     * Updates the view to reflect the current state of the model.
     */
    private void updateView() {
        view.updateState(model.getCurrentState());
    }

    /**
     * Handles the "Play" button press, transitioning to the world selection state.
     */
    public void onPlayPressed() {
        model.setCurrentState(MenuState.WORLD_SELECT);
        updateView();
    }

    /**
     * Handles the "Back" button press, returning to the main menu state.
     */
    public void onBackPressed() {
        model.setCurrentState(MenuState.MAIN);
        updateView();
    }

    /**
     * Handles the "New World" button press, allowing the user to create a new world.
     * If confirmed, starts a new game with the specified seed.
     */
    public void onNewWorldPressed() {
        NewWorldDialog dialog = new NewWorldDialog(view);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            long seed = dialog.getSeed();
            String name = dialog.getWorldName();
            WorldManager.saveWorldMetadata(new WorldData(name, seed));
            startNewGame(seed);
        }
    }

    /**
     * Starts a new game with the specified seed.
     * Initializes the game components and starts the game loop.
     *
     * @param seed The seed used to generate the world.
     */
    private void startNewGame(long seed) {
        view.dispose(); // Close the menu

        // Initialize the game components
        Model model = new Model(seed);
        View view = new View();
        view.createDisplay();

        GameController gameController = new GameController(model);
        InputController inputController = new InputController(view.getDisplayManager().getWindow());

        // Main game loop
        while (model.getGameState().getRunning()) {
            inputController.pollInput();
            gameController.update();
            view.updateDisplay();
        }

        view.closeDisplay(); // Clean up after the game loop ends
    }

    /**
     * Handles the "Load World" button press.
     * This method is currently a placeholder for future implementation.
     */
    public void onLoadWorldPressed() {
        WorldListDialog dialog = new WorldListDialog(view);
        dialog.setVisible(true);

        if (dialog.isConfirmed() && dialog.getSelectedWorld() != null) {
            WorldData selectedWorld = dialog.getSelectedWorld();
            startNewGame(selectedWorld.seed());
        }
    }

    /**
     * Handles the "Settings" button press, transitioning to the settings menu state.
     */
    public void onSettingsPressed() {
        model.setCurrentState(MenuState.SETTINGS);
        updateView();
    }
}