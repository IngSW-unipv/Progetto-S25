package controller.game;

import controller.event.*;
import controller.input.InputController;
import controller.input.PlayerController;
import model.game.Model;
import org.lwjgl.glfw.GLFW;
import view.View;
import view.renderer.PauseMenuRenderer;

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

    /** Renderer for pause menu interface */
    private final PauseMenuRenderer pauseMenu;


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
        this.pauseMenu = new PauseMenuRenderer(window);

        setupPauseMenu();
        startGameLoop();
    }

    /**
     * Sets up pause menu event subscription.
     */
    private void setupPauseMenu() {
        // Subscribe to menu events and route to action handler
        EventBus.getInstance().subscribe(EventType.MENU, event -> {
            if (event instanceof MenuEvent menuEvent && menuEvent.action() != null) {
                handlePauseMenuAction(menuEvent.action());
            }
        });
    }

    /**
     * Routes pause menu actions to appropriate handlers.
     *
     * @param action Menu action to process
     */
    private void handlePauseMenuAction(MenuAction action) {
        // Route each menu action to its handler
        switch (action) {
            case TOGGLE_PAUSE -> {
                if (model.getGameState().isPaused()) {
                    resumeGame();
                } else {
                    pauseGame();
                }
            }
            case RESUME_GAME -> resumeGame();
            case QUIT_GAME -> onQuitGame();
            case SHOW_SETTINGS -> onSettingsPressed();
        }
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
     * Enables pause state and shows pause menu.
     */
    private void pauseGame() {
        // Enable pause state and show cursor
        model.getGameState().setPaused(true);
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        pauseMenu.setVisible(true);
    }

    /**
     * Disables pause state and hides pause menu.
     */
    private void resumeGame() {
        // Disable pause state and hide cursor
        model.getGameState().setPaused(false);
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        pauseMenu.setVisible(false);
    }

    /**
     * Saves game state and initiates game shutdown.
     */
    public void onQuitGame() {
        // Save current state and exit
        model.saveGame();
        model.getGameState().setRunning(false);
    }

    /**
     * Shows settings interface (currently unimplemented).
     */
    public void onSettingsPressed() {
        // TODO: Implement settings menu
    }

    /**
     * Updates game state and triggers rendering.
     * Called each frame to progress game.
     */
    public void update() {
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

        // Render pause menu if game is paused
        if (model.getGameState().isPaused()) {
            pauseMenu.render();
        }
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