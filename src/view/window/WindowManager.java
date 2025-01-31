package view.window;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.util.Objects;

/**
 * Manages GLFW window creation, updates and OpenGL context.
 * Handles window events, resizing and fullscreen toggling.
 */
public class WindowManager {
    /** GLFW window handle */
    private static long window;

    /** Default window dimensions */
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    /** Window state tracking */
    private static boolean isFullscreen = false;
    private int currentWidth = WIDTH;
    private int currentHeight = HEIGHT;


    /**
     * Initializes GLFW and creates the game window
     */
    public void createDisplay() {
        // Initialize GLFW with error callback
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure window hints
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

        // Center on screen
        try (MemoryStack ignored = MemoryStack.stackPush()) {
            GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            GLFW.glfwSetWindowPos(window,
                (Objects.requireNonNull(vidmode).width() - WIDTH) / 2,
                (Objects.requireNonNull(vidmode).height() - HEIGHT) / 2
            );
        }

        // Setup resize callback
        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            currentWidth = width;
            currentHeight = height;
            updateViewport();
        });

        // Initialize OpenGL context
        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // Configure initial state
        GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        updateViewport();

        // Enable V-Sync
        GLFW.glfwSwapInterval(1);
    }

    /**
     * Updates viewport dimensions after window resize
     */
    private void updateViewport() {
        GL11.glViewport(0, 0, currentWidth, currentHeight);
    }

    /**
     * Switches between windowed and fullscreen modes
     */
    public void toggleFullscreen() {
        if (isFullscreen) {
            // Return to windowed mode
            GLFW.glfwSetWindowMonitor(window, 0, 100, 100, WIDTH, HEIGHT, GLFW.GLFW_DONT_CARE);
            currentWidth = WIDTH;
            currentHeight = HEIGHT;
        } else {
            // Switch to fullscreen
            GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            GLFW.glfwSetWindowMonitor(window, GLFW.glfwGetPrimaryMonitor(), 0, 0,
                Objects.requireNonNull(vidmode).width(), Objects.requireNonNull(vidmode).height(),
                vidmode.refreshRate());
            currentWidth = vidmode.width();
            currentHeight = vidmode.height();
        }
        isFullscreen = !isFullscreen;
        updateViewport();
    }

    /**
     * Swaps buffers and polls for window events
     */
    public void updateDisplay() {
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }

    /**
     * Destroys window and terminates GLFW
     */
    public void closeDisplay() {
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
        System.exit(0);
    }

    /**
     * Gets the window handle
     * @throws IllegalStateException if window not initialized
     */
    public long getWindow() {
        if (window == 0) {
            throw new IllegalStateException("Window not properly initialized");
        }
        return window;
    }

    /**
     * Gets current window aspect ratio
     */
    public float getAspectRatio() {
        return (float) currentWidth / currentHeight;
    }
}