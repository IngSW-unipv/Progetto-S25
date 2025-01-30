package model.world;

import model.block.Block;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a chunk in the world, which contains blocks and is identified by its position.
 */
public class Chunk {
    private final Vector3f position;                           // Position of the chunk in the world
    private final Map<Vector3f, Block> blocks = new HashMap<>();    // Map of blocks within the chunk

    /**
     * Constructs a new chunk at the specified position.
     *
     * @param position The position of the chunk.
     */
    public Chunk(Vector3f position) {
        this.position = position;
    }

    /**
     * Adds or updates a block in the chunk.
     *
     * @param block The block to be added or updated.
     */
    public void setBlock(Block block) {
        blocks.put(block.getPosition(), block);
    }

    /**
     * Retrieves a block at the specified position within the chunk.
     *
     * @param position The position of the block to retrieve.
     * @return The block at the specified position, or {@code null} if no block exists.
     */
    public Block getBlock(Vector3f position) {
        return blocks.get(position);
    }

    /**
     * Removes the block at the specified position within the chunk.
     *
     * @param position The position of the block to remove.
     */
    public void removeBlock(Vector3f position) {
        blocks.remove(position);
    }

    /**
     * Retrieves all blocks in the chunk.
     *
     * @return A collection of all blocks contained in the chunk.
     */
    public Collection<Block> getBlocks() {
        return blocks.values();
    }

    /**
     * Retrieves the position of the chunk.
     *
     * @return The position of the chunk in the world.
     */
    public Vector3f getPosition() {
        return position;
    }
}