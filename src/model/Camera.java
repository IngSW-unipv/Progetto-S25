package model;

import config.GameConfig;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position;
    private float pitch;
    private float yaw;
    private float roll;

    public Camera() {
        position = new Vector3f(0, 10, 0);  // Posizione più vicina al terreno
        pitch = -20f;  // Guardiamo leggermente verso il basso
        yaw = 45f;     // Ruotati per vedere gli angoli
        roll = 0;
    }

    // Rest of the model.Camera class remains the same
    public void move(boolean forward, boolean back, boolean left, boolean right, boolean up, boolean down) {
        float dx = 0, dz = 0, dy = 0;

        if (forward) dz -= GameConfig.CAMERA_MOVEMENT_INCREMENT;
        if (back) dz += GameConfig.CAMERA_MOVEMENT_INCREMENT;
        if (left) dx -= GameConfig.CAMERA_MOVEMENT_INCREMENT;
        if (right) dx += GameConfig.CAMERA_MOVEMENT_INCREMENT;
        if (up) dy += GameConfig.CAMERA_MOVEMENT_INCREMENT;
        if (down) dy -= GameConfig.CAMERA_MOVEMENT_INCREMENT;

        if (dx != 0 && dz != 0) {
            dx *= 0.707f;
            dz *= 0.707f;
        }

        float angle = (float) Math.toRadians(yaw);
        position.x += (float)(dx * Math.cos(angle) - dz * Math.sin(angle)) * GameConfig.CAMERA_MOVE_SPEED;
        position.z += (float)(dx * Math.sin(angle) + dz * Math.cos(angle)) * GameConfig.CAMERA_MOVE_SPEED;
        position.y += dy * GameConfig.CAMERA_MOVE_SPEED;
    }

    public void rotate(float dx, float dy) {
        yaw += dx * GameConfig.CAMERA_MOUSE_SENSITIVITY;
        pitch += dy * GameConfig.CAMERA_MOUSE_SENSITIVITY;

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