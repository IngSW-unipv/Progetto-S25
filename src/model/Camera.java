package model;

import config.GameConfig;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Represents the player's view and movement in the 3D world.
 * Handles camera movement, collision detection, and view transformation.
 */
public class Camera {
    /** Camera position vector */
    private final Vector3f position;

    /** Vertical rotation in degrees (-89 to 89) */
    private float pitch;

    /** Horizontal rotation in degrees (0 to 359) */
    private float yaw;

    /** Z-axis rotation in degrees (unused) */
    private float roll;

    /** Collision bounds for player model */
    private final BoundingBox boundingBox;

    /** Collision detection system */
    private final CollisionSystem collisionSystem;

    /** Player model height (world units) */
    private static final float PLAYER_HEIGHT = 1.8f;

    /** Player model width (world units) */
    private static final float PLAYER_WIDTH = 0.6f;

    /** Current vertical movement speed */
    private float verticalVelocity = 0.0f;

    /** True if player is on solid ground */
    private boolean isGrounded = false;

    /** Camera height offset from player position */
    private static final float EYE_HEIGHT = 1.6f;

    /**
     * Creates a camera at specified position with collision detection.
     *
     * @param collisionSystem The collision detection system
     * @param initialPosition Starting world position
     * @throws IllegalArgumentException if collisionSystem is null
     */
    public Camera(CollisionSystem collisionSystem, Vector3f initialPosition) {
        if (collisionSystem == null) {
            throw new IllegalArgumentException("CollisionSystem cannot be null");
        }
        this.collisionSystem = collisionSystem;
        position = initialPosition;
        boundingBox = new BoundingBox(PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_WIDTH);
        boundingBox.update(position);
        pitch = 0;
        yaw = 0;
        roll = 0;
    }

    /**
     * Tests if movement to new position is possible without collision.
     *
     * @param newPosition Position to test
     * @param direction HORIZONTAL or VERTICAL movement
     * @return true if movement is possible, false if blocked by collision
     */
    private boolean canMoveToPosition(Vector3f newPosition, Direction direction) {
        // Create a bounding box to test potential movement
        BoundingBox potentialBox = new BoundingBox(PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_WIDTH);
        Vector3f testPosition = new Vector3f(position);

        // Update test position based on movement direction
        switch (direction) {
            case HORIZONTAL:
                testPosition.x = newPosition.x; // Test X movement
                testPosition.z = newPosition.z; // Test Z movement
                break;
            case VERTICAL:
                testPosition.y = newPosition.y; // Test Y movement
                break;
        }

        // Update and check test box for collisions
        potentialBox.update(testPosition);
        return !collisionSystem.checkCollision(potentialBox);
    }

    /**
     * Updates camera position based on input and physics.
     * Handles movement, jumping, gravity and collisions.
     *
     * @param forward Forward movement
     * @param back Backward movement
     * @param left Strafe left
     * @param right Strafe right
     * @param up Jump
     * @param down Unused
     * @param deltaTime Seconds since last update
     */
    public void move(boolean forward, boolean back, boolean left, boolean right, boolean up, boolean down, float deltaTime) {
        // Calculate movement deltas based on input
        float dx = 0, dz = 0;
        if (forward) dz -= GameConfig.CAMERA_MOVEMENT_INCREMENT;
        if (back) dz += GameConfig.CAMERA_MOVEMENT_INCREMENT;
        if (left) dx -= GameConfig.CAMERA_MOVEMENT_INCREMENT;
        if (right) dx += GameConfig.CAMERA_MOVEMENT_INCREMENT;

        // Normalize diagonal movement
        if (dx != 0 && dz != 0) {
            dx *= 0.707f; // 1/√2 for diagonal normalization
            dz *= 0.707f;
        }

        // Apply gravity and terminal velocity
        verticalVelocity += GameConfig.GRAVITY * deltaTime;
        verticalVelocity = Math.max(verticalVelocity, GameConfig.TERMINAL_VELOCITY);

        // Handle jumping when grounded
        if (up && isGrounded) {
            verticalVelocity = GameConfig.JUMP_FORCE;
            isGrounded = false;
        }

        // Calculate movement vector based on camera rotation
        float angle = (float) Math.toRadians(yaw);
        Vector3f newPosition = new Vector3f(position);
        Vector3f horizontalMove = new Vector3f(
            (float)(dx * Math.cos(angle) - dz * Math.sin(angle)) * GameConfig.CAMERA_MOVE_SPEED * deltaTime,
            0,
            (float)(dx * Math.sin(angle) + dz * Math.cos(angle)) * GameConfig.CAMERA_MOVE_SPEED * deltaTime
    );

        // Try full horizontal movement
        newPosition.x = position.x + horizontalMove.x;
        newPosition.z = position.z + horizontalMove.z;

        if (canMoveToPosition(newPosition, Direction.HORIZONTAL)) {
            // Apply full movement if no collision
            position.x = newPosition.x;
            position.z = newPosition.z;
        } else {
            // Try X movement only on collision
            newPosition.x = position.x + horizontalMove.x;
            newPosition.z = position.z;
            if (canMoveToPosition(newPosition, Direction.HORIZONTAL)) {
                position.x = newPosition.x;
            }

            // Try Z movement only
            newPosition.x = position.x;
            newPosition.z = position.z + horizontalMove.z;
            if (canMoveToPosition(newPosition, Direction.HORIZONTAL)) {
                position.z = newPosition.z;
            }
        }

        // Handle vertical movement and collisions
        newPosition.y = position.y + verticalVelocity * deltaTime;
        if (canMoveToPosition(newPosition, Direction.VERTICAL)) {
            position.y = newPosition.y;
            isGrounded = false;
        } else {
            if (verticalVelocity < 0) {
                // Landing
                isGrounded = true;
                verticalVelocity = 0;
            } else if (verticalVelocity > 0) {
                // Hit ceiling
                verticalVelocity = 0;
            }
        }

        // Update collision box position
        boundingBox.update(position);
    }

    /**
     * Updates camera rotation based on mouse movement.
     *
     * @param dx Horizontal mouse delta
     * @param dy Vertical mouse delta
     */
    public void rotate(float dx, float dy) {
        yaw = (yaw + dx * GameConfig.CAMERA_MOUSE_SENSITIVITY) % 360;
        pitch = Math.max(-89.0f, Math.min(89.0f, pitch + dy * GameConfig.CAMERA_MOUSE_SENSITIVITY));
    }

    /**
     * Creates view transformation matrix for rendering.
     *
     * @return The camera's view matrix
     */
    public Matrix4f getViewMatrix() {
        // Add eye height offset to camera position
        Vector3f cameraPos = new Vector3f(position).add(0, EYE_HEIGHT, 0);

        // Create and return view matrix with rotations and position
        return new Matrix4f()
            .identity()
            .rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0))
            .rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0))
            .translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
    }

    /**
     * @return Position with eye height offset
     */
    public Vector3f getPosition() {
        return new Vector3f(position).add(0, EYE_HEIGHT, 0);
    }

    /**
     * @return Raw position without eye height
     */
    public Vector3f getRawPosition() {
        return position;
    }

    /**
     * @return Camera collision bounds
     */
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    /**
     * @return Vertical rotation in degrees
     */
    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = Math.max(-89.0f, Math.min(89.0f, pitch));
    }

    /**
     * @return Horizontal rotation in degrees
     */
    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw % 360;
    }

    /**
     * @return Z-axis rotation in degrees
     */
    public float getRoll() {
        return roll;
    }
}