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
 * Manages batched mesh data for efficient rendering in OpenGL.
 * Combines multiple block meshes into a single VAO/VBO for optimized draw calls.
 */
public class BatchedMesh {
    /** OpenGL VAO identifier */
    private int vaoID;

    /** Vertex buffer object for mesh data */
    private int vertexVBO;

    /** Index buffer object for mesh indices */
    private int indexVBO;

    /** Total number of vertices in the mesh */
    private int vertexCount;


    /** Dynamic lists to store mesh data before upload */
    private final List<Float> vertices = new ArrayList<>();
    private final List<Integer> indices = new ArrayList<>();
    private boolean isDirty = false;

    /**
     * Creates a new batched mesh and initializes OpenGL buffers
     */
    public BatchedMesh() {
        createVAO();
    }

    /**
     * Creates VAO and associated buffers for mesh data
     */
    private void createVAO() {
        vaoID = GL30.glGenVertexArrays();
        vertexVBO = GL15.glGenBuffers();
        indexVBO = GL15.glGenBuffers();
    }

    /**
     * Adds a block's mesh data to this batched mesh
     *
     * @param blockVertices Raw vertex data for the block
     * @param blockIndices Index data for the block vertices
     * @param indexOffset Offset to apply to indices to avoid conflicts
     * @param lightLevel Block's lighting value (0-15)
     */
    public void addBlockMesh(float[] blockVertices, int[] blockIndices,
                             int indexOffset, int lightLevel) {
        // Add light data to vertices
        float[] lightedVertices = addLightDataToVertices(blockVertices, lightLevel);

        // Add vertices and indices to lists
        for (float vertex : lightedVertices) {
            vertices.add(vertex);
        }

        for (int index : blockIndices) {
            indices.add(index + indexOffset);
        }
        isDirty = true;
    }

    /**
     * Adds per-vertex lighting data to block vertices
     */
    private float[] addLightDataToVertices(float[] blockVertices, int lightLevel) {
        float normalizedLight = lightLevel / 15.0f;
        List<Float> extendedVertices = new ArrayList<>();

        // Copy vertices and add light value
        for (int i = 0; i < blockVertices.length; i += 5) {
            for (int j = 0; j < 5; j++) {
                extendedVertices.add(blockVertices[i + j]);
            }
            extendedVertices.add(normalizedLight);
        }

        // Convert to array
        float[] result = new float[extendedVertices.size()];
        for (int i = 0; i < extendedVertices.size(); i++) {
            result[i] = extendedVertices.get(i);
        }
        return result;
    }

    /**
     * Updates OpenGL buffers with current mesh data if changed
     */
    public void updateGLBuffers() {
        if (!isDirty) return;

        // Create buffers
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.size());
        vertices.forEach(vertexBuffer::put);
        vertexBuffer.flip();

        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.size());
        indices.forEach(indexBuffer::put);
        indexBuffer.flip();

        // Upload to GPU
        GL30.glBindVertexArray(vaoID);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexVBO);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        // Configure vertex attributes
        configureVertexAttributes();

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexVBO);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);

        GL30.glBindVertexArray(0);

        vertexCount = indices.size();
        isDirty = false;
    }

    /**
     * Sets up vertex attribute pointers for position, texcoords and lighting
     */
    private void configureVertexAttributes() {
        // Position (xyz)
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        // Texture coordinates (uv)
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        // Light level
        GL20.glVertexAttribPointer(2, 1, GL11.GL_FLOAT, false, 6 * Float.BYTES, 5 * Float.BYTES);
        GL20.glEnableVertexAttribArray(2);
    }

    /**
     * Renders the mesh using current OpenGL state
     */
    public void render() {
        GL30.glBindVertexArray(vaoID);
        GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }

    /**
     * Frees OpenGL resources used by this mesh
     */
    public void cleanup() {
        GL15.glDeleteBuffers(vertexVBO);
        GL15.glDeleteBuffers(indexVBO);
        GL30.glDeleteVertexArrays(vaoID);
    }

    /**
     * Clears mesh data and marks for update
     */
    public void clear() {
        vertices.clear();
        indices.clear();
        isDirty = true;
    }
}