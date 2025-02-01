package model.physics;

import model.world.World;
import model.block.Block;

/**
 * Handles entity collision detection with blocks.
 * Checks for collisions between bounding boxes.
 */
public class CollisionSystem {
    /** Reference to game world */
    private final World world;


    /** Creates collision system for world */
    public CollisionSystem(World world) {
        this.world = world;
    }

    /** Checks box collision with visible blocks */
    public boolean checkCollision(BoundingBox boundingBox) {
        // TODO: Optimize to only check nearby blocks
        for (Block block : world.getVisibleBlocks()) {
            if (boundingBox.intersects(block.getBoundingBox())) {
                return true;
            }
        }
        return false;
    }
}