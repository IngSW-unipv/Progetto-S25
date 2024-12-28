package controller;

import controller.event.*;
import org.lwjgl.glfw.GLFW;

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
        handleFullscreen();
    }

    private void handleFullscreen() {
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_F11) == GLFW.GLFW_PRESS && !f11Pressed) {
            eventBus.post(new InputEvent(InputAction.TOGGLE_FULLSCREEN, 1.0f));
            f11Pressed = true;
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_F11) == GLFW.GLFW_RELEASE) {
            f11Pressed = false;
        }
    }

    private void handleKeyboardInput() {
        eventBus.post(new InputEvent(InputAction.MOVE_FORWARD,
                GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS ? 1.0f : 0.0f));
        eventBus.post(new InputEvent(InputAction.MOVE_BACKWARD,
                GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS ? 1.0f : 0.0f));
        eventBus.post(new InputEvent(InputAction.MOVE_LEFT,
                GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS ? 1.0f : 0.0f));
        eventBus.post(new InputEvent(InputAction.MOVE_RIGHT,
                GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS ? 1.0f : 0.0f));
        eventBus.post(new InputEvent(InputAction.MOVE_UP,
                GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS ? 1.0f : 0.0f));
        eventBus.post(new InputEvent(InputAction.MOVE_DOWN,
                GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS ? 1.0f : 0.0f));

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            GLFW.glfwSetWindowShouldClose(window, true);
        }
    }

    private void handleMouseInput() {
        double[] xpos = new double[1];
        double[] ypos = new double[1];
        GLFW.glfwGetCursorPos(window, xpos, ypos);

        if (firstMouse) {
            lastX = xpos[0];
            lastY = ypos[0];
            firstMouse = false;
            return;
        }

        float dx = (float)(xpos[0] - lastX);
        float dy = (float)(ypos[0] - lastY); // Removed the inversion

        lastX = xpos[0];
        lastY = ypos[0];

        if (dx != 0) eventBus.post(new InputEvent(InputAction.LOOK_X, dx));
        if (dy != 0) eventBus.post(new InputEvent(InputAction.LOOK_Y, dy));
    }
}