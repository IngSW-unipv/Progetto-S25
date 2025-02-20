package model.world;

import model.block.AbstractBlock;
import org.joml.Vector3f;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles occlusion culling to optimize rendering by hiding fully occluded blocks
 * Uses a spatial map to track occlusion states of blocks in each chunk
 */
public class OcclusionCulling {
    /** Maps block positions to their occlusion states */
    private final Map<Vector3f, OcclusionState> blockStates = new HashMap<>();


    /**
     * Updates occlusion states for all blocks in a chunk
     * @param chunk Chunk to update occlusion for
     * @param world Reference to world for neighbor checks
     */
    public void updateOcclusion(Chunk chunk, World world) {
        chunk.getBlocks().forEach(block -> {
            Vector3f pos = block.getPosition();
            // Test if block is surrounded by opaque neighbors
            boolean isOccluded = isFullyOccluded(pos, world);

            // Update block state and visibility
            blockStates.put(pos, new OcclusionState(isOccluded));
            block.setVisible(!isOccluded);
        });
    }

    /**
     * Tests if a block position is completely surrounded by opaque blocks
     * @param pos Position to check
     * @param world World reference for block lookups
     */
    private boolean isFullyOccluded(Vector3f pos, World world) {
        // Check all 6 adjacent faces for opaque blocks
        return isOpaqueBlock(pos.x + 1, pos.y, pos.z, world) && // Right
                isOpaqueBlock(pos.x - 1, pos.y, pos.z, world) && // Left
                isOpaqueBlock(pos.x, pos.y + 1, pos.z, world) && // Top
                isOpaqueBlock(pos.x, pos.y - 1, pos.z, world) && // Bottom
                isOpaqueBlock(pos.x, pos.y, pos.z + 1, world) && // Front
                isOpaqueBlock(pos.x, pos.y, pos.z - 1, world);   // Back
    }

    /**
     * Tests if a block at given coordinates is opaque
     */
    private boolean isOpaqueBlock(float x, float y, float z, World world) {
        AbstractBlock abstractBlock = world.getBlock(new Vector3f(x, y, z));
        return abstractBlock != null && abstractBlock.isOpaque();
    }

    /**
     * Immutable record representing a block's occlusion state
     */
    private record OcclusionState(boolean isOccluded) {}
}