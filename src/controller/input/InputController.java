package controller.input;

import controller.event.*;
import model.game.Model;
import org.lwjgl.glfw.GLFW;
import view.window.WindowManager;

/**
 * Handles raw input processing using GLFW.
 * Routes keyboard and mouse events through event system.
 * Uses singleton pattern to ensure single input handler.
 */
public class InputController {
    /** Singleton instance of controller */
    private static InputController instance;

    /** GLFW window handle */
    private final long window;

    /** Event bus for publishing input events */
    private final EventBus eventBus;

    /** Last known mouse position */
    private double lastX = 400;
    private double lastY = 300;

    /** First mouse movement flag */
    private boolean firstMouse = true;

    /** Escape key state tracking */
    private boolean escWasPressed = false;

    /** Reference to game model */
    private final Model model;


    /**
     * Private constructor for singleton.
     * Sets up input modes and event bus.
     *
     * @param window GLFW window handle
     * @param model Game model reference
     */
    private InputController(long window, Model model) {
        // Initialize core components
        this.window = window;
        this.model = model;
        this.eventBus = EventBus.getInstance();

        // Hide cursor for game input
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    /**
     * Gets singleton instance, creating if needed.
     *
     * @param window GLFW window handle
     * @param model Game model reference
     * @return InputController instance
     */
    public static InputController getInstance(long window, Model model) {
        if (instance == null) {
            instance = new InputController(window, model);
        }
        return instance;
    }

    /**
     * Gets state of specified key.
     *
     * @param key GLFW key code
     * @return 1.0f if pressed, 0.0f if not
     */
    public float getKeyState(int key) {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS ? 1.0f : 0.0f;
    }

    /**
     * Processes input events each frame.
     */
    public void pollInput() {
        handleKeyboardInput();
        handleMouseInput();
    }

    /**
     * Processes keyboard input and posts events.
     */
    private void handleKeyboardInput() {
        // Check escape key for pause toggle
        float escValue = getKeyState(GLFW.GLFW_KEY_ESCAPE);

        if (escValue > 0 && !escWasPressed) {
            EventBus.getInstance().post(MenuEvent.action(MenuAction.RESUME_GAME));
            escWasPressed = true;
        } else if (escValue == 0 && escWasPressed) {
            escWasPressed = false;
        }

        // Process movement only when unpaused
        if (!model.getGameState().isPaused()) {
            handleMovementInput();
        }
    }

    /**
     * Processes and posts movement input events.
     */
    private void handleMovementInput() {
        // Process fullscreen toggle
        eventBus.post(new InputEvent(InputAction.TOGGLE_FULLSCREEN, getKeyState(GLFW.GLFW_KEY_F11)));

        // Post movement input events
        eventBus.post(new InputEvent(InputAction.MOVE_FORWARD, getKeyState(GLFW.GLFW_KEY_W)));
        eventBus.post(new InputEvent(InputAction.MOVE_BACKWARD, getKeyState(GLFW.GLFW_KEY_S)));
        eventBus.post(new InputEvent(InputAction.MOVE_LEFT, getKeyState(GLFW.GLFW_KEY_A)));
        eventBus.post(new InputEvent(InputAction.MOVE_RIGHT, getKeyState(GLFW.GLFW_KEY_D)));
        eventBus.post(new InputEvent(InputAction.SPRINT, getKeyState(GLFW.GLFW_KEY_LEFT_SHIFT)));
        eventBus.post(new InputEvent(InputAction.MOVE_UP, getKeyState(GLFW.GLFW_KEY_SPACE)));
        eventBus.post(new InputEvent(InputAction.MOVE_DOWN, getKeyState(GLFW.GLFW_KEY_LEFT_CONTROL)));
    }

    /**
     * Processes mouse input for camera and menu.
     */
    private void handleMouseInput() {
        // Get current mouse position
        double[] xPos = new double[1];
        double[] yPos = new double[1];
        GLFW.glfwGetCursorPos(window, xPos, yPos);

        if (model.getGameState().isPaused()) {
            // Handle menu clicks when paused
            if (GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
                handlePauseMenuClick((float) xPos[0], (float) yPos[0]);
            }
        } else {
            // Handle mouse look when unpaused
            if (firstMouse) {
                lastX = xPos[0];
                lastY = yPos[0];
                firstMouse = false;
                return;
            }

            // Calculate and post mouse movement
            float dx = (float) (xPos[0] - lastX);
            float dy = (float) (yPos[0] - lastY);

            lastX = xPos[0];
            lastY = yPos[0];

            if (dx != 0) eventBus.post(new InputEvent(InputAction.LOOK_X, dx));
            if (dy != 0) eventBus.post(new InputEvent(InputAction.LOOK_Y, dy));

            // Post mouse button events for block interaction
            eventBus.post(new InputEvent(InputAction.DESTROY_BLOCK, GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS ? 1.0f : 0.0f));
            eventBus.post(new InputEvent(InputAction.PLACE_BLOCK, GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS ? 1.0f : 0.0f));
        }
    }

    /**
     * Handles mouse clicks in pause menu.
     *
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     */
    private void handlePauseMenuClick(float mouseX, float mouseY) {
        // Check each button and post corresponding event
        if (isMouseOverButton(mouseX, mouseY, (float) WindowManager.WIDTH / 2 - 50, (float) WindowManager.HEIGHT / 2, 100, 40)) {
            EventBus.getInstance().post(MenuEvent.action(MenuAction.RESUME_GAME));
        } else if (isMouseOverButton(mouseX, mouseY, (float) WindowManager.WIDTH / 2 - 50, (float) WindowManager.HEIGHT / 2 - 50, 100, 40)) {
            EventBus.getInstance().post(MenuEvent.action(MenuAction.SHOW_SETTINGS));
        } else if (isMouseOverButton(mouseX, mouseY, (float) WindowManager.WIDTH / 2 - 50, (float) WindowManager.HEIGHT / 2 - 100, 100, 40)) {
            EventBus.getInstance().post(MenuEvent.action(MenuAction.QUIT_GAME));
        }
    }

    /**
     * Checks if mouse is within button bounds.
     *
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     * @param buttonX Button left edge
     * @param buttonY Button top edge
     * @param buttonWidth Button width
     * @param buttonHeight Button height
     * @return True if mouse is over button
     */
    private boolean isMouseOverButton(float mouseX, float mouseY, float buttonX, float buttonY, float buttonWidth, float buttonHeight) {
        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }
}