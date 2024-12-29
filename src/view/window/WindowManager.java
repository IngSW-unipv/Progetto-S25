package view.window;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.util.Objects;

public class WindowManager {

    private long window;
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    private boolean isFullscreen = false;

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

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        System.out.println("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION));
        System.out.println("Vendor: " + GL11.glGetString(GL11.GL_VENDOR));
        System.out.println("Renderer: " + GL11.glGetString(GL11.GL_RENDERER));

        GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        GL11.glViewport(0, 0, WIDTH, HEIGHT);
        GLFW.glfwSwapInterval(1);
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