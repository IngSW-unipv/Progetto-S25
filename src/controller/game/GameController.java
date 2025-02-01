package controller.game;

import controller.event.*;
import controller.input.InputController;
import controller.input.PlayerController;
import model.game.Model;
import org.lwjgl.glfw.GLFW;
import util.PerformanceMetrics;
import view.View;

/**
 * Core controller managing game loop, input handling and state updates.
 * Coordinates between model, view and input components.
 */
public class GameController {
    /** Model containing game state and logic */
    public final Model model;

    /** View handling rendering */
    public final View view;

    /** Controls player movement and actions */
    private final PlayerController playerController;

    /** Handles raw input processing */
    private final InputController inputController;

    /** Timestamp of last frame for delta calculations */
    private long lastFrameTime;

    /** Time elapsed since last frame */
    private float deltaTime;

    /** GLFW window handle */
    private final long window;


    /**
     * Initializes game controller and core components.
     *
     * @param worldName Name of world to load/create
     * @param seed World generation seed
     */
    public GameController(String worldName, long seed) {
        // Initialize core components
        this.model = new Model(worldName, seed);
        this.view = new View();
        view.createDisplay();

        // Set up window and controllers
        this.window = view.getDisplayManager().getWindow();
        this.inputController = InputController.getInstance(window, model);
        this.playerController = new PlayerController(model.getPlayer(), model.getWorld());

        // Initialize timing and menu
        this.lastFrameTime = System.nanoTime();

        startGameLoop();
    }

    /**
     * Main game loop handling updates and rendering.
     * Runs until window closed or game terminated.
     */
    private void startGameLoop() {
        // Process input and update game state each frame
        while (!GLFW.glfwWindowShouldClose(window) && model.getGameState().isRunning()) {
            inputController.pollInput();
            update();
            view.updateDisplay();
        }

        // Clean up resources on exit
        view.closeDisplay();
    }

    /**
     * Updates game state and triggers rendering.
     * Called each frame to progress game.
     */
    public void update() {
        PerformanceMetrics.startFrame();

        // Update timing and physics
        updateDeltaTime();
        playerController.update(deltaTime);
        model.update(deltaTime);

        // Trigger rendering
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
     * Calculates time elapsed since last frame.
     */
    private void updateDeltaTime() {
        // Calculate delta time in seconds
        long currentTime = System.nanoTime();
        deltaTime = (currentTime - lastFrameTime) / 1_000_000_000f;
        lastFrameTime = currentTime;
    }
}