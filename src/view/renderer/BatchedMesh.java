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
     * @param blockIndices The indices of the block mesh.
     * @param indexOffset The offset to apply to the indices to avoid conflicts with other meshes.
     */
    public void addBlockMesh(float[] blockVertices, int[] blockIndices, int indexOffset) {
        for (float vertex : blockVertices) {
            vertices.add(vertex);
        }

        for (int index : blockIndices) {
            indices.add(index + indexOffset);
        }
        isDirty = true;
    }

    /**
     * Updates the OpenGL buffers with the current vertex and index data.
     * This method should be called when the mesh data has changed.
     */
    public void updateGLBuffers() {
        if (!isDirty) return;

        // Create buffers for vertices and indices
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

        // Bind the VAO and update the buffers
        GL30.glBindVertexArray(vaoID);

        // Update the vertex buffer
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexVBO);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        // Define vertex attribute pointers
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 5 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        // Update the index buffer
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexVBO);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);

        // Unbind the VAO
        GL30.glBindVertexArray(0);

        // Update the vertex count and mark the mesh as not dirty
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