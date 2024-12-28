package view;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import model.Camera;

import org.joml.Matrix4f;

public class MasterRenderer {
    private int vaoID;
    private int vboID;
    private int eboID;
    private int vertexCount;

    private ShaderProgram shader;
    private Matrix4f projectionMatrix;

    private Texture texture;

    public MasterRenderer() {
        shader = new ShaderProgram("resources/vertex.glsl", "resources/fragment.glsl");
        texture = new Texture("resources/dirt.png");
        projectionMatrix = new Matrix4f().perspective(
                (float) Math.toRadians(70.0f),
                1280.0f / 720.0f,
                0.1f,
                1000.0f
        );
        createCube(); // Aggiungi questa chiamata

        // Abilita il depth testing
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public void render(Camera camera) {
        shader.start();
        shader.loadMatrix("viewMatrix", camera.getViewMatrix());
        shader.loadMatrix("projectionMatrix", projectionMatrix);

        texture.bind(0); // Usa texture slot 0

        GL30.glBindVertexArray(vaoID);
        GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);

        shader.stop();
    }


    private void createCube() {
        float[] vertices = {
                // Front face
                -0.5f,  0.5f,  0.5f,   0.0f, 0.0f,  // top-left
                -0.5f, -0.5f,  0.5f,   0.0f, 1.0f,  // bottom-left
                0.5f, -0.5f,  0.5f,   1.0f, 1.0f,  // bottom-right
                0.5f,  0.5f,  0.5f,   1.0f, 0.0f,  // top-right

                // Back face
                -0.5f,  0.5f, -0.5f,   0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f,   0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,   1.0f, 1.0f,
                0.5f,  0.5f, -0.5f,   1.0f, 0.0f,

                // Top face
                -0.5f,  0.5f, -0.5f,   0.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,   0.0f, 1.0f,
                0.5f,  0.5f,  0.5f,   1.0f, 1.0f,
                0.5f,  0.5f, -0.5f,   1.0f, 0.0f,

                // Bottom face
                -0.5f, -0.5f, -0.5f,   0.0f, 0.0f,
                -0.5f, -0.5f,  0.5f,   0.0f, 1.0f,
                0.5f, -0.5f,  0.5f,   1.0f, 1.0f,
                0.5f, -0.5f, -0.5f,   1.0f, 0.0f,

                // Right face
                0.5f,  0.5f,  0.5f,   0.0f, 0.0f,
                0.5f, -0.5f,  0.5f,   0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,   1.0f, 1.0f,
                0.5f,  0.5f, -0.5f,   1.0f, 0.0f,

                // Left face
                -0.5f,  0.5f,  0.5f,   1.0f, 0.0f,
                -0.5f, -0.5f,  0.5f,   1.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,   0.0f, 1.0f,
                -0.5f,  0.5f, -0.5f,   0.0f, 0.0f
        };

        int[] indices = new int[36];
        int i = 0;
        for (int face = 0; face < 6; face++) {
            int offset = face * 4;
            // First triangle of face
            indices[i++] = offset;
            indices[i++] = offset + 1;
            indices[i++] = offset + 2;
            // Second triangle of face
            indices[i++] = offset;
            indices[i++] = offset + 2;
            indices[i++] = offset + 3;
        }

        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        // Position VBO
        vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        // Position attribute
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 5 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        // Texture coordinate attribute
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        // Index buffer
        eboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboID);
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);

        vertexCount = indices.length;
        GL30.glBindVertexArray(0);
    }

    public void prepare() {
        GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }
    public void render() {
        GL30.glBindVertexArray(vaoID);
        GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }

    public void cleanUp() {
        GL15.glDeleteBuffers(vboID);
        GL15.glDeleteBuffers(eboID);
        GL30.glDeleteVertexArrays(vaoID);
        texture.cleanup();
        shader.stop();
    }
}