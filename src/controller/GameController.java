package controller;

import controller.event.*;
import model.Model;

public class GameController implements EventListener {
    private final Model model;
    private boolean forward, backward, left, right, up, down;

    private long lastFrameTime = System.nanoTime();
    private float deltaTime;

    public GameController(Model model) {
        this.model = model;

        EventBus.getInstance().subscribe(EventType.INPUT, this);
    }

    public void onEvent(GameEvent event) {
        if (event instanceof InputEvent inputEvent) {
            handleInput(inputEvent);
        }
    }

    private void handleInput(InputEvent event) {
        switch (event.action()) {
            case MOVE_FORWARD -> forward = event.value() > 0;
            case MOVE_BACKWARD -> backward = event.value() > 0;
            case MOVE_LEFT -> left = event.value() > 0;
            case MOVE_RIGHT -> right = event.value() > 0;
            case MOVE_UP -> up = event.value() > 0;
            case MOVE_DOWN -> down = event.value() > 0;
            case LOOK_X -> model.getCamera().rotate(event.value(), 0);
            case LOOK_Y -> model.getCamera().rotate(0, event.value());
            case EXIT -> {
                if (event.value() > 0) {
                    model.getGameState().setRunning(false);
                }
            }
        }

        model.getCamera().move(forward, backward, left, right, up, down, deltaTime);
    }

    public void update() {
        updateDeltaTime();
        model.updateGame();
    }

    private void updateDeltaTime() {
        long currentTime = System.nanoTime();
        deltaTime = (currentTime - lastFrameTime) / 1_000_000_000f; // Converti in secondi
        lastFrameTime = currentTime;
    }
}