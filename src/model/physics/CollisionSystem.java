package model.physics;

import model.block.AbstractBlock;
import model.world.World;
import org.joml.Vector3f;
import java.util.List;

/**
 * Handles entity collision detection with blocks.
 * Checks for collisions between bounding boxes.
 */
public class CollisionSystem {
    private final World world;


    public CollisionSystem(World world) {
        this.world = world;
    }

    /**
     * Checks if a bounding box collides with any blocks in the world
     */
    public boolean checkCollision(BoundingBox box) {
        // Get nearby blocks for collision check
        List<AbstractBlock> nearbyAbstractBlocks = getNearbyBlocks(box);

        for (AbstractBlock abstractBlock : nearbyAbstractBlocks) {
            if (box.intersects(abstractBlock.getBoundingBox())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets blocks that could be hit during movement
     */
    private List<AbstractBlock> getNearbyBlocks(BoundingBox box) {
        return world.getVisibleBlocks().stream()
            .filter(block -> {
                Vector3f pos = block.getPosition();
                return pos.x() >= box.getMin().x() - 1 && pos.x() <= box.getMax().x() + 1 &&
                        pos.y() >= box.getMin().y() - 1 && pos.y() <= box.getMax().y() + 1 &&
                        pos.z() >= box.getMin().z() - 1 && pos.z() <= box.getMax().z() + 1;
            })
            .toList();
    }
}