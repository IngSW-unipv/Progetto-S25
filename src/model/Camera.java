package model;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position;
    private float pitch;    // Rotazione X (guardare su/giù)
    private float yaw;      // Rotazione Y (guardare destra/sinistra)
    private float roll;     // Rotazione Z (inclinazione laterale)

    private static final float MOVE_SPEED = 0.05f;
    private static final float MOUSE_SENSITIVITY = 0.15f;

    public Camera() {
        position = new Vector3f(0, 0, 2);
        pitch = 0;
        yaw = 0;
        roll = 0;
    }

    public void move(boolean forward, boolean back, boolean left, boolean right
            //, boolean up, boolean down
                     ) {
        float dx = 0, dz = 0, dy = 0;

        if (forward) dz -= .1f;
        if (back) dz += .1f;
        if (left) dx -= .1f;
        if (right) dx += .1f;
        //if (up) dy += 1;
        //if (down) dy -= 1;

        // Normalizza il movimento diagonale
        if (dx != 0 && dz != 0) {
            dx *= 0.707f;
            dz *= 0.707f;
        }

        // Calcola il movimento relativo alla direzione della camera
        float angle = (float) Math.toRadians(yaw);
        position.x += (float)(dx * Math.cos(angle) - dz * Math.sin(angle)) * MOVE_SPEED;
        position.z += (float)(dx * Math.sin(angle) + dz * Math.cos(angle)) * MOVE_SPEED;
        position.y += dy * MOVE_SPEED;  // Aggiungi il movimento verticale
    }

    public void rotate(float dx, float dy) {
        yaw += dx * MOUSE_SENSITIVITY;
        pitch += dy * MOUSE_SENSITIVITY;  // Rimosso il segno negativo

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

        // Debug: stampa la posizione della camera e la matrice di vista
        //System.out.println("Camera Position: " + position);
        //System.out.println("Yaw: " + yaw + ", Pitch: " + pitch);

        // Applica le rotazioni
        matrix.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0));
        matrix.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0));
        matrix.rotate((float) Math.toRadians(roll), new Vector3f(0, 0, 1));

        // Applica la traslazione
        matrix.translate(new Vector3f(-position.x, -position.y, -position.z));

        return matrix;
    }

    // Getters
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