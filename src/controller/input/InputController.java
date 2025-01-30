package controller.input;

import controller.event.*;
import model.Model;
import org.lwjgl.glfw.GLFW;

/**
 * Handles user input through keyboard and mouse using the GLFW library.
 * This class follows the Singleton pattern to ensure a single point of input handling
 * and maintains consistent input state across the application.
 * It processes input events and posts them to the EventBus for other components to react.
 */
public class InputController {
    /** The instance of the InputController (Singleton pattern) */
    private static InputController instance;

    /** The GLFW window handle */
    private long window;

    /** The event bus used to post input events */
    private final EventBus eventBus;

    /** The last recorded X-coordinate of the mouse position */
    private double lastX = 400;

    /** The last recorded Y-coordinate of the mouse position */
    private double lastY = 300;

    /** Flag indicating if the mouse is being moved for the first time */
    private boolean firstMouse = true;

    /** Flag for tracking key states */
    private boolean escWasPressed = false;

    private final Model model;

    /**
     * Private constructor for Singleton pattern.
     * Configures the input mode to hide the cursor and initializes the event bus.
     *
     * @param window The GLFW window handle
     * @param model  The Model instance
     */
    private InputController(long window, Model model) {
        this.window = window;
        this.model = model;  // Inizializza il riferimento al Model
        this.eventBus = EventBus.getInstance();
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    /**
     * Gets the singleton instance of the InputController.
     * Creates a new instance if one doesn't exist.
     *
     * @param window The GLFW window handle
     * @param model  The Model instance
     * @return The InputController instance
     */
    public static InputController getInstance(long window, Model model) {
        if (instance == null) {
            instance = new InputController(window, model);
        }
        return instance;
    }

    /**
     * Gets the state of a specific key.
     *
     * @param key The GLFW key code
     * @return 1.0f if the key is pressed, 0.0f otherwise
     */
    public float getKeyState(int key) {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS ? 1.0f : 0.0f;
    }

    /**
     * Polls for input events from the keyboard and mouse, posting them to the EventBus.
     * This method should be called every frame to process input.
     */
    public void pollInput() {
        handleKeyboardInput();
        handleMouseInput();
    }

    /**
     * Processes keyboard input, mapping key presses to specific actions.
     * Posts corresponding input events to the EventBus.
     */
    private void handleKeyboardInput() {
        float escValue = getKeyState(GLFW.GLFW_KEY_ESCAPE);

        // Handle ESC key press/release for view.menu toggle
        if (escValue > 0 && !escWasPressed) {
            EventBus.getInstance().post(new MenuActionEvent(MenuAction.TOGGLE_PAUSE));
            escWasPressed = true;
        } else if (escValue == 0 && escWasPressed) {
            escWasPressed = false;
        }

        // Usa il riferimento al Model per controllare lo stato del gioco
        if (!model.getGameState().isPaused()) {
            handleMovementInput();
        }
    }

    private void handleMovementInput() {
        eventBus.post(new InputEvent(InputAction.MOVE_FORWARD, getKeyState(GLFW.GLFW_KEY_W)));
        eventBus.post(new InputEvent(InputAction.MOVE_BACKWARD, getKeyState(GLFW.GLFW_KEY_S)));
        eventBus.post(new InputEvent(InputAction.MOVE_LEFT, getKeyState(GLFW.GLFW_KEY_A)));
        eventBus.post(new InputEvent(InputAction.MOVE_RIGHT, getKeyState(GLFW.GLFW_KEY_D)));
        eventBus.post(new InputEvent(InputAction.SPRINT, getKeyState(GLFW.GLFW_KEY_LEFT_SHIFT)));
        eventBus.post(new InputEvent(InputAction.MOVE_UP, getKeyState(GLFW.GLFW_KEY_SPACE)));
        eventBus.post(new InputEvent(InputAction.MOVE_DOWN, getKeyState(GLFW.GLFW_KEY_LEFT_SHIFT)));
        eventBus.post(new InputEvent(InputAction.TOGGLE_FULLSCREEN, getKeyState(GLFW.GLFW_KEY_F11)));
    }

    /**
     * Processes mouse input, including movement and button presses.
     * Posts events for mouse look and block interaction.
     */
    private void handleMouseInput() {
        double[] xPos = new double[1];
        double[] yPos = new double[1];

        GLFW.glfwGetCursorPos(window, xPos, yPos);

        if (firstMouse) {
            lastX = xPos[0];
            lastY = yPos[0];
            firstMouse = false;
            return;
        }

        float dx = (float) (xPos[0] - lastX);
        float dy = (float) (yPos[0] - lastY);

        lastX = xPos[0];
        lastY = yPos[0];

        //if(model.getGameState().isPaused()) return;

        if (dx != 0) eventBus.post(new InputEvent(InputAction.LOOK_X, dx));
        if (dy != 0) eventBus.post(new InputEvent(InputAction.LOOK_Y, dy));

        eventBus.post(new InputEvent(InputAction.DESTROY_BLOCK, GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS ? 1.0f : 0.0f));
        eventBus.post(new InputEvent(InputAction.PLACE_BLOCK, GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS ? 1.0f : 0.0f));
    }

    /**
     * Updates the window handle and cursor mode.
     * Call this when switching between game and view.menu windows.
     *
     * @param newWindow The new GLFW window handle
     */
    public void updateWindow(long newWindow) {
        this.window = newWindow;
    }
}