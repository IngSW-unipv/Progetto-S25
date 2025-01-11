package view.renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class BatchedMesh {
    private int vaoID;
    private int vertexVBO;
    private int indexVBO;
    private int vertexCount;
    private List<Float> vertices;
    private List<Integer> indices;
    private boolean isDirty;

    public BatchedMesh() {
        this.vertices = new ArrayList<>();
        this.indices = new ArrayList<>();
        this.isDirty = false;
        createVAO();
    }

    private void createVAO() {
        vaoID = GL30.glGenVertexArrays();
        vertexVBO = GL15.glGenBuffers();
        indexVBO = GL15.glGenBuffers();
    }

    public void addBlockMesh(float[] blockVertices, int[] blockIndices, int indexOffset) {
        for (float vertex : blockVertices) {
            vertices.add(vertex);
        }

        for (int index : blockIndices) {
            indices.add(index + indexOffset);
        }
        isDirty = true;
    }

    public void updateGLBuffers() {
        if (!isDirty) return;

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.size());
        for (float v : vertices) {
            vertexBuffer.put(v);
        }
        vertexBuffer.flip();

        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.size());
        for (int i : indices) {
            indexBuffer.put(i);
        }
        indexBuffer.flip();

        GL30.glBindVertexArray(vaoID);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexVBO);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 5 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexVBO);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);

        GL30.glBindVertexArray(0);

        vertexCount = indices.size();
        isDirty = false;
    }

    public void render() {
        GL30.glBindVertexArray(vaoID);
        GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        GL15.glDeleteBuffers(vertexVBO);
        GL15.glDeleteBuffers(indexVBO);
        GL30.glDeleteVertexArrays(vaoID);
    }

    public void clear() {
        vertices.clear();
        indices.clear();
        isDirty = true;
    }
}