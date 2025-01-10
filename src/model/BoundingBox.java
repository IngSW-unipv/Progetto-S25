package model;

import org.joml.Vector3f;

public class BoundingBox {
    private Vector3f min;
    private Vector3f max;
    private final float width;
    private final float height;
    private final float depth;

    public BoundingBox(float width, float height, float depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.min = new Vector3f();
        this.max = new Vector3f();
    }

    public void update(Vector3f position) {
        min.x = position.x - width/2;
        min.y = position.y - height/2;
        min.z = position.z - depth/2;

        max.x = position.x + width/2;
        max.y = position.y + height/2;
        max.z = position.z + depth/2;
    }

    public boolean intersects(BoundingBox other) {
        return (min.x <= other.max.x && max.x >= other.min.x) &&
            (min.y <= other.max.y && max.y >= other.min.y) &&
            (min.z <= other.max.z && max.z >= other.min.z);
    }
}