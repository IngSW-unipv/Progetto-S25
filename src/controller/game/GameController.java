package controller.game;

import controller.input.InputController;
import controller.input.PlayerController;
import controller.event.*;
import view.menu.PauseMenuPanel;
import model.Model;
import org.lwjgl.glfw.GLFW;
import view.View;
import view.window.WindowManager;

public class GameController {
    public final Model model;
    public final View view;
    private final PlayerController playerController;
    private final InputController inputController;
    private long lastFrameTime;
    private float deltaTime;
    private final long window;

    private PauseMenuPanel pauseMenu;

    public GameController(String worldName, long seed) {
        this.model = new Model(worldName, seed);
        this.view = new View();
        view.createDisplay();
        this.window = view.getDisplayManager().getWindow();
        this.inputController = InputController.getInstance(window, model);
        this.playerController = new PlayerController(model.getPlayer(), model.getWorld());
        this.lastFrameTime = System.nanoTime();

        setupPauseMenu();

        // Blocks the execution: must be the last instruction of the constructor
        startGameLoop();
    }

    private void setupPauseMenu() {
        pauseMenu = new PauseMenuPanel();

        EventBus.getInstance().subscribe(EventType.MENU, event -> {
            if (event instanceof MenuActionEvent menuEvent) {
                handlePauseMenuAction(menuEvent.action());
            }
        });
    }

    private void handlePauseMenuAction(MenuAction action) {
        switch (action) {
            case TOGGLE_PAUSE -> togglePause();
            case RESUME_GAME -> resumeGame();
            case QUIT_GAME -> onQuitGame();
            case SHOW_SETTINGS -> onSettingsPressed();
        }
    }

    private void startGameLoop() {
        while (!GLFW.glfwWindowShouldClose(window) && model.getGameState().isRunning()) {
            inputController.pollInput();
            update();
            view.updateDisplay();
        }
        view.closeDisplay();
    }

    private void togglePause() {
        if (model.getGameState().isPaused()) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    private void pauseGame() {
        model.getGameState().setPaused(true);
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        pauseMenu.setSize(WindowManager.WIDTH, WindowManager.HEIGHT);
        pauseMenu.setVisible(true);
    }

    private void resumeGame() {
        model.getGameState().setPaused(false);
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        pauseMenu.setVisible(false);
    }

    public void onQuitGame() {
        model.saveGame();
        model.getGameState().setRunning(false);
    }

    public void onSettingsPressed() {
    }

    public void update() {
        updateDeltaTime();
        playerController.update(deltaTime); // Aggiorna la fisica del giocatore
        model.update(deltaTime); // Aggiorna il modello

        EventBus.getInstance().post(
            new RenderEvent(
                model.getPlayer().getCamera(),
                model.getWorld().getVisibleBlocks(),
                model.getWorld()
            )
        );

        if (!model.getGameState().isPaused()) {
            //System.out.println("sssssssssssssss");
        }
    }

    private void updateDeltaTime() {
        long currentTime = System.nanoTime();
        deltaTime = (currentTime - lastFrameTime) / 1_000_000_000f;
        lastFrameTime = currentTime;
    }
}