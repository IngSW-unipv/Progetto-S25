package model;

import org.joml.Vector3f;
import java.util.HashMap;
import java.util.Map;

public class OcclusionCulling {
    private final Map<Vector3f, OcclusionState> blockStates = new HashMap<>();

    public void updateOcclusion(Chunk chunk, World world) {
        chunk.getBlocks().forEach(block -> {
            Vector3f pos = block.getPosition();
            boolean isOccluded = isFullyOccluded(pos, world);
            blockStates.put(pos, new OcclusionState(isOccluded));
            block.setVisible(!isOccluded);
        });
    }

    private boolean isFullyOccluded(Vector3f pos, World world) {
        // Check all 6 adjacent blocks
        return isOpaqueBlock(pos.x + 1, pos.y, pos.z, world) &&
                isOpaqueBlock(pos.x - 1, pos.y, pos.z, world) &&
                isOpaqueBlock(pos.x, pos.y + 1, pos.z, world) &&
                isOpaqueBlock(pos.x, pos.y - 1, pos.z, world) &&
                isOpaqueBlock(pos.x, pos.y, pos.z + 1, world) &&
                isOpaqueBlock(pos.x, pos.y, pos.z - 1, world);
    }

    private boolean isOpaqueBlock(float x, float y, float z, World world) {
        Block block = world.getBlock(new Vector3f(x, y, z));
        return block != null && block.getType().isOpaque();
    }

    private record OcclusionState(boolean isOccluded) {}
}