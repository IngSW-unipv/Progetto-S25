package controller;

import controller.event.*;
import model.Model;

public class GameController {
    private final Model model;
    private final PlayerController playerController;
    private long lastFrameTime;
    private float deltaTime;

    public GameController(Model model) {
        this.model = model;
        this.playerController = new PlayerController(model.getPlayer(), model.getWorld());
        this.lastFrameTime = System.nanoTime();
        EventBus.getInstance().subscribe(EventType.INPUT, this::onEvent);
    }

    public void onEvent(GameEvent event) {
        if (event instanceof InputEvent inputEvent) {
            handleInput(inputEvent);
        }
    }

    private void handleInput(InputEvent event) {
        switch (event.action()) {
            case EXIT -> {
                if (event.value() > 0) {
                    if (!model.getGameState().isPaused()) {
                        model.getGameState().setPaused(true);
                        showPauseMenu();
                    }
                }
            }
            default -> {
                if (!model.getGameState().isPaused()) {
                    playerController.handleInput(event);
                }
            }
        }
    }

    private void showPauseMenu() {

    }

    public void update() {
        updateDeltaTime();
        playerController.update(deltaTime);
        model.update(deltaTime);

        EventBus.getInstance().post(new RenderEvent(
            model.getPlayer().getCamera(),
            model.getWorld().getVisibleBlocks(),
            model.getWorld()
        ));
    }

    private void updateDeltaTime() {
        long currentTime = System.nanoTime();
        deltaTime = (currentTime - lastFrameTime) / 1_000_000_000f;
        lastFrameTime = currentTime;
    }
}