package model.block;

import model.physics.BoundingBox;
import model.world.World;
import org.joml.Vector3f;

import java.util.List;

public abstract class AbstractBlock {
    protected final Vector3f position;
    protected final BoundingBox boundingBox;
    protected final boolean[] visibleFaces;
    protected boolean isVisible;
    protected boolean isHighlighted;
    protected float breakProgress;
    protected final int lightLevel;

    protected static final int FRONT = 0;   // Z+
    protected static final int BACK = 1;    // Z-
    protected static final int TOP = 2;     // Y+
    protected static final int BOTTOM = 3;  // Y-
    protected static final int RIGHT = 4;   // X+
    protected static final int LEFT = 5;    // X-

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

    // Metodi astratti che devono essere implementati dalle sottoclassi
    /**
     * Gets the type identifier for this block
     */
    public abstract BlockType getType();
    public abstract boolean isUnbreakable();
    public abstract String getTexturePath();
    public abstract float getBreakTime();
    public abstract boolean isOpaque();
    public abstract float[] getVertices();
    public abstract int[] getIndices();
    public abstract void onBreak(World world);
    public abstract void onPlace(World world);
    protected abstract void onUpdate(World world);

    // Getters comuni
    public Vector3f getPosition() { return position; }
    public BoundingBox getBoundingBox() { return boundingBox; }
    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { isVisible = visible; }
    public boolean isHighlighted() { return isHighlighted; }
    public void setHighlighted(boolean highlighted) { isHighlighted = highlighted; }
    public float getBreakProgress() { return breakProgress; }
    public void setBreakProgress(float progress) { this.breakProgress = progress; }
    public int getLightLevel() { return lightLevel; }

    // Gestione visibilità facce
    public void updateVisibleFaces(World world) {
        if (isCompletelyHidden(world)) {
            for (int i = 0; i < visibleFaces.length; i++) {
                visibleFaces[i] = false;
            }
            isVisible = false;
            return;
        }

        updateFaceVisibility(world);
        updateOverallVisibility();
    }

    protected void updateFaceVisibility(World world) {
        visibleFaces[TOP] = shouldRenderFace(world, 0, 1, 0);
        visibleFaces[BOTTOM] = shouldRenderFace(world, 0, -1, 0);
        visibleFaces[FRONT] = shouldRenderFace(world, 0, 0, 1);
        visibleFaces[BACK] = shouldRenderFace(world, 0, 0, -1);
        visibleFaces[RIGHT] = shouldRenderFace(world, 1, 0, 0);
        visibleFaces[LEFT] = shouldRenderFace(world, -1, 0, 0);
    }

    private void updateOverallVisibility() {
        isVisible = false;
        for (boolean face : visibleFaces) {
            if (face) {
                isVisible = true;
                break;
            }
        }
    }

    protected boolean shouldRenderFace(World world, int dx, int dy, int dz) {
        Vector3f adjacentPos = new Vector3f(
                position.x() + dx,
                position.y() + dy,
                position.z() + dz
        );
        AbstractBlock adjacent = world.getBlock(adjacentPos);
        return adjacent == null || !adjacent.isOpaque();
    }

    private boolean isCompletelyHidden(World world) {
        return !shouldRenderFace(world, 0, 1, 0) &&
                !shouldRenderFace(world, 0, -1, 0) &&
                !shouldRenderFace(world, 0, 0, 1) &&
                !shouldRenderFace(world, 0, 0, -1) &&
                !shouldRenderFace(world, 1, 0, 0) &&
                !shouldRenderFace(world, -1, 0, 0);
    }

    // Utility per le sottoclassi
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