package view.window;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

public class WindowManager {
    private static long window;
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static boolean isFullscreen = false;
    private int currentWidth = WIDTH;
    private int currentHeight = HEIGHT;

    // Store window position and size
    private int lastWindowX = 100;
    private int lastWindowY = 100;
    private int lastWindowWidth = WIDTH;
    private int lastWindowHeight = HEIGHT;

    public void createDisplay() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, "Game", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to create window");
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            GLFW.glfwSetWindowPos(window,
                    (vidmode.width() - WIDTH) / 2,
                    (vidmode.height() - HEIGHT) / 2
            );
        }

        // Set up window resize callback
        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            currentWidth = width;
            currentHeight = height;
            updateViewport();
        });

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        updateViewport();
        GLFW.glfwSwapInterval(1);
    }

    private void updateViewport() {
        GL11.glViewport(0, 0, currentWidth, currentHeight);
    }

    public void toggleFullscreen() {
        if (isFullscreen) {
            // Switch to windowed mode
            GLFW.glfwSetWindowMonitor(window, 0, 100, 100, WIDTH, HEIGHT, GLFW.GLFW_DONT_CARE);
            currentWidth = WIDTH;
            currentHeight = HEIGHT;
        } else {
            // Switch to fullscreen
            GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            GLFW.glfwSetWindowMonitor(window, GLFW.glfwGetPrimaryMonitor(), 0, 0,
                    vidmode.width(), vidmode.height(), vidmode.refreshRate());
            currentWidth = vidmode.width();
            currentHeight = vidmode.height();
        }
        isFullscreen = !isFullscreen;
        updateViewport();
    }

    public void updateDisplay() {
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }

    public void closeDisplay() {
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public long getWindow() {
        if (window == 0) {
            throw new IllegalStateException("Window not properly initialized");
        }
        return window;
    }

    public float getAspectRatio() {
        return (float) currentWidth / currentHeight;
    }
}