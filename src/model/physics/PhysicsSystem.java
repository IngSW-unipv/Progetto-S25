package model.physics;

import config.GameConfig;
import model.player.Player;
import model.world.World;
import model.block.AbstractBlock;
import org.joml.Vector3f;

/**
 * Physics simulation system for movement and collisions.
 * Manages gravity, velocity and position updates.
 */
public class PhysicsSystem {
    /** World components */
    private final World world;
    private final CollisionSystem collisionSystem;

    /** Physics constants */
    public static final float GRAVITY = GameConfig.GRAVITY;
    public static final float MOVE_SPEED = GameConfig.MOVE_SPEED;


    /** Creates physics system for world */
    public PhysicsSystem(World world) {
        this.world = world;
        this.collisionSystem = new CollisionSystem(world);
    }

    /** Updates player physics */
    public void updatePlayerPhysics(Player player, float deltaTime) {
        updateVelocity(player, deltaTime);
        updatePosition(player, deltaTime);
    }

    /** Updates velocity with gravity and acceleration */
    private void updateVelocity(Player player, float deltaTime) {
        Vector3f velocity = player.getVelocity();
        Vector3f acceleration = player.getAcceleration();

        // Apply movement speed
        float speedMult = player.isSprinting() ? 2.0f : 1.0f;
        velocity.x = acceleration.x * MOVE_SPEED * speedMult;
        velocity.z = acceleration.z * MOVE_SPEED * speedMult;

        // Apply gravity
        float gravity = velocity.y > 0 ? GRAVITY * 2.5f : GRAVITY;
        if (!isBlockBelow(player)) {
            velocity.y += gravity * deltaTime;
        } else if (velocity.y <= 0) {
            velocity.y = 0;
            player.setGrounded(true);
        }

        player.setVelocity(velocity);
    }

    /** Checks for block below player */
    private boolean isBlockBelow(Player player) {
        Vector3f pos = player.getPosition();
        AbstractBlock abstractBlock = world.getBlock(new Vector3f(pos.x, pos.y - 0.5f, pos.z));
        return abstractBlock != null && abstractBlock.getType() != null;
    }

    /** Updates position with collision detection */
    private void updatePosition(Player player, float deltaTime) {
        Vector3f currentPos = player.getPosition();
        Vector3f velocity = player.getVelocity();
        Vector3f newPosition = new Vector3f(currentPos);

        // Move horizontally then vertically
        tryMove(player, newPosition,
                new Vector3f(velocity.x * deltaTime, 0, velocity.z * deltaTime));
        tryMove(player, newPosition,
                new Vector3f(0, velocity.y * deltaTime, 0));

        player.setPosition(newPosition);
    }

    /** Attempts movement with collision checks */
    private void tryMove(Player player, Vector3f newPosition, Vector3f delta) {
        Vector3f testPos = new Vector3f(newPosition).add(delta);

        if (canMoveTo(player, testPos)) {
            newPosition.set(testPos);
            return;
        }

        // Try component movements if full move blocked
        if (delta.x != 0) {
            tryXMovement(player, newPosition, delta);
        }
        if (delta.z != 0) {
            tryZMovement(player, newPosition, delta);
        }
        if (delta.y != 0) {
            tryYMovement(player, newPosition, delta);
        }
    }

    /** Attempts X-axis movement */
    private void tryXMovement(Player player, Vector3f newPosition, Vector3f delta) {
        Vector3f testPos = new Vector3f(
                newPosition.x + delta.x,
                newPosition.y,
                newPosition.z
        );
        if (canMoveTo(player, testPos)) {
            newPosition.set(testPos);
        }
    }

    /** Attempts Y-axis movement */
    private void tryYMovement(Player player, Vector3f newPosition, Vector3f delta) {
        Vector3f testPos = new Vector3f(
                newPosition.x,
                newPosition.y + delta.y,
                newPosition.z
        );
        if (canMoveTo(player, testPos)) {
            newPosition.set(testPos);
            player.setGrounded(false);
        } else if (delta.y < 0) {
            player.setGrounded(true);
            player.getVelocity().y = 0;
        }
    }

    /** Attempts Z-axis movement */
    private void tryZMovement(Player player, Vector3f newPosition, Vector3f delta) {
        Vector3f testPos = new Vector3f(
                newPosition.x,
                newPosition.y,
                newPosition.z + delta.z
        );
        if (canMoveTo(player, testPos)) {
            newPosition.set(testPos);
        }
    }

    /** Checks if position is collision-free */
    private boolean canMoveTo(Player player, Vector3f position) {
        BoundingBox boundingBox = player.getBoundingBox();
        boundingBox.update(position);
        return !collisionSystem.checkCollision(boundingBox);
    }
}