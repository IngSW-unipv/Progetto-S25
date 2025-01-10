package controller;

import controller.event.*;
import model.Model;

public class GameController implements EventListener {
    private final Model model;
    private final EventBus eventBus;
    private boolean forward, backward, left, right, up, down;

    public GameController(Model model) { // Modificato
        this.model = model;

        this.eventBus = EventBus.getInstance();
        eventBus.subscribe(EventType.INPUT, this);
    }

    @Override
    public void onEvent(GameEvent event) {
        if (event instanceof InputEvent inputEvent) {
            handleInput(inputEvent);
        }
    }

    private void handleInput(InputEvent event) {
        switch (event.getAction()) {
            case MOVE_FORWARD -> forward = event.getValue() > 0;
            case MOVE_BACKWARD -> backward = event.getValue() > 0;
            case MOVE_LEFT -> left = event.getValue() > 0;
            case MOVE_RIGHT -> right = event.getValue() > 0;
            case MOVE_UP -> up = event.getValue() > 0;
            case MOVE_DOWN -> down = event.getValue() > 0;
            case LOOK_X -> model.getCamera().rotate(event.getValue(), 0);
            case LOOK_Y -> model.getCamera().rotate(0, event.getValue());
            case EXIT -> {
                if (event.getValue() > 0) {
                    model.getGameState().setRunning(false);
                }
            }
        }

        model.getCamera().move(forward, backward, left, right, up, down);
    }

    public void update() {
        model.updateGame();
    }
}