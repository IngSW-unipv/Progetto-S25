package view;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class MasterRenderer {
    private int vaoID;
    private int vboID;
    private int eboID;
    private int vertexCount;

    public MasterRenderer() {
        createQuad();
    }

    private void createQuad() {
        float[] vertices = {
                -0.5f,  0.5f, 0f,  // V0
                -0.5f, -0.5f, 0f,  // V1
                0.5f, -0.5f, 0f,  // V2
                0.5f,  0.5f, 0f   // V3
        };

        int[] indices = {
                0, 1, 2,  // Triangolo 1
                0, 2, 3   // Triangolo 2
        };

        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        // VBO per i vertici
        vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        // Attributo posizione
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);

        // EBO per gli indici
        eboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboID);
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);

        vertexCount = indices.length;

        // Scollega il VAO
        GL30.glBindVertexArray(0);
    }

    public void prepare() {
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
    }
}