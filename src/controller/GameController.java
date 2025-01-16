package controller;

import controller.event.*;
import model.Model;

/**
 * The GameController class listens for input events and handles the game logic.
 * It processes player input, updates the game state, and manages the camera movement.
 */
public class GameController implements EventListener {
    private final Model model; // The game model, which contains the game state and logic
    private boolean forward, backward, left, right, up, down; // Movement flags for the player

    private long lastFrameTime = System.nanoTime(); // Time of the last frame for delta time calculation
    private float deltaTime; // Time difference between frames

    /**
     * Constructor for the GameController class.
     * Subscribes to input events from the EventBus.
     * @param model The game model that holds the game state and logic
     */
    public GameController(Model model) {
        this.model = model;
        EventBus.getInstance().subscribe(EventType.INPUT, this); // Subscribe to input events
    }

    /**
     * Handles events posted to the EventBus.
     * Specifically processes input events to update player actions.
     * @param event The game event to handle
     */
    public void onEvent(GameEvent event) {
        if (event instanceof InputEvent inputEvent) {
            handleInput(inputEvent); // Handle the input event
        }
    }

    /**
     * Processes an input event and updates the movement flags or performs actions accordingly.
     * @param event The input event to handle
     */
    private void handleInput(InputEvent event) {
        switch (event.action()) {
            case MOVE_FORWARD -> forward = event.value() > 0; // Move forward if the value is greater than 0
            case MOVE_BACKWARD -> backward = event.value() > 0; // Move backward if the value is greater than 0
            case MOVE_LEFT -> left = event.value() > 0; // Move left if the value is greater than 0
            case MOVE_RIGHT -> right = event.value() > 0; // Move right if the value is greater than 0
            case MOVE_UP -> up = event.value() > 0; // Move up if the value is greater than 0
            case MOVE_DOWN -> down = event.value() > 0; // Move down if the value is greater than 0
            case LOOK_X -> model.getCamera().rotate(event.value(), 0); // Rotate the camera along the X axis
            case LOOK_Y -> model.getCamera().rotate(0, event.value()); // Rotate the camera along the Y axis
            case PLACE_BLOCK -> {
                if (event.value() > 0) {
                    model.placeBlock(); // Place a block if the value is greater than 0
                }
            }
            case DESTROY_BLOCK -> {
                if (event.value() > 0) {
                    model.startBreaking(); // Start breaking a block if the value is greater than 0
                } else {
                    model.stopBreaking(); // Stop breaking the block if the value is 0 or less
                }
            }
            case EXIT -> {
                if (event.value() > 0) {
                    model.getGameState().setRunning(false); // Stop the game if the value is greater than 0
                }
            }
        }

        model.getCamera().move(forward, backward, left, right, up, down, deltaTime); // Update camera movement based on flags
    }

    /**
     * Updates the game state by calculating the delta time and calling the model's update method.
     */
    public void update() {
        updateDeltaTime(); // Update the delta time
        model.updateGame(deltaTime); // Update the game logic with the calculated delta time
    }

    /**
     * Calculates the time difference between the current and previous frame (delta time).
     */
    private void updateDeltaTime() {
        long currentTime = System.nanoTime(); // Get the current time in nanoseconds
        deltaTime = (currentTime - lastFrameTime) / 1_000_000_000f; // Convert the time difference to seconds
        lastFrameTime = currentTime; // Update the last frame time
    }
}