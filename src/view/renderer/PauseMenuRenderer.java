package view.renderer;

import controller.event.EventBus;
import controller.event.MenuAction;
import controller.event.MenuEvent;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import view.shader.ShaderProgram;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders the pause menu interface using OpenGL.
 * Manages menu buttons, input handling, and visual effects.
 */
public class PauseMenuRenderer {
    /** Shader and buffer objects */
    private final ShaderProgram shader;
    private int vaoID;
    private int vboID;

    /** Menu state */
    private boolean isVisible = false;
    private final List<MenuButton> buttons = new ArrayList<>();
    private int hoveredButton = -1;
    private final long windowHandle;


    /** Button dimensions and layout */
    private static final float BUTTON_WIDTH = 0.4f;
    private static final float BUTTON_HEIGHT = 0.1f;
    private static final float BUTTON_SPACING = 0.15f;

    /**
     * Creates menu renderer with shaders and buttons
     */
    public PauseMenuRenderer(long windowHandle) {
        this.windowHandle = windowHandle;
        shader = new ShaderProgram(
            "resources/shaders/pause_menu_vertex.glsl",
            "resources/shaders/pause_menu_fragment.glsl"
        );
        setupMenu();
        setupVAO();
    }

    /**
     * Initializes menu buttons and positions
     */
    private void setupMenu() {
        float startY = 0.3f;
        buttons.add(new MenuButton("Resume", 0, startY, MenuAction.RESUME_GAME));
        buttons.add(new MenuButton("Settings", 0, startY - BUTTON_SPACING,
                MenuAction.SHOW_SETTINGS));
        buttons.add(new MenuButton("Save and Quit", 0, startY - 2 * BUTTON_SPACING,
                MenuAction.QUIT_GAME));
    }

    /**
     * Creates VAO/VBO for menu geometry
     */
    private void setupVAO() {
        vaoID = GL30.glGenVertexArrays();
        vboID = GL15.glGenBuffers();

        GL30.glBindVertexArray(vaoID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

        FloatBuffer vertexBuffer = createVertexBuffer();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        // Configure attributes
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES,
                2 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
    }

    /**
     * Creates vertex buffer for background and buttons
     */
    private FloatBuffer createVertexBuffer() {
        List<Float> vertices = new ArrayList<>();

        // Background quad
        addQuadVertices(vertices, -1, -1, 2, 2, 0, 0);

        // Button quads
        for (MenuButton button : buttons) {
            addQuadVertices(vertices,
                button.x - BUTTON_WIDTH/2,
                button.y - BUTTON_HEIGHT/2,
                BUTTON_WIDTH, BUTTON_HEIGHT, 1, 1);
        }

        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.size());
        vertices.forEach(buffer::put);
        buffer.flip();
        return buffer;
    }

    /**
     * Adds quad vertices to vertex list
     */
    private void addQuadVertices(List<Float> vertices, float x, float y,
                                 float width, float height, float s, float t) {
        // First triangle
        vertices.add(x); vertices.add(y); vertices.add(s); vertices.add(t);
        vertices.add(x + width); vertices.add(y); vertices.add(s); vertices.add(t);
        vertices.add(x + width); vertices.add(y + height); vertices.add(s); vertices.add(t);

        // Second triangle
        vertices.add(x); vertices.add(y); vertices.add(s); vertices.add(t);
        vertices.add(x + width); vertices.add(y + height); vertices.add(s); vertices.add(t);
        vertices.add(x); vertices.add(y + height); vertices.add(s); vertices.add(t);
    }

    /**
     * Renders menu if visible
     */
    public void render() {
        if (!isVisible) return;

        shader.start();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL30.glBindVertexArray(vaoID);

        renderBackground();
        updateButtonStates();
        renderButtons();

        GL30.glBindVertexArray(0);
        GL11.glDisable(GL11.GL_BLEND);
        shader.stop();
    }

    /**
     * Renders semi-transparent background
     */
    private void renderBackground() {
        int colorLoc = GL20.glGetUniformLocation(shader.getProgramID(), "overlayColor");
        GL20.glUniform4f(colorLoc, 0.0f, 0.0f, 0.0f, 0.5f);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
    }

    /**
     * Updates button hover states and handles clicks
     */
    private void updateButtonStates() {
        if (!isVisible) return;

        float[] mousePos = getMousePosition();

        hoveredButton = -1;
        for (int i = 0; i < buttons.size(); i++) {
            MenuButton button = buttons.get(i);
            if (isPointInButton(mousePos[0], mousePos[1], button)) {
                hoveredButton = i;
                if (GLFW.glfwGetMouseButton(windowHandle, GLFW.GLFW_MOUSE_BUTTON_LEFT)
                        == GLFW.GLFW_PRESS) {
                    EventBus.getInstance().post(MenuEvent.action(button.action));
                }
                break;
            }
        }
    }

    /**
     * Gets normalized mouse coordinates
     */
    private float[] getMousePosition() {
        double[] xPos = new double[1];
        double[] yPos = new double[1];
        GLFW.glfwGetCursorPos(windowHandle, xPos, yPos);

        int[] width = new int[1];
        int[] height = new int[1];
        GLFW.glfwGetWindowSize(windowHandle, width, height);

        return new float[] {
            (float) (2.0f * xPos[0] / width[0] - 1.0f),
            (float) (1.0f - 2.0f * yPos[0] / height[0])
        };
    }

    /**
     * Checks if point is within button bounds
     */
    private boolean isPointInButton(float x, float y, MenuButton button) {
        return x >= button.x - BUTTON_WIDTH/2 && x <= button.x + BUTTON_WIDTH/2 &&
                y >= button.y - BUTTON_HEIGHT/2 && y <= button.y + BUTTON_HEIGHT/2;
    }

    /**
     * Renders all buttons with hover effects
     */
    private void renderButtons() {
        int colorLoc = GL20.glGetUniformLocation(shader.getProgramID(), "overlayColor");

        for (int i = 0; i < buttons.size(); i++) {
            if (i == hoveredButton) {
                GL20.glUniform4f(colorLoc, 0.4f, 0.4f, 0.4f, 0.8f);
            } else {
                GL20.glUniform4f(colorLoc, 0.3f, 0.3f, 0.3f, 0.8f);
            }
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 6 + i * 6, 6);
        }
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public void cleanUp() {
        shader.cleanup();
        GL30.glDeleteVertexArrays(vaoID);
        GL15.glDeleteBuffers(vboID);
    }

    /**
     * Represents a clickable menu button
     */
    private static class MenuButton {
        final String text;
        final float x;
        final float y;
        final MenuAction action;

        MenuButton(String text, float x, float y, MenuAction action) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.action = action;
        }
    }
}