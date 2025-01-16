package controller.event;

public enum InputAction {
    MOVE_FORWARD, // Move the player forward
    MOVE_BACKWARD, // Move the player backward
    MOVE_LEFT, // Move the player to the left
    MOVE_RIGHT, // Move the player to the right
    MOVE_UP, // Move the player upwards
    MOVE_DOWN, // Move the player downwards
    LOOK_X, // Rotate the camera along the X-axis
    LOOK_Y, // Rotate the camera along the Y-axis
    TOGGLE_FULLSCREEN, // Toggle fullscreen mode
    PLACE_BLOCK, // Place a block in the world
    DESTROY_BLOCK, // Destroy a block in the world
    EXIT // Exit the game
}
