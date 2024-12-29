package controller;

import controller.event.*;
import org.lwjgl.glfw.GLFW;
import view.window.WindowManager;

public class InputController {
    private final long window;
    private final EventBus eventBus;
    private boolean f11Pressed = false;
    private double lastX = 400, lastY = 300;
    private boolean firstMouse = true;

    public InputController(long window) {
        this.window = window;
        this.eventBus = EventBus.getInstance();
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    public void pollInput() {
        handleKeyboardInput();
        handleMouseInput();
    }

    private float getKeyState(int key) {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS ? 1.0f : 0.0f;
    }

    private void handleKeyboardInput() {
        eventBus.post(new InputEvent(InputAction.MOVE_FORWARD, getKeyState(GLFW.GLFW_KEY_W)));
        eventBus.post(new InputEvent(InputAction.MOVE_BACKWARD, getKeyState(GLFW.GLFW_KEY_S)));
        eventBus.post(new InputEvent(InputAction.MOVE_LEFT, getKeyState(GLFW.GLFW_KEY_A)));
        eventBus.post(new InputEvent(InputAction.MOVE_RIGHT, getKeyState(GLFW.GLFW_KEY_D)));
        eventBus.post(new InputEvent(InputAction.MOVE_UP, getKeyState(GLFW.GLFW_KEY_SPACE)));
        eventBus.post(new InputEvent(InputAction.MOVE_DOWN, getKeyState(GLFW.GLFW_KEY_LEFT_SHIFT)));
        eventBus.post(new InputEvent(InputAction.EXIT, getKeyState(GLFW.GLFW_KEY_ESCAPE)));
        eventBus.post(new InputEvent(InputAction.TOGGLE_FULLSCREEN, getKeyState(GLFW.GLFW_KEY_F11)));
    }

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

        float dx = (float)(xPos[0] - lastX);
        float dy = (float)(yPos[0] - lastY); // Removed the inversion

        lastX = xPos[0];
        lastY = yPos[0];

        if (dx != 0) eventBus.post(new InputEvent(InputAction.LOOK_X, dx));
        if (dy != 0) eventBus.post(new InputEvent(InputAction.LOOK_Y, dy));
    }
}