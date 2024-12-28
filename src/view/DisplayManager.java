package view;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

public class DisplayManager {

    private long window;
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    private boolean isFullscreen = false;

    public void createDisplay() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, "Game", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowPos(window, (Objects.requireNonNull(videoMode).width() - WIDTH) / 2, (Objects.requireNonNull(videoMode).height() - HEIGHT) / 2);

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
        GLFW.glfwSwapInterval(1);
        GL11.glViewport(0, 0, WIDTH, HEIGHT);
    }

    public void updateDisplay() {
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

    public void closeDisplay() {
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public long getWindow() {
        if (window == 0) {
            throw new IllegalStateException("La finestra non è stata inizializzata correttamente.");
        }
        return window;
    }

    public void toggleFullscreen() {
        if (isFullscreen) {
            // Passa alla modalità finestra
            GLFW.glfwSetWindowMonitor(window, 0, 100, 100, WIDTH, HEIGHT, GLFW.GLFW_DONT_CARE);
            isFullscreen = false;
        } else {
            // Passa alla modalità fullscreen
            GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            GLFW.glfwSetWindowMonitor(window, GLFW.glfwGetPrimaryMonitor(), 0, 0, videoMode.width(), videoMode.height(), GLFW.GLFW_DONT_CARE);
            isFullscreen = true;
        }
    }
}