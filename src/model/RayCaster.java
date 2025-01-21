package model;

import config.GameConfig;
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
        Vector3f position = new Vector3f(cameraPosition);

        // Step along ray checking for blocks
        for (float distance = 0; distance <= RAY_MAX_DISTANCE; distance += STEP) {
            // Calculate current position along ray
            Vector3f checkPos = new Vector3f(
                position.x + direction.x * distance,
                position.y + direction.y * distance,
                position.z + direction.z * distance
            );

            // Convert to block coordinates with center offset
            Position blockPos = new Position(
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
        Vector3f direction = calculateDirection(yaw, pitch);

        // Add eye height offset to camera position
        Vector3f position = new Vector3f(cameraPosition).add(0, .8f, 0);

        // Step along ray checking for block faces
        for (float distance = 0; distance <= RAY_MAX_DISTANCE; distance += STEP) {
            Vector3f checkPos = new Vector3f(
                position.x + direction.x * distance,
                position.y + direction.y * distance,
                position.z + direction.z * distance
        );

            // Get fractional position within block (0 to 1)
            float blockX = checkPos.x - (float) Math.floor(checkPos.x);
            float blockY = checkPos.y - (float) Math.floor(checkPos.y);
            float blockZ = checkPos.z - (float) Math.floor(checkPos.z);

            Position blockPos = new Position(
                (int) Math.floor(checkPos.x),
                (int) Math.floor(checkPos.y),
                (int) Math.floor(checkPos.z)
        );

            Block block = world.getBlock(blockPos);
            if (block != null) {
                // Return face based on which boundary was hit first
                if (blockX < STEP) return BlockDirection.LEFT;
                if (blockX > 1 - STEP) return BlockDirection.RIGHT;
                if (blockY < STEP) return BlockDirection.BOTTOM;
                if (blockY > 1 - STEP) return BlockDirection.TOP;
                if (blockZ < STEP) return BlockDirection.BACK;
                if (blockZ > 1 - STEP) return BlockDirection.FRONT;
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