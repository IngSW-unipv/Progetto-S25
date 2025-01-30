package model.player;

import config.GameConfig;
import model.world.World;
import model.block.Block;
import model.block.BlockDirection;
import org.joml.Vector3f;

/**
 * Utility class for raycasting in a voxel world to detect targeted blocks and faces.
 */
public class RayCaster {
    /** Maximum raycast distance in world units */
    private static final float RAY_MAX_DISTANCE = GameConfig.RAY_MAX_DISTANCE;

    /** Size of each ray step in world units */
    private static final float STEP = GameConfig.STEP;

    /**
     * Gets the first block intersected by a ray from the camera.
     *
     * @param cameraPosition Starting position
     * @param yaw Horizontal angle (degrees)
     * @param pitch Vertical angle (degrees)
     * @param roll Z-axis rotation (unused)
     * @param world World to check collisions in
     * @return First intersected block, or null if none found
     * @throws IllegalArgumentException if world is null
     */
    public static Block getTargetBlock(Vector3f cameraPosition, float yaw, float pitch, float roll, World world) {
        // Calculate normalized ray direction from angles
        Vector3f direction = calculateDirection(yaw, pitch);

        // Step along ray checking for blocks
        for (float distance = 0; distance <= RAY_MAX_DISTANCE; distance += STEP) {
            // Calculate current position along ray
            Vector3f checkPos = new Vector3f(
                cameraPosition.x + direction.x * distance,
                cameraPosition.y + direction.y * distance,
                cameraPosition.z + direction.z * distance
            );

            // Convert to block coordinates with center offset
            Vector3f blockPos = new Vector3f(
                (int) Math.floor(checkPos.x + 0.5f),
                (int) Math.floor(checkPos.y + 0.5f),
                (int) Math.floor(checkPos.z + 0.5f)
            );

            Block block = world.getBlock(blockPos);
            if (block != null) {
                return block;
            }
        }
        return null;
    }

    /**
     * Determines which face of an intersected block the ray hit.
     *
     * @param cameraPosition Starting position
     * @param yaw Horizontal angle (degrees)
     * @param pitch Vertical angle (degrees)
     * @param roll Z-axis rotation (unused)
     * @param world World to check collisions in
     * @return Face that was hit, or null if no block found
     * @throws IllegalArgumentException if world is null
     */
    public static BlockDirection getTargetFace(Vector3f cameraPosition, float yaw, float pitch, float roll, World world) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }

        // Calculate normalized ray direction from angles
        Vector3f direction = calculateDirection(yaw, pitch);

        // Step along ray checking for blocks
        for (float distance = 0; distance <= RAY_MAX_DISTANCE; distance += STEP) {
            // Calculate current position along ray
            Vector3f checkPos = new Vector3f(
                    cameraPosition.x + direction.x * distance,
                    cameraPosition.y + direction.y * distance,
                    cameraPosition.z + direction.z * distance
            );

            // Convert to block coordinates with center offset
            Vector3f blockPos = new Vector3f(
                    (int) Math.floor(checkPos.x + 0.5f),
                    (int) Math.floor(checkPos.y + 0.5f),
                    (int) Math.floor(checkPos.z + 0.5f)
            );

            Block block = world.getBlock(blockPos);
            if (block != null) {
                // Calculate the position where the ray enters the block
                Vector3f entryPoint = new Vector3f(
                        checkPos.x - direction.x * STEP,
                        checkPos.y - direction.y * STEP,
                        checkPos.z - direction.z * STEP
                );

                // Determine which face was hit based on the entry point
                float dx = entryPoint.x - blockPos.x;
                float dy = entryPoint.y - blockPos.y;
                float dz = entryPoint.z - blockPos.z;

                // Find the face with the largest absolute delta
                float absDx = Math.abs(dx);
                float absDy = Math.abs(dy);
                float absDz = Math.abs(dz);

                if (absDx > absDy && absDx > absDz) {
                    return dx > 0 ? BlockDirection.RIGHT : BlockDirection.LEFT;
                } else if (absDy > absDz) {
                    return dy > 0 ? BlockDirection.TOP : BlockDirection.BOTTOM;
                } else {
                    return dz > 0 ? BlockDirection.FRONT : BlockDirection.BACK;
                }
            }
        }
        return null;
    }

    /**
     * Converts yaw and pitch angles into a normalized direction vector.
     *
     * @param yaw Horizontal angle (degrees)
     * @param pitch Vertical angle (degrees)
     * @return Unit vector pointing in the specified direction
     */
    private static Vector3f calculateDirection(float yaw, float pitch) {
        // Convert angles to radians
        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);

        // Calculate direction components using spherical coordinates
        return new Vector3f(
            (float) Math.sin(yawRad) * (float) Math.cos(pitchRad),
            (float) -Math.sin(pitchRad),
            (float) -Math.cos(yawRad) * (float) Math.cos(pitchRad)
        ).normalize();
    }
}