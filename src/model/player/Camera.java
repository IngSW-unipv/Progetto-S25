package model.player;

import config.GameConfig;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Represents the camera view in the 3D world.
 * Handles camera rotation and view transformations.
 */
public class Camera {
    /** Camera position in world space */
    private Vector3f position;

    /** Vertical rotation angle in degrees, clamped between -89° and 89° */
    private float pitch = 0;

    /** Horizontal rotation angle in degrees, wraps between 0° and 359° */
    private float yaw = 0;

    /** Z-axis rotation in degrees (currently unused) */
    private float roll = 0;

    /** Movement constants from config */
    private static final float EYE_HEIGHT = .8f;
    private static final float CAMERA_MOUSE_SENSITIVITY = GameConfig.CAMERA_MOUSE_SENSITIVITY;

    /**
     * Creates a camera at the specified position.
     *
     * @param initialPosition Starting position in world space
     */
    public Camera(Vector3f initialPosition) {
        this.position = initialPosition;
    }

    /**
     * Updates camera rotation based on mouse movement.
     * Applies sensitivity and ensures pitch stays within valid range.
     *
     * @param dx Horizontal mouse movement delta
     * @param dy Vertical mouse movement delta
     */
    public void rotate(float dx, float dy) {
        yaw = (yaw + dx * CAMERA_MOUSE_SENSITIVITY) % 360;
        pitch = Math.max(-89.0f, Math.min(89.0f, pitch + dy * CAMERA_MOUSE_SENSITIVITY));
    }

    /**
     * Creates the view transformation matrix for rendering.
     * Applies rotations and position offset for proper camera view.
     *
     * @return View transformation matrix
     */
    public Matrix4f getViewMatrix() {
        Vector3f cameraPos = new Vector3f(position).add(0, EYE_HEIGHT, 0);
        return new Matrix4f()
            .identity()
            .rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0))
            .rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0))
            .translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
    }

    /**
     * Gets camera position with eye height offset.
     *
     * @return World position adjusted for eye level
     */
    public Vector3f getPosition() {
        return new Vector3f(position).add(0, EYE_HEIGHT, 0);
    }

    /**
     * Updates the camera's position in world space.
     *
     * @param newPosition New world position to set
     */
    public void setPosition(Vector3f newPosition) {
        this.position = newPosition;
    }

    /**
     * Gets raw camera position without eye height offset.
     *
     * @return Base world position
     */
    public Vector3f getRawPosition() {
        return position;
    }

    /**
     * Gets current vertical rotation angle.
     *
     * @return Pitch in degrees
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Sets vertical rotation angle, clamped to valid range.
     *
     * @param pitch New pitch angle in degrees
     */
    public void setPitch(float pitch) {
        this.pitch = Math.max(-89.0f, Math.min(89.0f, pitch));
    }

    /**
     * Gets current horizontal rotation angle.
     *
     * @return Yaw in degrees
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Sets horizontal rotation angle, wrapped to 0-359 range.
     *
     * @param yaw New yaw angle in degrees
     */
    public void setYaw(float yaw) {
        this.yaw = yaw % 360;
    }

    /**
     * Gets current roll rotation angle.
     *
     * @return Roll in degrees
     */
    public float getRoll() {
        return roll;
    }
}