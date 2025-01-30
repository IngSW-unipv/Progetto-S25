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

public class PauseMenuRenderer {
    private final ShaderProgram shader;
    private int vaoID;
    private int vboID;
    private boolean isVisible = false;
    private final List<MenuButton> buttons = new ArrayList<>();
    private int hoveredButton = -1;
    private final long windowHandle;

    private static final float BUTTON_WIDTH = 0.4f;
    private static final float BUTTON_HEIGHT = 0.1f;
    private static final float BUTTON_SPACING = 0.15f;

    public PauseMenuRenderer(long windowHandle) {
        this.windowHandle = windowHandle;
        shader = new ShaderProgram(
                "resources/shaders/pause_menu_vertex.glsl",
                "resources/shaders/pause_menu_fragment.glsl"
        );
        setupMenu();
        setupVAO();
    }

    private void setupMenu() {
        // Position buttons vertically centered
        float startY = 0.3f;
        buttons.add(new MenuButton("Resume", 0, startY, MenuAction.RESUME_GAME));
        buttons.add(new MenuButton("Settings", 0, startY - BUTTON_SPACING, MenuAction.SHOW_SETTINGS));
        buttons.add(new MenuButton("Save and Quit", 0, startY - 2 * BUTTON_SPACING, MenuAction.QUIT_GAME));
    }

    private void setupVAO() {
        vaoID = GL30.glGenVertexArrays();
        vboID = GL15.glGenBuffers();

        GL30.glBindVertexArray(vaoID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

        // Create vertex data for background and buttons
        FloatBuffer vertexBuffer = createVertexBuffer();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        // Position attribute
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        // Color attribute
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
    }

    private FloatBuffer createVertexBuffer() {
        List<Float> vertices = new ArrayList<>();

        // Background (full screen quad)
        addQuadVertices(vertices, -1, -1, 2, 2, 0, 0);

        // Add button vertices
        for (MenuButton button : buttons) {
            addQuadVertices(vertices,
                    button.x - BUTTON_WIDTH/2, button.y - BUTTON_HEIGHT/2,
                    BUTTON_WIDTH, BUTTON_HEIGHT, 1, 1);
        }

        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.size());
        vertices.forEach(buffer::put);
        buffer.flip();
        return buffer;
    }

    private void addQuadVertices(List<Float> vertices, float x, float y, float width, float height, float s, float t) {
        // First triangle
        vertices.add(x); vertices.add(y); vertices.add(s); vertices.add(t);
        vertices.add(x + width); vertices.add(y); vertices.add(s); vertices.add(t);
        vertices.add(x + width); vertices.add(y + height); vertices.add(s); vertices.add(t);

        // Second triangle
        vertices.add(x); vertices.add(y); vertices.add(s); vertices.add(t);
        vertices.add(x + width); vertices.add(y + height); vertices.add(s); vertices.add(t);
        vertices.add(x); vertices.add(y + height); vertices.add(s); vertices.add(t);
    }

    public void render() {
        if (!isVisible) return;

        shader.start();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL30.glBindVertexArray(vaoID);

        // Draw background
        int overlayColorLoc = GL20.glGetUniformLocation(shader.getProgramID(), "overlayColor");
        GL20.glUniform4f(overlayColorLoc, 0.0f, 0.0f, 0.0f, 0.5f);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

        // Update and draw buttons
        updateButtonStates();
        drawButtons();

        GL30.glBindVertexArray(0);
        GL11.glDisable(GL11.GL_BLEND);
        shader.stop();
    }

    private void updateButtonStates() {
        if (!isVisible) return;

        double[] xPos = new double[1];
        double[] yPos = new double[1];
        GLFW.glfwGetCursorPos(windowHandle, xPos, yPos);

        // Convert screen coordinates to OpenGL coordinates
        int[] width = new int[1];
        int[] height = new int[1];
        GLFW.glfwGetWindowSize(windowHandle, width, height);
        float normalizedX = (float) (2.0f * xPos[0] / width[0] - 1.0f);
        float normalizedY = (float) (1.0f - 2.0f * yPos[0] / height[0]);

        // Check button hover states
        hoveredButton = -1;
        for (int i = 0; i < buttons.size(); i++) {
            MenuButton button = buttons.get(i);
            if (isPointInButton(normalizedX, normalizedY, button)) {
                hoveredButton = i;
                if (GLFW.glfwGetMouseButton(windowHandle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
                    EventBus.getInstance().post(MenuEvent.action(button.action));
                }
                break;
            }
        }
    }

    private boolean isPointInButton(float x, float y, MenuButton button) {
        return x >= button.x - BUTTON_WIDTH/2 && x <= button.x + BUTTON_WIDTH/2 &&
                y >= button.y - BUTTON_HEIGHT/2 && y <= button.y + BUTTON_HEIGHT/2;
    }

    private void drawButtons() {
        int overlayColorLoc = GL20.glGetUniformLocation(shader.getProgramID(), "overlayColor");

        for (int i = 0; i < buttons.size(); i++) {
            if (i == hoveredButton) {
                GL20.glUniform4f(overlayColorLoc, 0.4f, 0.4f, 0.4f, 0.8f);
            } else {
                GL20.glUniform4f(overlayColorLoc, 0.3f, 0.3f, 0.3f, 0.8f);
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