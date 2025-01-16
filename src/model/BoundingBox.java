package model;

import org.joml.Vector3f;

/**
 * Represents a 3D bounding box, used for collision detection and spatial queries.
 */
public class BoundingBox {
    private final Vector3f min; // Minimum coordinates of the bounding box
    private final Vector3f max; // Maximum coordinates of the bounding box
    private final float width;  // Width of the bounding box
    private final float height; // Height of the bounding box
    private final float depth;  // Depth of the bounding box

    /**
     * Constructs a bounding box with the specified dimensions.
     *
     * @param width  The width of the bounding box.
     * @param height The height of the bounding box.
     * @param depth  The depth of the bounding box.
     */
    public BoundingBox(float width, float height, float depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.min = new Vector3f();
        this.max = new Vector3f();
    }

    /**
     * Updates the bounding box's position based on a given center position.
     *
     * @param position The center position of the bounding box.
     */
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

    /**
     * Checks if this bounding box intersects with another bounding box.
     *
     * @param other The other bounding box to check for intersection.
     * @return {@code true} if the bounding boxes intersect, otherwise {@code false}.
     */
    public boolean intersects(BoundingBox other) {
        return (min.x <= other.max.x && max.x >= other.min.x) &&
            (min.y <= other.max.y && max.y >= other.min.y) &&
            (min.z <= other.max.z && max.z >= other.min.z);
    }
}