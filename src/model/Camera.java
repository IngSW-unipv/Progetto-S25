package model;

import config.GameConfig;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position;
    private float pitch;
    private float yaw;
    private float roll;
    private final BoundingBox boundingBox;
    private final CollisionSystem collisionSystem;
    private static final float PLAYER_HEIGHT = 1.8f;
    private static final float PLAYER_WIDTH = 0.6f;
    private float verticalVelocity = 0.0f;
    private boolean isGrounded = false;

    public Camera(CollisionSystem collisionSystem, Vector3f initialPosition) {
        this.collisionSystem = collisionSystem;
        position = initialPosition;
        boundingBox = new BoundingBox(
            PLAYER_WIDTH,
            PLAYER_HEIGHT,
            PLAYER_WIDTH
        );
        this.boundingBox.update(position);
        pitch = 30f;
        yaw = 90;
        roll = 0;
    }

    private boolean checkCollisionInDirection(Vector3f newPosition, Direction direction) {
        float epsilon = 0.1f; // Margine di tolleranza per le collisioni
        BoundingBox potentialBox = new BoundingBox(PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_WIDTH);

        // Testiamo solo nella direzione del movimento
        Vector3f testPosition = new Vector3f(position);
        switch(direction) {
            case HORIZONTAL:
                testPosition.x = newPosition.x;
                testPosition.z = newPosition.z;
                break;
            case VERTICAL:
                testPosition.y = newPosition.y;
                break;
        }

        potentialBox.update(testPosition);
        return collisionSystem.checkCollision(potentialBox);
    }

    public void move(boolean forward, boolean back, boolean left, boolean right, boolean up, boolean down, float deltaTime) {
        float dx = 0, dz = 0;

        // Movimento orizzontale
        if (forward) dz -= GameConfig.CAMERA_MOVEMENT_INCREMENT;
        if (back) dz += GameConfig.CAMERA_MOVEMENT_INCREMENT;
        if (left) dx -= GameConfig.CAMERA_MOVEMENT_INCREMENT;
        if (right) dx += GameConfig.CAMERA_MOVEMENT_INCREMENT;

        if (dx != 0 && dz != 0) {
            dx *= 0.707f;
            dz *= 0.707f;
        }

        // Applica gravità
        verticalVelocity += GameConfig.GRAVITY * deltaTime;
        if (verticalVelocity < GameConfig.TERMINAL_VELOCITY) {
            verticalVelocity = GameConfig.TERMINAL_VELOCITY;
        }

        // Gestisce il salto
        if (up && isGrounded) {
            verticalVelocity = GameConfig.JUMP_FORCE;
            isGrounded = false;
        }

        float angle = (float) Math.toRadians(yaw);
        Vector3f newPosition = new Vector3f(position);

        // Applica movimento orizzontale separatamente
        Vector3f horizontalMove = new Vector3f(
                (float)(dx * Math.cos(angle) - dz * Math.sin(angle)) * GameConfig.CAMERA_MOVE_SPEED * deltaTime,
                0,
                (float)(dx * Math.sin(angle) + dz * Math.cos(angle)) * GameConfig.CAMERA_MOVE_SPEED * deltaTime
        );

        // Prova il movimento orizzontale
        newPosition.x = position.x + horizontalMove.x;
        newPosition.z = position.z + horizontalMove.z;

        if (!checkCollisionInDirection(newPosition, Direction.HORIZONTAL)) {
            position.x = newPosition.x;
            position.z = newPosition.z;
        } else {
            // Prova a muoversi solo su X
            newPosition.x = position.x + horizontalMove.x;
            newPosition.z = position.z;
            if (!checkCollisionInDirection(newPosition, Direction.HORIZONTAL)) {
                position.x = newPosition.x;
            }

            // Prova a muoversi solo su Z
            newPosition.x = position.x;
            newPosition.z = position.z + horizontalMove.z;
            if (!checkCollisionInDirection(newPosition, Direction.HORIZONTAL)) {
                position.z = newPosition.z;
            }
        }

        // Applica movimento verticale separatamente
        newPosition.y = position.y + verticalVelocity * deltaTime;

        if (!checkCollisionInDirection(newPosition, Direction.VERTICAL)) {
            position.y = newPosition.y;
            isGrounded = false;
        } else {
            if (verticalVelocity < 0) {
                isGrounded = true;
                verticalVelocity = 0;
            } else if (verticalVelocity > 0) {
                verticalVelocity = 0;
            }
        }

        boundingBox.update(position);
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