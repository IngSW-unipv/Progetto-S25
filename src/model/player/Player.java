//player

package model.player;

import model.physics.BoundingBox;
import model.physics.PhysicsSystem;
import org.joml.Vector3f;

public class Player {
    private final PhysicsSystem physicsSystem;
    private final BoundingBox boundingBox;
    private Vector3f position;
    private Vector3f velocity;
    private Vector3f acceleration;
    private boolean isGrounded;
    private boolean sprinting;
    private final Camera camera;

    private static final float PLAYER_HEIGHT = 1.8f;
    private static final float PLAYER_WIDTH = 0.6f;

    public Player(PhysicsSystem physicsSystem, Vector3f spawnPosition, float pitch, float yaw) {
        this.physicsSystem = physicsSystem;
        this.position = spawnPosition;
        this.velocity = new Vector3f(0);
        this.acceleration = new Vector3f(0);
        this.boundingBox = new BoundingBox(PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_WIDTH);
        this.camera = new Camera(spawnPosition);
        this.camera.setPitch(pitch);
        this.camera.setYaw(yaw);
        updateBoundingBox();
    }

    public void update(float deltaTime) {
        physicsSystem.updatePlayerPhysics(this, deltaTime);
        updateBoundingBox();
        camera.setPosition(position);
    }

    private void updateBoundingBox() {
        boundingBox.update(position);
    }

    public Vector3f getPosition() { return position; }
    public void setPosition(Vector3f position) {
        this.position = position;
        camera.setPosition(position);
    }
    public float getPitch() { return camera.getPitch(); }
    public void setPitch(float pitch) { camera.setPitch(pitch); }
    public float getYaw() { return camera.getYaw(); }
    public void setYaw(float yaw) { camera.setYaw(yaw); }
    public Vector3f getVelocity() { return velocity; }
    public void setVelocity(Vector3f velocity) { this.velocity = velocity; }
    public Vector3f getAcceleration() { return acceleration; }
    public void setAcceleration(Vector3f acceleration) { this.acceleration = acceleration; }
    public boolean isGrounded() { return isGrounded; }
    public void setGrounded(boolean grounded) { isGrounded = grounded; }
    public BoundingBox getBoundingBox() { return boundingBox; }
    public boolean isSprinting() { return sprinting; }
    public void setSprinting(boolean sprinting) { this.sprinting = sprinting; }
    public Camera getCamera() { return camera; }

    public Vector3f getCameraPosition() {
        return camera.getPosition();
    }

    public Vector3f getRawPosition() {
        return new Vector3f(position);
    }
}