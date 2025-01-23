// src/view/renderer/BatchedMesh.java

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

/**
 * Represents a batched mesh of vertices and indices that can be rendered efficiently in OpenGL.
 * This class supports adding multiple blocks to a single mesh and rendering them in one draw call.
 */
public class BatchedMesh {
    private int vaoID;
    private int vertexVBO;
    private int indexVBO;
    private int vertexCount;
    private List<Float> vertices;
    private List<Integer> indices;
    private boolean isDirty;

    /**
     * Constructs a new BatchedMesh.
     * Initializes the vertex and index buffers and creates the VAO.
     */
    public BatchedMesh() {
        this.vertices = new ArrayList<>();
        this.indices = new ArrayList<>();
        this.isDirty = false;
        createVAO();
    }

    /**
     * Creates the Vertex Array Object (VAO) and associated buffers.
     */
    private void createVAO() {
        vaoID = GL30.glGenVertexArrays();
        vertexVBO = GL15.glGenBuffers();
        indexVBO = GL15.glGenBuffers();
    }

    /**
     * Adds a block mesh to the batched mesh. This method appends the vertices and indices
     * of the block mesh to the existing batched mesh.
     *
     * @param blockVertices The vertices of the block mesh.
     * @param blockIndices  The indices of the block mesh.
     * @param indexOffset   The offset to apply to the indices to avoid conflicts with other meshes.
     * @param lightLevel    The light level of the block (0 to 15).
     */
    public void addBlockMesh(float[] blockVertices, int[] blockIndices, int indexOffset, int lightLevel) {
        // Add light data to the vertices
        float[] extendedVertices = addLightDataToVertices(blockVertices, lightLevel);

        for (float vertex : extendedVertices) {
            vertices.add(vertex);
        }

        for (int index : blockIndices) {
            indices.add(index + indexOffset);
        }
        isDirty = true;
    }

    /**
     * Adds light data to the vertices of a block mesh.
     *
     * @param blockVertices The original vertices of the block mesh.
     * @param lightLevel    The light level to add (normalized to 0.0 - 1.0).
     * @return An array of vertices with the light data appended.
     */
    private float[] addLightDataToVertices(float[] blockVertices, int lightLevel) {
        float normalizedLight = lightLevel / 15.0f; // Normalize the light level
        List<Float> extendedVertices = new ArrayList<>();

        for (int i = 0; i < blockVertices.length; i += 5) {
            // Copy existing vertex attributes
            extendedVertices.add(blockVertices[i]);     // x
            extendedVertices.add(blockVertices[i + 1]); // y
            extendedVertices.add(blockVertices[i + 2]); // z
            extendedVertices.add(blockVertices[i + 3]); // u
            extendedVertices.add(blockVertices[i + 4]); // v
            // Add light level
            extendedVertices.add(normalizedLight);      // light
        }

        // Convert list to array
        float[] result = new float[extendedVertices.size()];
        for (int i = 0; i < extendedVertices.size(); i++) {
            result[i] = extendedVertices.get(i);
        }
        return result;
    }

    /**
     * Updates the OpenGL buffers with the current vertex and index data.
     * This method should be called when the mesh data has changed.
     */
    public void updateGLBuffers() {
        if (!isDirty) return;

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.size());
        for (float v : vertices) vertexBuffer.put(v);
        vertexBuffer.flip();

        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.size());
        for (int i : indices) indexBuffer.put(i);
        indexBuffer.flip();

        GL30.glBindVertexArray(vaoID);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexVBO);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        // Position (3 float)
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        // Texture coords (2 float)
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        // Light intensity (1 float)
        GL20.glVertexAttribPointer(2, 1, GL11.GL_FLOAT, false, 6 * Float.BYTES, 5 * Float.BYTES);
        GL20.glEnableVertexAttribArray(2);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexVBO);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);

        GL30.glBindVertexArray(0);

        vertexCount = indices.size();
        isDirty = false;
    }

    /**
     * Renders the batched mesh using the current OpenGL state.
     */
    public void render() {
        GL30.glBindVertexArray(vaoID);
        GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }

    /**
     * Cleans up the OpenGL resources used by the mesh, including buffers and VAO.
     * This should be called when the mesh is no longer needed.
     */
    public void cleanup() {
        GL15.glDeleteBuffers(vertexVBO);
        GL15.glDeleteBuffers(indexVBO);
        GL30.glDeleteVertexArrays(vaoID);
    }

    /**
     * Clears the mesh data, removing all vertices and indices.
     * This method marks the mesh as dirty, so it will need to be updated with new data.
     */
    public void clear() {
        vertices.clear();
        indices.clear();
        isDirty = true;
    }
}
