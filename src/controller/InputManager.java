package controller;

import org.lwjgl.glfw.GLFW;
import view.DisplayManager;

public class InputManager {

    private final long window;
    private DisplayManager displayManager;
    private boolean f11Pressed = false;  // Flag per evitare esecuzioni multiple durante il "press"

    public InputManager(long window, DisplayManager displayManager) {
        this.window = window;
        this.displayManager = displayManager;
    }

    public void pollInput() {
        // Gestisci il tasto ESC per chiudere la finestra
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            GLFW.glfwSetWindowShouldClose(window, true);
        }

        // Gestisci il tasto F11 per il toggle fullscreen
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_F11) == GLFW.GLFW_PRESS && !f11Pressed) {
            displayManager.toggleFullscreen();  // Toggle fullscreen
            f11Pressed = true;  // Imposta il flag per evitare esecuzioni multiple
        }

        // Rilascia il tasto F11 per poterlo premere di nuovo
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_F11) == GLFW.GLFW_RELEASE) {
            f11Pressed = false;  // Reset del flag quando il tasto viene rilasciato
        }
    }
}
