package model.physics;

import model.world.World;
import model.block.Block;

/**
 * CollisionSystem is responsible for handling collision detection between
 * entities and blocks in the game world.
 */
public class CollisionSystem {
    private final World world; // Reference to the game world.

    /**
     * Constructor to initialize the CollisionSystem with a given world.
     *
     * @param world The game world containing blocks for collision checks.
     */
    public CollisionSystem(World world) {
        this.world = world;
    }

    /**
     * Checks if the given bounding box collides with any visible blocks in the world.
     *
     * @param boundingBox The bounding box to check for collisions.
     * @return True if a collision is detected, otherwise false.
     */
    public boolean checkCollision(BoundingBox boundingBox) {
        //NON SI PUO FARE SOLO CON I BLOCCHI VICINI AL PLAYER? È UNO SPRECO ALLUCINANTE COSÌ

        // Iterate through all visible blocks in the world
        for (Block block : world.getVisibleBlocks()) {
            // Check if the bounding box intersects with the block's bounding box
            if (boundingBox.intersects(block.getBoundingBox())) {
                return true; // Collision detected
            }
        }
        return false; // No collision detected
    }
}
