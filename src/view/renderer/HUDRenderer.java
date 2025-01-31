package view.renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import view.shader.ShaderProgram;

import java.nio.FloatBuffer;

/**
 * Responsible for rendering the Heads-Up Display (HUD) elements, such as a crosshair.
 * This class manages the setup of shaders, buffers, and the rendering of HUD elements in OpenGL.
 */
public class HUDRenderer {
    private int vaoID;
    private int vboID;
    private ShaderProgram shader;

    // Coordinates for the crosshair (vertical and horizontal lines)
    private static final float[] CROSSHAIR = {
        // Vertical line
        0.0f,  0.02f, // Top
        0.0f, -0.02f, // Bottom

        // Horizontal line
        -0.02f,  0.0f, // Left
        0.02f,  0.0f  // Right
    };

    /**
     * Constructs a new HUDRenderer.
     * Sets up the shader and the necessary OpenGL buffers for rendering the HUD.
     */
    public HUDRenderer() {
        setupShader();
        setupVAO();
    }

    /**
     * Sets up the shader program for rendering the HUD elements.
     * Loads the vertex and fragment shaders from the specified file paths.
     */
    private void setupShader() {
        shader = new ShaderProgram(
    "resources/shaders/hud_vertex.glsl",
    "resources/shaders/hud_fragment.glsl"
        );
    }

    /**
     * Sets up the Vertex Array Object (VAO) and Vertex Buffer Object (VBO) for rendering the crosshair.
     * It stores the crosshair coordinates in a buffer and configures the vertex attributes.
     */
    private void setupVAO() {
        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

        // Create a buffer for the crosshair vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(CROSSHAIR.length);
        vertexBuffer.put(CROSSHAIR).flip();

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);

        GL30.glBindVertexArray(0);
    }

    /**
     * Renders the HUD elements (e.g., crosshair) to the screen.
     * This method binds the shader and VAO, and draws the crosshair using OpenGL's line drawing functionality.
     */
    public void render() {
        shader.start();

        GL11.glLineWidth(2.0f);
        GL30.glBindVertexArray(vaoID);

        // Draw the vertical line of the crosshair
        GL11.glDrawArrays(GL11.GL_LINES, 0, 2);
        // Draw the horizontal line of the crosshair
        GL11.glDrawArrays(GL11.GL_LINES, 2, 2);

        GL30.glBindVertexArray(0);
        shader.stop();
    }

    /**
     * Cleans up the OpenGL resources used by the HUD renderer.
     * This includes deleting the VAO, VBO, and cleaning up the shader program.
     */
    public void cleanUp() {
        GL30.glDeleteVertexArrays(vaoID);
        GL15.glDeleteBuffers(vboID);
        shader.cleanup();
    }
}