//
//physicssystem

package model.physics;

import config.GameConfig;
import model.player.Player;
import model.world.World;
import model.block.Block;
import org.joml.Vector3f;

public class PhysicsSystem {
    private final World world;
    private final CollisionSystem collisionSystem;

    public static final float GRAVITY = GameConfig.GRAVITY;
    public static final float MOVE_SPEED = GameConfig.MOVE_SPEED;

    public PhysicsSystem(World world) {
        this.world = world;
        this.collisionSystem = new CollisionSystem(world);
    }



    public void updatePlayerPhysics(Player player, float deltaTime) {
        updateVelocity(player, deltaTime);
        updatePosition(player, deltaTime);
    }

    private void updateVelocity(Player player, float deltaTime) {
        Vector3f velocity = player.getVelocity();
        Vector3f acceleration = player.getAcceleration();

        // Horizontal movement with sprint factor
        float speedMultiplier = player.isSprinting() ? 2.0f : 1.0f;
        velocity.x = acceleration.x * MOVE_SPEED * speedMultiplier;
        velocity.z = acceleration.z * MOVE_SPEED * speedMultiplier;
        float currentGravity = velocity.y > 0 ? GRAVITY * 2.5f : GRAVITY;

        if (!isBlockBelow(player)) {
            velocity.y += currentGravity * deltaTime;
        } else if (velocity.y <= 0) {
            velocity.y = 0;
            player.setGrounded(true);
        }

        player.setVelocity(velocity);
    }

    private boolean isBlockBelow(Player player) {
        Vector3f pos = player.getPosition();
        Block block = world.getBlock(new Vector3f(pos.x, pos.y - 0.5f, pos.z));
        return block != null && block.getType() != null;
    }

    private void updatePosition(Player player, float deltaTime) {
        Vector3f currentPos = player.getPosition();
        Vector3f velocity = player.getVelocity();
        Vector3f newPosition = new Vector3f(currentPos);

        tryMove(player, newPosition, new Vector3f(velocity.x * deltaTime, 0, velocity.z * deltaTime));
        tryMove(player, newPosition, new Vector3f(0, velocity.y * deltaTime, 0));

        player.setPosition(newPosition);
    }

    private void tryMove(Player player, Vector3f newPosition, Vector3f delta) {
        Vector3f testPos = new Vector3f(newPosition).add(delta);

        if (canMoveTo(player, testPos)) {
            newPosition.set(testPos);
        } else {
            if (delta.x != 0) {
                testPos.set(newPosition.x + delta.x, newPosition.y, newPosition.z);
                if (canMoveTo(player, testPos)) {
                    newPosition.set(testPos);
                }
            }
            if (delta.z != 0) {
                testPos.set(newPosition.x, newPosition.y, newPosition.z + delta.z);
                if (canMoveTo(player, testPos)) {
                    newPosition.set(testPos);
                }
            }
            if (delta.y != 0) {
                testPos.set(newPosition.x, newPosition.y + delta.y, newPosition.z);
                if (canMoveTo(player, testPos)) {
                    newPosition.set(testPos);
                    player.setGrounded(false);
                } else if (delta.y < 0) {
                    player.setGrounded(true);
                    player.getVelocity().y = 0;
                }
            }
        }
    }

    private boolean canMoveTo(Player player, Vector3f position) {
        BoundingBox boundingBox = player.getBoundingBox();
        boundingBox.update(position);
        return !collisionSystem.checkCollision(boundingBox);
    }
}