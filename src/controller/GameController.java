package controller;

import controller.event.*;
import model.Model;

public class GameController {
    private final Model model;
    private long lastFrameTime = System.nanoTime();
    private float deltaTime;

    public GameController(Model model) {
        this.model = model;
    }

    public void update() {
        updateDeltaTime();
        model.update(deltaTime);
        EventBus.getInstance().post(new RenderEvent(
                model.getCamera(),
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