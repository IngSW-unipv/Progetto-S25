package model;

import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a block in a 3D world, including its type, position, visibility, and rendering details.
 */
public class Block {
    private final BlockType type;           // The type of the block (e.g., material, texture).
    private final Vector3f position;        // The position of the block in the world.
    private final BoundingBox boundingBox;  // The bounding box used for collision detection.
    private boolean[] visibleFaces;         // Tracks which faces of the block are visible.
    private boolean isVisible = true;       // Indicates if the block is visible for rendering.
    private boolean isHighlighted = false;  // Indicates if the block is highlighted (e.g., selected).
    private float breakProgress = 0.0f;     // Tracks the progress of breaking the block.

    // Indices for each face of the block
    private static final int FRONT = 0;  // Z+
    private static final int BACK = 1;   // Z-
    private static final int TOP = 2;    // Y+
    private static final int BOTTOM = 3; // Y-
    private static final int RIGHT = 4;  // X+
    private static final int LEFT = 5;   // X-

    /**
     * Constructs a block with a specific type and position.
     * Initializes the bounding box and sets all faces as visible by default.
     *
     * @param type     The type of the block.
     * @param position The position of the block in the world.
     */
    public Block(BlockType type, Vector3f position) {
        this.type = type;
        this.position = position;
        this.visibleFaces = new boolean[]{true, true, true, true, true, true};
        this.boundingBox = new BoundingBox(1.0f, 1.0f, 1.0f);
        this.boundingBox.update(new Vector3f(position.x(), position.y(), position.z()));
    }

    /**
     * Retrieves the current break progress of the block.
     *
     * @return The current break progress as a float.
     */
    public float getBreakProgress() {
        return breakProgress;
    }

    /**
     * Sets the break progress of the block.
     *
     * @param progress The new break progress value.
     */
    public void setBreakProgress(float progress) {
        this.breakProgress = progress;
    }

    /**
     * Updates the visibility of each face of the block based on its surroundings.
     * If all faces are hidden, the block is marked as invisible.
     *
     * @param world The world object containing all blocks.
     */
    public void updateVisibleFaces(World world) {
        if (isCompletelyHidden(world)) {
            Arrays.fill(visibleFaces, false);
            isVisible = false;
            return;
        }

        visibleFaces[TOP] = shouldRenderFace(world, 0, 1, 0);
        visibleFaces[BOTTOM] = shouldRenderFace(world, 0, -1, 0);
        visibleFaces[FRONT] = shouldRenderFace(world, 0, 0, 1);
        visibleFaces[BACK] = shouldRenderFace(world, 0, 0, -1);
        visibleFaces[RIGHT] = shouldRenderFace(world, 1, 0, 0);
        visibleFaces[LEFT] = shouldRenderFace(world, -1, 0, 0);

        isVisible = false;
        for (boolean face : visibleFaces) {
            if (face) {
                isVisible = true;
                break;
            }
        }
    }

    /**
     * Checks if a specific face of the block should be rendered.
     *
     * @param world The world object containing all blocks.
     * @param dx    The x-offset to check for adjacent blocks.
     * @param dy    The y-offset to check for adjacent blocks.
     * @param dz    The z-offset to check for adjacent blocks.
     * @return True if the face should be rendered; false otherwise.
     */
    private boolean shouldRenderFace(World world, int dx, int dy, int dz) {
        Vector3f adjacentPos = new Vector3f(
                position.x() + dx,
                position.y() + dy,
                position.z() + dz
        );

        Block adjacentBlock = world.getBlock(adjacentPos);
        return adjacentBlock == null;
    }

    /**
     * Determines if the block is completely hidden by adjacent blocks.
     *
     * @param world The world object containing all blocks.
     * @return True if all faces are hidden; false otherwise.
     */
    private boolean isCompletelyHidden(World world) {
        return hasAdjacentBlock(world, 0, 1, 0) &&
                hasAdjacentBlock(world, 0, -1, 0) &&
                hasAdjacentBlock(world, 0, 0, 1) &&
                hasAdjacentBlock(world, 0, 0, -1) &&
                hasAdjacentBlock(world, 1, 0, 0) &&
                hasAdjacentBlock(world, -1, 0, 0);
    }

    /**
     * Checks if there is an adjacent block at the specified offset.
     *
     * @param world The world object containing all blocks.
     * @param dx    The x-offset to check for adjacent blocks.
     * @param dy    The y-offset to check for adjacent blocks.
     * @param dz    The z-offset to check for adjacent blocks.
     * @return True if there is an adjacent block; false otherwise.
     */
    private boolean hasAdjacentBlock(World world, int dx, int dy, int dz) {
        Vector3f adjacentPos = new Vector3f(
                position.x() + dx,
                position.y() + dy,
                position.z() + dz
        );
        Block block = world.getBlock(adjacentPos);
        return block != null;
    }

    /**
     * Retrieves the vertices of a 3D block, based on the visible faces.
     * The vertices are calculated relative to the block's position and the visibility of each face.
     * Each face is defined by four vertices, with texture coordinates (u, v) assigned to each vertex.
     *
     * @return an array of float values representing the vertices of the visible faces.
     */
    public float[] getVertices() {
        List<Float> visibleVertices = new ArrayList<>();
        float x = position.x();
        float y = position.y();
        float z = position.z();

        // Front face (Z+)
        if (visibleFaces[FRONT]) {
            addFaceVertices(visibleVertices,
                    x - 0.5f, y + 0.5f, z + 0.5f, 0.0f, 0.0f,
                    x + 0.5f, y + 0.5f, z + 0.5f, 1.0f, 0.0f,
                    x + 0.5f, y - 0.5f, z + 0.5f, 1.0f, 1.0f,
                    x - 0.5f, y - 0.5f, z + 0.5f, 0.0f, 1.0f
            );
        }

        // Back face (Z-)
        if (visibleFaces[BACK]) {
            addFaceVertices(visibleVertices,
                    x + 0.5f, y + 0.5f, z - 0.5f, 0.0f, 0.0f,
                    x - 0.5f, y + 0.5f, z - 0.5f, 1.0f, 0.0f,
                    x - 0.5f, y - 0.5f, z - 0.5f, 1.0f, 1.0f,
                    x + 0.5f, y - 0.5f, z - 0.5f, 0.0f, 1.0f
            );
        }

        // Top face (Y+)
        if (visibleFaces[TOP]) {
            addFaceVertices(visibleVertices,
                    x - 0.5f, y + 0.5f, z - 0.5f, 0.0f, 0.0f,
                    x + 0.5f, y + 0.5f, z - 0.5f, 1.0f, 0.0f,
                    x + 0.5f, y + 0.5f, z + 0.5f, 1.0f, 1.0f,
                    x - 0.5f, y + 0.5f, z + 0.5f, 0.0f, 1.0f
            );
        }

        // Bottom face (Y-)
        if (visibleFaces[BOTTOM]) {
            addFaceVertices(visibleVertices,
                    x - 0.5f, y - 0.5f, z + 0.5f, 0.0f, 0.0f,
                    x + 0.5f, y - 0.5f, z + 0.5f, 1.0f, 0.0f,
                    x + 0.5f, y - 0.5f, z - 0.5f, 1.0f, 1.0f,
                    x - 0.5f, y - 0.5f, z - 0.5f, 0.0f, 1.0f
            );
        }

        // Right face (X+)
        if (visibleFaces[RIGHT]) {
            addFaceVertices(visibleVertices,
                    x + 0.5f, y + 0.5f, z + 0.5f, 0.0f, 0.0f,
                    x + 0.5f, y + 0.5f, z - 0.5f, 1.0f, 0.0f,
                    x + 0.5f, y - 0.5f, z - 0.5f, 1.0f, 1.0f,
                    x + 0.5f, y - 0.5f, z + 0.5f, 0.0f, 1.0f
            );
        }

        // Left face (X-)
        if (visibleFaces[LEFT]) {
            addFaceVertices(visibleVertices,
                    x - 0.5f, y + 0.5f, z - 0.5f, 0.0f, 0.0f,
                    x - 0.5f, y + 0.5f, z + 0.5f, 1.0f, 0.0f,
                    x - 0.5f, y - 0.5f, z + 0.5f, 1.0f, 1.0f,
                    x - 0.5f, y - 0.5f, z - 0.5f, 0.0f, 1.0f
            );
        }

        float[] result = new float[visibleVertices.size()];
        for (int i = 0; i < visibleVertices.size(); i++) {
            result[i] = visibleVertices.get(i);
        }
        return result;
    }

    public float[] getOutlineVertices() {
        float x = position.x();
        float y = position.y();
        float z = position.z();

        // 8 vertici del cubo
        return new float[] {
                // Bordo inferiore
                x - 0.5f, y - 0.5f, z - 0.5f,
                x + 0.5f, y - 0.5f, z - 0.5f,
                x + 0.5f, y - 0.5f, z + 0.5f,
                x - 0.5f, y - 0.5f, z + 0.5f,
                // Bordo superiore
                x - 0.5f, y + 0.5f, z - 0.5f,
                x + 0.5f, y + 0.5f, z - 0.5f,
                x + 0.5f, y + 0.5f, z + 0.5f,
                x - 0.5f, y + 0.5f, z + 0.5f
        };
    }

    public int[] getOutlineIndices() {
        return new int[] {
                0, 1, 1, 2, 2, 3, 3, 0, // bordo inferiore
                4, 5, 5, 6, 6, 7, 7, 4, // bordo superiore
                0, 4, 1, 5, 2, 6, 3, 7  // linee verticali
        };
    }

    /**
     * Adds the vertices of a face to the provided list of vertices.
     * Each face is defined by four vertices, with their respective 3D coordinates (x, y, z)
     * and texture coordinates (u, v).
     *
     * @param vertices The list to which the face vertices will be added.
     * @param x1, y1, z1, u1, v1 The coordinates and texture coordinates for the first vertex.
     * @param x2, y2, z2, u2, v2 The coordinates and texture coordinates for the second vertex.
     * @param x3, y3, z3, u3, v3 The coordinates and texture coordinates for the third vertex.
     * @param x4, y4, z4, u4, v4 The coordinates and texture coordinates for the fourth vertex.
     */
    private void addFaceVertices(List<Float> vertices,
         float x1, float y1, float z1, float u1, float v1,
         float x2, float y2, float z2, float u2, float v2,
         float x3, float y3, float z3, float u3, float v3,
         float x4, float y4, float z4, float u4, float v4) {
        vertices.add(x1); vertices.add(y1); vertices.add(z1); vertices.add(u1); vertices.add(v1);
        vertices.add(x2); vertices.add(y2); vertices.add(z2); vertices.add(u2); vertices.add(v2);
        vertices.add(x3); vertices.add(y3); vertices.add(z3); vertices.add(u3); vertices.add(v3);
        vertices.add(x4); vertices.add(y4); vertices.add(z4); vertices.add(u4); vertices.add(v4);
    }

    /**
     * Retrieves the indices of the visible faces, used for rendering the block.
     * Each visible face is split into two triangles, with the indices corresponding to the vertices.
     * The indices are used to create the geometry for rendering the block.
     *
     * @return an array of integers representing the indices of the visible faces.
     */
    public int[] getIndices() {
        // Count the number of visible faces
        int visibleFaceCount = 0;
        for (boolean visible : visibleFaces) {
            if (visible) visibleFaceCount++;
        }

        int[] indices = new int[visibleFaceCount * 6];
        int indexCount = 0;
        int vertexOffset = 0;

        for (int face = 0; face < 6; face++) {
            if (visibleFaces[face]) {
                // First triangle
                indices[indexCount++] = vertexOffset;
                indices[indexCount++] = vertexOffset + 1;
                indices[indexCount++] = vertexOffset + 2;
                // Second triangle
                indices[indexCount++] = vertexOffset;
                indices[indexCount++] = vertexOffset + 2;
                indices[indexCount++] = vertexOffset + 3;
                vertexOffset += 4;
            }
        }

        return indices;
    }

    /**
     * Sets the highlighted state of the block.
     * This state can be used to visually indicate whether the block is highlighted.
     *
     * @param highlighted A boolean value indicating whether the block should be highlighted.
     */
    public void setHighlighted(boolean highlighted) {
        this.isHighlighted = highlighted;
    }

    /**
     * Retrieves the highlighted state of the block.
     *
     * @return true if the block is highlighted, false otherwise.
     */
    public boolean isHighlighted() {
        return isHighlighted;
    }

    /**
     * Retrieves the type of the block.
     *
     * @return the type of the block as a {@link BlockType} enum.
     */
    public BlockType getType() {
        return type;
    }

    /**
     * Retrieves the bounding box of the block.
     * The bounding box defines the spatial limits of the block in 3D space.
     *
     * @return the bounding box of the block.
     */
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    /**
     * Retrieves the position of the block in 3D space.
     *
     * @return the position of the block as a {@link Vector3f} object.
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Retrieves the visibility state of the block.
     *
     * @return true if the block is visible, false otherwise.
     */
    public boolean isVisible() {
        return isVisible;
    }
}