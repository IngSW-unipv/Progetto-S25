package model;

import config.GameConfig;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position;                          // Position of the camera in the world.
    private float pitch;                                // Vertical rotation of the camera.
    private float yaw;                                  // Horizontal rotation of the camera.
    private float roll;                                 // Roll rotation of the camera.
    private final BoundingBox boundingBox;              // Bounding box representing the camera's physical space.
    private final CollisionSystem collisionSystem;      // System to check collisions with the world.
    private static final float PLAYER_HEIGHT = 1.8f;    // Height of the player.
    private static final float PLAYER_WIDTH = 0.6f;     // Width of the player.
    private float verticalVelocity = 0.0f;              // Current vertical velocity of the camera.
    private boolean isGrounded = false;                 // Whether the camera is on the ground.

    public Camera(CollisionSystem collisionSystem, Vector3f initialPosition) {
        this.collisionSystem = collisionSystem; // Initializes the collision system.
        position = initialPosition; // Sets the initial position.
        boundingBox = new BoundingBox(PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_WIDTH); // Creates the bounding box.
        this.boundingBox.update(position); // Updates the bounding box to the initial position.
        pitch = 30f; // Sets the initial pitch.
        yaw = 90; // Sets the initial yaw.
        roll = 0; // Sets the initial roll.
    }

    private boolean checkCollisionInDirection(Vector3f newPosition, Direction direction) {
        float epsilon = 0.1f; // Margin of tolerance for collisions.
        BoundingBox potentialBox = new BoundingBox(PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_WIDTH); // Creates a test bounding box.

        Vector3f testPosition = new Vector3f(position); // Starts with the current position.
        switch (direction) {
            case HORIZONTAL:
                testPosition.x = newPosition.x; // Tests horizontal movement in X.
                testPosition.z = newPosition.z; // Tests horizontal movement in Z.
                break;
            case VERTICAL:
                testPosition.y = newPosition.y; // Tests vertical movement in Y.
                break;
        }

        potentialBox.update(testPosition); // Updates the test bounding box.
        return collisionSystem.checkCollision(potentialBox); // Checks for collisions.
    }

    public void move(boolean forward, boolean back, boolean left, boolean right, boolean up, boolean down, float deltaTime) {
        float dx = 0, dz = 0; // Initializes movement deltas.

        if (forward) dz -= GameConfig.CAMERA_MOVEMENT_INCREMENT; // Moves forward.
        if (back) dz += GameConfig.CAMERA_MOVEMENT_INCREMENT; // Moves backward.
        if (left) dx -= GameConfig.CAMERA_MOVEMENT_INCREMENT; // Moves left.
        if (right) dx += GameConfig.CAMERA_MOVEMENT_INCREMENT; // Moves right.

        if (dx != 0 && dz != 0) { // Normalizes diagonal movement.
            dx *= 0.707f;
            dz *= 0.707f;
        }

        verticalVelocity += GameConfig.GRAVITY * deltaTime; // Applies gravity.
        if (verticalVelocity < GameConfig.TERMINAL_VELOCITY) {
            verticalVelocity = GameConfig.TERMINAL_VELOCITY; // Limits vertical velocity.
        }

        if (up && isGrounded) { // Handles jumping.
            verticalVelocity = GameConfig.JUMP_FORCE;
            isGrounded = false;
        }

        float angle = (float) Math.toRadians(yaw); // Converts yaw to radians.
        Vector3f newPosition = new Vector3f(position); // Starts with the current position.

        Vector3f horizontalMove = new Vector3f( // Calculates horizontal movement.
                (float) (dx * Math.cos(angle) - dz * Math.sin(angle)) * GameConfig.CAMERA_MOVE_SPEED * deltaTime,
                0,
                (float) (dx * Math.sin(angle) + dz * Math.cos(angle)) * GameConfig.CAMERA_MOVE_SPEED * deltaTime
        );

        newPosition.x = position.x + horizontalMove.x; // Tests movement in X.
        newPosition.z = position.z + horizontalMove.z; // Tests movement in Z.

        if (!checkCollisionInDirection(newPosition, Direction.HORIZONTAL)) { // Checks horizontal collisions.
            position.x = newPosition.x;
            position.z = newPosition.z;
        } else {
            newPosition.x = position.x + horizontalMove.x; // Tests movement only in X.
            newPosition.z = position.z;
            if (!checkCollisionInDirection(newPosition, Direction.HORIZONTAL)) {
                position.x = newPosition.x;
            }

            newPosition.x = position.x; // Tests movement only in Z.
            newPosition.z = position.z + horizontalMove.z;
            if (!checkCollisionInDirection(newPosition, Direction.HORIZONTAL)) {
                position.z = newPosition.z;
            }
        }

        newPosition.y = position.y + verticalVelocity * deltaTime; // Applies vertical movement.

        if (!checkCollisionInDirection(newPosition, Direction.VERTICAL)) { // Checks vertical collisions.
            position.y = newPosition.y;
            isGrounded = false;
        } else {
            if (verticalVelocity < 0) { // Handles landing.
                isGrounded = true;
                verticalVelocity = 0;
            } else if (verticalVelocity > 0) { // Handles hitting the ceiling.
                verticalVelocity = 0;
            }
        }

        boundingBox.update(position); // Updates the bounding box.
    }

    public void rotate(float dx, float dy) {
        yaw += dx * GameConfig.CAMERA_MOUSE_SENSITIVITY; // Adjusts yaw.
        pitch += dy * GameConfig.CAMERA_MOUSE_SENSITIVITY; // Adjusts pitch.

        if (pitch > 90) pitch = 90; // Clamps pitch.
        else if (pitch < -90) pitch = -90;

        if (yaw >= 360) yaw -= 360; // Wraps yaw.
        else if (yaw < 0) yaw += 360;
    }

    public Matrix4f getViewMatrix() {
        Matrix4f matrix = new Matrix4f(); // Creates a new matrix.
        matrix.identity(); // Resets the matrix.
        matrix.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0)); // Applies pitch rotation.
        matrix.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0)); // Applies yaw rotation.
        matrix.rotate((float) Math.toRadians(roll), new Vector3f(0, 0, 1)); // Applies roll rotation.
        matrix.translate(new Vector3f(-position.x, -position.y, -position.z)); // Translates to the camera position.
        return matrix;
    }

    public Vector3f getPosition() { return position; } // Returns the camera position.

    public BoundingBox getBoundingBox() { return boundingBox; } // Returns the bounding box.

    public float getPitch() { return pitch; } // Returns the pitch.

    public float getYaw() { return yaw; } // Returns the yaw.

    public float getRoll() { return roll; } // Returns the roll.
}