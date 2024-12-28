package controller;

import org.lwjgl.glfw.GLFW;
import view.DisplayManager;
import model.Camera;

public class InputManager {
    private final long window;
    private DisplayManager displayManager;
    private boolean f11Pressed = false;

    private double lastX = 400;
    private double lastY = 300;
    private boolean firstMouse = true;

    public InputManager(long window) {
        this.window = window;
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    public void pollInput(Camera camera) {
        // Movement input
        boolean forward = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS;
        boolean back = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS;
        boolean left = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS;
        boolean right = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS;
        boolean up = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS;
        boolean down = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS;

        camera.move(forward, back, left, right, up, down);

        // Mouse input
        double[] xpos = new double[1];
        double[] ypos = new double[1];
        GLFW.glfwGetCursorPos(window, xpos, ypos);

        if (firstMouse) {
            lastX = xpos[0];
            lastY = ypos[0];
            firstMouse = false;
        }

        float dx = (float)(xpos[0] - lastX);
        float dy = (float)(ypos[0] - lastY);

        camera.rotate(dx, dy);

        lastX = xpos[0];
        lastY = ypos[0];

        // Additional controls
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            GLFW.glfwSetWindowShouldClose(window, true);
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_F11) == GLFW.GLFW_PRESS && !f11Pressed) {
            displayManager.toggleFullscreen();
            f11Pressed = true;
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_F11) == GLFW.GLFW_RELEASE) {
            f11Pressed = false;
        }
    }
}