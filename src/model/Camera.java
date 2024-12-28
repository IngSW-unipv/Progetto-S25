package model;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position;
    private float pitch;    // X rotation (up/down)
    private float yaw;      // Y rotation (left/right)
    private float roll;     // Z rotation (tilt)

    private static final float MOVE_SPEED = 0.05f;
    private static final float MOUSE_SENSITIVITY = 0.15f;

    public Camera() {
        position = new Vector3f(0, 0, 2);
        pitch = 0;
        yaw = 0;
        roll = 0;
    }

    public void move(boolean forward, boolean back, boolean left, boolean right, boolean up, boolean down) {
        float dx = 0, dz = 0, dy = 0;

        if (forward) dz -= .1f;
        if (back) dz += .1f;
        if (left) dx -= .1f;
        if (right) dx += .1f;
        if (up) dy += .1f;
        if (down) dy -= .1f;

        // Normalize diagonal movement
        if (dx != 0 && dz != 0) {
            dx *= 0.707f;
            dz *= 0.707f;
        }

        // Calculate movement relative to camera direction
        float angle = (float) Math.toRadians(yaw);
        position.x += (float)(dx * Math.cos(angle) - dz * Math.sin(angle)) * MOVE_SPEED;
        position.z += (float)(dx * Math.sin(angle) + dz * Math.cos(angle)) * MOVE_SPEED;
        position.y += dy * MOVE_SPEED;
    }

    public void rotate(float dx, float dy) {
        yaw += dx * MOUSE_SENSITIVITY;
        pitch += dy * MOUSE_SENSITIVITY;

        if (pitch > 90) {
            pitch = 90;
        } else if (pitch < -90) {
            pitch = -90;
        }

        if (yaw >= 360) {
            yaw -= 360;
        } else if (yaw < 0) {
            yaw += 360;
        }
    }

    public Matrix4f getViewMatrix() {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();

        matrix.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0));
        matrix.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0));
        matrix.rotate((float) Math.toRadians(roll), new Vector3f(0, 0, 1));
        matrix.translate(new Vector3f(-position.x, -position.y, -position.z));

        return matrix;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
}