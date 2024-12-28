package model;

import org.joml.Vector3f;

public class Light {
    private Vector3f color;
    private Vector3f direction;

    public Light(Vector3f color, Vector3f direction) {
        this.color = color;
        this.direction = direction;
    }

    public Vector3f getColor() {
        return color;
    }

    public Vector3f getDirection() {
        return direction;
    }
}
