package controller;

import controller.event.EventBus;
import controller.event.RenderEvent;
import model.Model;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import view.View;

public class GameController {
    public final Model model;
    public final View view;
    private final PlayerController playerController;
    private final InputController inputController;
    private long lastFrameTime;
    private float deltaTime;
    private final long window;

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

    private void startGameLoop() {
        while (!GLFW.glfwWindowShouldClose(window) && model.getGameState().isRunning()) {
            inputController.pollInput();
            update();
            view.updateDisplay();
        }
        view.closeDisplay();
    }

    public void onResumeGame() {
        model.getGameState().setPaused(false); // Riprendi il gioco
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED); // Ripristina il cursore

        // Resetta la velocità del giocatore
        model.getPlayer().setVelocity(new Vector3f(0, 0, 0));

        // Forza un aggiornamento del rendering
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapBuffers(window);

        System.out.println("Player position after resume: " + model.getPlayer().getPosition());
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