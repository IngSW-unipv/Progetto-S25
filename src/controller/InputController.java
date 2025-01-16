package controller;

import controller.event.EventBus;
import controller.event.InputAction;
import controller.event.InputEvent;
import org.lwjgl.glfw.GLFW;

/**
 * The InputController class handles input from the user through the keyboard and mouse.
 * It polls for input events and posts them to the EventBus for further processing.
 */
public class InputController {
    private final long window; // The window handle for GLFW
    private final EventBus eventBus; // The event bus to post input events
    private double lastX = 400, lastY = 300; // The last mouse position (initialized to center of screen)
    private boolean firstMouse = true; // Flag to handle the first mouse movement

    /**
     * Constructor for the InputController class.
     * Initializes the GLFW input mode to hide the cursor and subscribes to input events.
     * @param window The window handle for GLFW
     */
    public InputController(long window) {
        this.window = window;
        this.eventBus = EventBus.getInstance();
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED); // Hide cursor when moving the mouse
    }

    /**
     * Polls for input from the keyboard and mouse.
     * Posts the respective input events to the EventBus.
     */
    public void pollInput() {
        handleKeyboardInput(); // Handle keyboard input events
        handleMouseInput(); // Handle mouse input events
    }

    /**
     * Returns the state of a given key (pressed or not).
     * @param key The GLFW key code
     * @return 1.0f if the key is pressed, 0.0f otherwise
     */
    private float getKeyState(int key) {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS ? 1.0f : 0.0f; // Check if key is pressed
    }

    /**
     * Handles the keyboard input by posting relevant events to the EventBus.
     * Each key press is mapped to a specific action.
     */
    private void handleKeyboardInput() {
        eventBus.post(new InputEvent(InputAction.MOVE_FORWARD, getKeyState(GLFW.GLFW_KEY_W))); // Move forward
        eventBus.post(new InputEvent(InputAction.MOVE_BACKWARD, getKeyState(GLFW.GLFW_KEY_S))); // Move backward
        eventBus.post(new InputEvent(InputAction.MOVE_LEFT, getKeyState(GLFW.GLFW_KEY_A))); // Move left
        eventBus.post(new InputEvent(InputAction.MOVE_RIGHT, getKeyState(GLFW.GLFW_KEY_D))); // Move right
        eventBus.post(new InputEvent(InputAction.MOVE_UP, getKeyState(GLFW.GLFW_KEY_SPACE))); // Move up (jump)
        eventBus.post(new InputEvent(InputAction.MOVE_DOWN, getKeyState(GLFW.GLFW_KEY_LEFT_SHIFT))); // Move down (crouch)
        eventBus.post(new InputEvent(InputAction.EXIT, getKeyState(GLFW.GLFW_KEY_ESCAPE))); // Exit the game
        eventBus.post(new InputEvent(InputAction.TOGGLE_FULLSCREEN, getKeyState(GLFW.GLFW_KEY_F11))); // Toggle fullscreen
    }

    /**
     * Handles the mouse input by calculating mouse movement and button presses.
     * Posts relevant events to the EventBus for mouse look and block actions.
     */
    private void handleMouseInput() {
        double[] xPos = new double[1]; // Array to hold the x position of the cursor
        double[] yPos = new double[1]; // Array to hold the y position of the cursor
        GLFW.glfwGetCursorPos(window, xPos, yPos); // Get the current cursor position

        if (firstMouse) { // If it's the first mouse movement
            lastX = xPos[0]; // Set the last X position to current
            lastY = yPos[0]; // Set the last Y position to current
            firstMouse = false; // Disable first mouse flag
            return; // Skip the rest of the code
        }

        float dx = (float)(xPos[0] - lastX); // Calculate the difference in the X position
        float dy = (float)(yPos[0] - lastY); // Calculate the difference in the Y position

        lastX = xPos[0]; // Update the last X position
        lastY = yPos[0]; // Update the last Y position

        if (dx != 0) eventBus.post(new InputEvent(InputAction.LOOK_X, dx)); // Post X-axis mouse look event
        if (dy != 0) eventBus.post(new InputEvent(InputAction.LOOK_Y, dy)); // Post Y-axis mouse look event

        // Post events for mouse button presses (left for destroy, right for place)
        eventBus.post(new InputEvent(InputAction.DESTROY_BLOCK, GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS ? 1.0f : 0.0f));
        eventBus.post(new InputEvent(InputAction.PLACE_BLOCK, GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS ? 1.0f : 0.0f));
    }
}
