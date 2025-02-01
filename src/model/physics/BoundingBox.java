package model.physics;

import org.joml.Vector3f;

/**
 * 3D bounding box for collision detection.
 * Stores box dimensions and min/max coordinates.
 */
public class BoundingBox {
    /** Box boundaries */
    private final Vector3f min;
    private final Vector3f max;

    /** Box dimensions */
    private final float width;
    private final float height;
    private final float depth;


    /** Creates box with given dimensions */
    public BoundingBox(float width, float height, float depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.min = new Vector3f();
        this.max = new Vector3f();
    }

    /** Updates box position around center point */
    public void update(Vector3f position) {
        min.set(
            position.x - width / 2,
            position.y - height / 2,
            position.z - depth / 2
        );

        max.set(
            position.x + width / 2,
            position.y + height / 2,
            position.z + depth / 2
        );
    }

    /** Checks intersection with other box */
    public boolean intersects(BoundingBox other) {
        return (min.x <= other.max.x && max.x >= other.min.x) &&
                (min.y <= other.max.y && max.y >= other.min.y) &&
                (min.z <= other.max.z && max.z >= other.min.z);
    }
}