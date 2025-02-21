package controller.game;

import controller.event.EventBus;
import controller.event.MenuAction;
import controller.event.MenuEvent;
import controller.event.RenderEvent;
import controller.input.InputController;
import controller.input.PlayerController;
import model.game.Model;
import org.lwjgl.glfw.GLFW;
import util.GameClock;
import util.PerformanceMetrics;
import view.View;

/**
 * Core controller managing game loop, input handling and state updates.
 * Coordinates between model, view and input components following MVC pattern.
 * Handles game lifecycle including initialization, updates, and cleanup.
 *
 * @see Model
 * @see View
 * @see InputController
 * @see PlayerController
 */
public class GameController {
    /** Core game components */
    private final Model model;
    private final View view;
    private final PlayerController playerController;
    private final InputController inputController;

    /** Window and timing state */
    private final long window;
    private long lastFrameTime;
    private float deltaTime;

    /**
     * Initializes game controller and core components.
     * Sets up model, view, input handling and starts game loop.
     *
     * @param worldName Name of world to load/create
     * @param seed World generation seed
     */
    public GameController(String worldName, long seed) {
        this.model = new Model(worldName, seed);
        this.view = new View();
        view.createDisplay();

        this.window = view.getDisplayManager().getWindow();
        this.inputController = InputController.getInstance(window, model);
        this.playerController = new PlayerController(model.getPlayer(), model.getWorld());
        this.lastFrameTime = System.nanoTime();

        startGameLoop();
    }

    /**
     * Runs main game loop until exit or window close.
     * Updates input, game state and display each frame.
     */
    private void startGameLoop() {
        while (!GLFW.glfwWindowShouldClose(window) && model.getGameState().isRunning()) {
            inputController.pollInput();
            update();
            view.updateDisplay();
        }

        cleanup();
        view.closeDisplay();
        EventBus.getInstance().post(MenuEvent.action(MenuAction.SHOW_STATISTICS));
    }

    /**
     * Cleans up resources and shows statistics if requested.
     * Saves game state and releases system resources.
     */
    private void cleanup() {
        model.saveGame();
        model.getWorld().cleanup();
    }

    /**
     * Updates game state each frame.
     * Handles timing, player updates, and triggers rendering.
     */
    private void update() {
        PerformanceMetrics.startFrame();
        updateDeltaTime();
        GameClock.getInstance().update(deltaTime);

        playerController.update(deltaTime);
        model.update(deltaTime);

        EventBus.getInstance().post(
            new RenderEvent(
                model.getPlayer().getCamera(),
                model.getWorld().getVisibleBlocks(),
                model.getWorld()
            )
        );

        PerformanceMetrics.updateFrameMetrics();
    }

    /**
     * Updates frame timing for consistent game speed.
     * Calculates time elapsed since last frame.
     */
    private void updateDeltaTime() {
        long currentTime = System.nanoTime();
        deltaTime = (currentTime - lastFrameTime) / 1_000_000_000f;
        lastFrameTime = currentTime;
    }
}