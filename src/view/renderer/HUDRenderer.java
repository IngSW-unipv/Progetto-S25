package view.renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import view.shader.ShaderProgram;

import java.nio.FloatBuffer;

public class HUDRenderer {
    private int vaoID;
    private int vboID;
    private ShaderProgram shader;

    private static final float[] CROSSHAIR = {
        // Vertical line
        0.0f,  0.02f, // Top
        0.0f, -0.02f, // Bottom

        // Horizontal line
        -0.02f,  0.0f, // Left
        0.02f,  0.0f  // Right
    };

    public HUDRenderer() {
        setupShader();
        setupVAO();
    }

    private void setupShader() {
        shader = new ShaderProgram(
"resources/shaders/hud_vertex.glsl",
"resources/shaders/hud_fragment.glsl"
        );
    }

    private void setupVAO() {
        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(CROSSHAIR.length);
        vertexBuffer.put(CROSSHAIR).flip();

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);

        GL30.glBindVertexArray(0);
    }

    public void render() {
        shader.start();

        GL11.glLineWidth(2.0f);
        GL30.glBindVertexArray(vaoID);

        // Draw vertical line
        GL11.glDrawArrays(GL11.GL_LINES, 0, 2);
        // Draw horizontal line
        GL11.glDrawArrays(GL11.GL_LINES, 2, 2);

        GL30.glBindVertexArray(0);
        shader.stop();
    }

    public void cleanUp() {
        GL30.glDeleteVertexArrays(vaoID);
        GL15.glDeleteBuffers(vboID);
        shader.cleanup();
    }
}