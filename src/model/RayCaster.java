package model;

import org.joml.Vector3f;

/**
 * A utility class for raycasting, used to determine the target block and face
 * in a 3D world based on a camera's position and orientation.
 */
public class RayCaster {
    private static final float MAX_DISTANCE = 5.0f; // Maximum raycasting distance
    private static final float STEP = 0.05f;        // Incremental step size for raycasting

    /**
     * Retrieves the block that the ray intersects with, based on the camera's position and orientation.
     *
     * @param cameraPosition The position of the camera in the world.
     * @param yaw            The yaw (horizontal rotation) angle in degrees.
     * @param pitch          The pitch (vertical rotation) angle in degrees.
     * @param roll           The roll angle in degrees (not used here but included for completeness).
     * @param world          The world object containing blocks.
     * @return The first block intersected by the ray, or null if no block is found.
     */
    public static Block getTargetBlock(Vector3f cameraPosition, float yaw, float pitch, float roll, World world) {
        // Adjust the starting position to account for camera height
        Vector3f position = new Vector3f(cameraPosition).add(0, 0.65f, 0);

        // Calculate the ray direction vector
        Vector3f direction = calculateDirection(yaw, pitch);

        // Perform raycasting up to the maximum distance
        for (float distance = 0; distance <= MAX_DISTANCE; distance += STEP) {
            Vector3f checkPos = new Vector3f(
                    position.x + direction.x * distance,
                    position.y + direction.y * distance,
                    position.z + direction.z * distance
            );

            // Determine the block position being checked
            Position blockPos = new Position(
                    (int) Math.floor(checkPos.x),
                    (int) Math.floor(checkPos.y),
                    (int) Math.floor(checkPos.z)
            );

            // Check if a block exists at this position
            Block block = world.getBlock(blockPos);
            if (block != null) {
                return block; // Return the first block hit
            }
        }

        return null; // No block found within the maximum distance
    }

    /**
     * Determines the face of the block that the ray intersects with.
     *
     * @param cameraPosition The position of the camera in the world.
     * @param yaw            The yaw (horizontal rotation) angle in degrees.
     * @param pitch          The pitch (vertical rotation) angle in degrees.
     * @param roll           The roll angle in degrees (not used here but included for completeness).
     * @param world          The world object containing blocks.
     * @return The face of the block that was hit, or null if no block is found.
     */
    public static BlockDirection getTargetFace(Vector3f cameraPosition, float yaw, float pitch, float roll, World world) {
        // Adjust the starting position to account for camera height
        Vector3f position = new Vector3f(cameraPosition).add(0, 0.65f, 0);

        // Calculate the ray direction vector
        Vector3f direction = calculateDirection(yaw, pitch);

        // Perform raycasting up to the maximum distance
        for (float distance = 0; distance <= MAX_DISTANCE; distance += STEP) {
            Vector3f checkPos = new Vector3f(
                position.x + direction.x * distance,
                position.y + direction.y * distance,
                position.z + direction.z * distance
            );

            // Determine fractional position within the block (0 to 1)
            float blockX = checkPos.x - (float) Math.floor(checkPos.x);
            float blockY = checkPos.y - (float) Math.floor(checkPos.y);
            float blockZ = checkPos.z - (float) Math.floor(checkPos.z);

            // Determine the block position being checked
            Position blockPos = new Position(
                (int) Math.floor(checkPos.x),
                (int) Math.floor(checkPos.y),
                (int) Math.floor(checkPos.z)
            );

            // Check if a block exists at this position
            Block block = world.getBlock(blockPos);
            if (block != null) {
                // Determine which face was hit based on the fractional position
                if (blockX < STEP) return BlockDirection.LEFT;
                if (blockX > 1 - STEP) return BlockDirection.RIGHT;
                if (blockY < STEP) return BlockDirection.BOTTOM;
                if (blockY > 1 - STEP) return BlockDirection.TOP;
                if (blockZ < STEP) return BlockDirection.BACK;
                if (blockZ > 1 - STEP) return BlockDirection.FRONT;
            }
        }

        return null; // No block face found within the maximum distance
    }

    /**
     * Calculates the direction vector based on yaw and pitch angles.
     *
     * @param yaw   The yaw angle in degrees.
     * @param pitch The pitch angle in degrees.
     * @return A normalized direction vector.
     */
    private static Vector3f calculateDirection(float yaw, float pitch) {
        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);

        // Calculate the ray direction vector
        return new Vector3f(
            (float) (Math.cos(pitchRad) * Math.sin(yawRad)),
            (float) (-Math.sin(pitchRad)),
            (float) (-Math.cos(pitchRad) * Math.cos(yawRad))
        ).normalize();
    }
}
