package model.block;

import model.physics.BoundingBox;
import model.world.World;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.List;

/**
 * Base class for all block types in the voxel world.
 * Handles block state, visibility, and face culling.
 *
 * @see TerrainBlock
 * @see BlockFactory
 * @see World
 */
public abstract class AbstractBlock {
    /** Block position in world space */
    protected final Vector3f position;

    /** Collision boundaries */
    protected final BoundingBox boundingBox;

    /** Per-face visibility flags */
    protected final boolean[] visibleFaces;

    /** Block visibility state */
    protected boolean isVisible;

    /** Selection highlight state */
    protected boolean isHighlighted;

    /** Breaking animation progress */
    protected float breakProgress;

    /** Block illumination level */
    protected final int lightLevel;

    /** Face direction indices */
    protected static final int FRONT = 0;   // Z+
    protected static final int BACK = 1;    // Z-
    protected static final int TOP = 2;     // Y+
    protected static final int BOTTOM = 3;  // Y-
    protected static final int RIGHT = 4;   // X+
    protected static final int LEFT = 5;    // X-

    /**
     * Creates block at specified position.
     *
     * @param position Block world coordinates
     */
    protected AbstractBlock(Vector3f position) {
        this.position = position;
        this.boundingBox = new BoundingBox(1.0f, 1.0f, 1.0f);
        this.visibleFaces = new boolean[]{true, true, true, true, true, true};
        this.isVisible = true;
        this.isHighlighted = false;
        this.breakProgress = 0.0f;
        this.lightLevel = 15;
        this.boundingBox.update(position);
    }

    /** Gets block type identifier */
    public abstract BlockType getType();

    /** Whether block cannot be broken */
    public abstract boolean isUnbreakable();

    /** Gets block texture file path */
    public abstract String getTexturePath();

    /** Gets time required to break block */
    public abstract float getBreakTime();

    /** Whether block is visually solid */
    public abstract boolean isOpaque();

    /** Gets block geometry vertex data */
    public abstract float[] getVertices();

    /** Gets block geometry index data */
    public abstract int[] getIndices();

    /** Called when block is broken */
    public abstract void onBreak(World world);

    /** Called when block is placed */
    public abstract void onPlace(World world);

    /** Called to update block state */
    protected abstract void onUpdate(World world);

    /** Gets block position */
    public Vector3f getPosition() { return position; }

    /** Gets collision bounds */
    public BoundingBox getBoundingBox() { return boundingBox; }

    /** Gets visibility state */
    public boolean isVisible() { return isVisible; }

    /** Sets visibility state */
    public void setVisible(boolean visible) { isVisible = visible; }

    /** Gets highlight state */
    public boolean isHighlighted() { return isHighlighted; }

    /** Sets highlight state */
    public void setHighlighted(boolean highlighted) { isHighlighted = highlighted; }

    /** Gets break progress */
    public float getBreakProgress() { return breakProgress; }

    /** Sets break progress */
    public void setBreakProgress(float progress) { this.breakProgress = progress; }

    /** Gets light level */
    public int getLightLevel() { return lightLevel; }

    /**
     * Updates which faces should be rendered based on neighbors.
     *
     * @param world World containing this block
     */
    public void updateVisibleFaces(World world) {
        if (isCompletelyHidden(world)) {
            Arrays.fill(visibleFaces, false);
            isVisible = false;
            return;
        }

        updateFaceVisibility(world);
        updateOverallVisibility();
    }

    /**
     * Updates face visibility based on adjacent blocks.
     */
    protected void updateFaceVisibility(World world) {
        visibleFaces[TOP] = shouldRenderFace(world, 0, 1, 0);
        visibleFaces[BOTTOM] = shouldRenderFace(world, 0, -1, 0);
        visibleFaces[FRONT] = shouldRenderFace(world, 0, 0, 1);
        visibleFaces[BACK] = shouldRenderFace(world, 0, 0, -1);
        visibleFaces[RIGHT] = shouldRenderFace(world, 1, 0, 0);
        visibleFaces[LEFT] = shouldRenderFace(world, -1, 0, 0);
    }

    /**
     * Updates overall visibility based on face states.
     */
    private void updateOverallVisibility() {
        isVisible = false;
        for (boolean face : visibleFaces) {
            if (face) {
                isVisible = true;
                break;
            }
        }
    }

    /**
     * Checks if face at offset should be rendered.
     */
    protected boolean shouldRenderFace(World world, int dx, int dy, int dz) {
        Vector3f adjacentPos = new Vector3f(
                position.x() + dx,
                position.y() + dy,
                position.z() + dz
        );
        AbstractBlock adjacent = world.getBlock(adjacentPos);
        return adjacent == null || !adjacent.isOpaque();
    }

    /**
     * Checks if block is fully surrounded by opaque blocks.
     */
    private boolean isCompletelyHidden(World world) {
        return !shouldRenderFace(world, 0, 1, 0) &&
                !shouldRenderFace(world, 0, -1, 0) &&
                !shouldRenderFace(world, 0, 0, 1) &&
                !shouldRenderFace(world, 0, 0, -1) &&
                !shouldRenderFace(world, 1, 0, 0) &&
                !shouldRenderFace(world, -1, 0, 0);
    }

    /**
     * Adds vertices for a block face to vertex list.
     * @param vertices List to add vertices to
     * @param x1,y1,z1,u1,v1 First vertex position and texture coordinates
     * @param x2,y2,z2,u2,v2 Second vertex position and texture coordinates
     * @param x3,y3,z3,u3,v3 Third vertex position and texture coordinates
     * @param x4,y4,z4,u4,v4 Fourth vertex position and texture coordinates
     */
    protected void addFaceVertices(List<Float> vertices,
                                   float x1, float y1, float z1, float u1, float v1,
                                   float x2, float y2, float z2, float u2, float v2,
                                   float x3, float y3, float z3, float u3, float v3,
                                   float x4, float y4, float z4, float u4, float v4) {
        vertices.add(x1); vertices.add(y1); vertices.add(z1); vertices.add(u1); vertices.add(v1);
        vertices.add(x2); vertices.add(y2); vertices.add(z2); vertices.add(u2); vertices.add(v2);
        vertices.add(x3); vertices.add(y3); vertices.add(z3); vertices.add(u3); vertices.add(v3);
        vertices.add(x4); vertices.add(y4); vertices.add(z4); vertices.add(u4); vertices.add(v4);
    }
}