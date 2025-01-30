package view.window;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.util.Objects;

/**
 * The WindowManager class is responsible for creating and managing the game window,
 * including handling window resizing, fullscreen toggling, and updating the display.
 */
public class WindowManager {
    private static long window;  // The window handle
    public static final int WIDTH = 1280;  // Default window width
    public static final int HEIGHT = 720; // Default window height
    private static boolean isFullscreen = false; // Flag indicating if the window is fullscreen
    private int currentWidth = WIDTH;  // Current width of the window
    private int currentHeight = HEIGHT; // Current height of the window

    /**
     * Initializes the GLFW window system and creates the game window.
     * Sets up OpenGL context and window hints, and centers the window on the screen.
     */
    public void createDisplay() {
        // Set GLFW error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Set window hints
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        // Create the window
        window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, "Voxel", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to create window");
        }

        // Center the window on the screen
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            GLFW.glfwSetWindowPos(window,
                    (Objects.requireNonNull(vidmode).width() - WIDTH) / 2,
                    (Objects.requireNonNull(vidmode).height() - HEIGHT) / 2
            );
        }

        // Set up window resize callback
        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            currentWidth = width;
            currentHeight = height;
            updateViewport();
        });

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // Set clear color and update the viewport
        GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        updateViewport();

        // Enable V-Sync
        GLFW.glfwSwapInterval(1);
    }

    /**
     * Updates the viewport to match the current window size.
     */
    private void updateViewport() {
        GL11.glViewport(0, 0, currentWidth, currentHeight);
    }

    /**
     * Toggles between fullscreen and windowed mode.
     * Adjusts the window size and position accordingly.
     */
    public void toggleFullscreen() {
        if (isFullscreen) {
            // Switch to windowed mode
            GLFW.glfwSetWindowMonitor(window, 0, 100, 100, WIDTH, HEIGHT, GLFW.GLFW_DONT_CARE);
            currentWidth = WIDTH;
            currentHeight = HEIGHT;
        } else {
            // Switch to fullscreen mode
            GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            GLFW.glfwSetWindowMonitor(window, GLFW.glfwGetPrimaryMonitor(), 0, 0,
                    Objects.requireNonNull(vidmode).width(), Objects.requireNonNull(vidmode).height(), vidmode.refreshRate());
            currentWidth = vidmode.width();
            currentHeight = vidmode.height();
        }
        isFullscreen = !isFullscreen;
        updateViewport();
    }

    /**
     * Updates the display by swapping buffers and polling for events.
     */
    public void updateDisplay() {
        GLFW.glfwSwapBuffers(window);  // Swap the front and back buffers
        GLFW.glfwPollEvents();         // Poll for window events
    }

    /**
     * Closes the display and terminates GLFW.
     */
    public void closeDisplay() {
        GLFW.glfwDestroyWindow(window);  // Distrugge la finestra
        GLFW.glfwTerminate();            // Termina GLFW
        System.exit(0);            // Forza la terminazione del processo
    }

    /**
     * Retrieves the window handle.
     *
     * @return the window handle.
     * @throws IllegalStateException if the window has not been properly initialized.
     */
    public long getWindow() {
        if (window == 0) {
            throw new IllegalStateException("Window not properly initialized");
        }
        return window;
    }

    /**
     * Gets the aspect ratio of the window based on its current width and height.
     *
     * @return the aspect ratio of the window.
     */
    public float getAspectRatio() {
        return (float) currentWidth / currentHeight;
    }
}