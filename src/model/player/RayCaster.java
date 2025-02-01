package model.player;

import config.GameConfig;
import model.world.World;
import model.block.Block;
import model.block.BlockDirection;
import org.joml.Vector3f;

/**
 * Casts rays in voxel world.
 * Detects block intersections and face hits.
 */
public class RayCaster {
    /** Maximum distance rays will travel */
    private static final float RAY_MAX_DISTANCE = GameConfig.RAY_MAX_DISTANCE;

    /** Distance between ray samples */
    private static final float STEP = GameConfig.STEP;

    /** Result of a ray intersection test */
    private record RaycastResult(
        Block block,
        Vector3f checkPos,
        Vector3f blockPos
    ) {}


    /**
     * Gets first block intersected by camera ray
     *
     * @param cameraPosition Ray origin
     * @param yaw Camera horizontal angle
     * @param pitch Camera vertical angle
     * @param roll Unused rotation
     * @param world World to check
     */
    public static Block getTargetBlock(Vector3f cameraPosition, float yaw, float pitch, float roll, World world) {
        Vector3f direction = calculateDirection(yaw, pitch);

        for (float distance = 0; distance <= RAY_MAX_DISTANCE; distance += STEP) {
            RaycastResult result = getBlockAtRayPosition(cameraPosition, direction, distance, world);
            if (result.block() != null) {
                return result.block();
            }
        }
        return null;
    }

    /**
     * Gets face hit by camera ray on first intersected block
     *
     * @param cameraPosition Ray origin
     * @param yaw Camera horizontal angle
     * @param pitch Camera vertical angle
     * @param roll Unused rotation
     * @param world World to check
     */
    public static BlockDirection getTargetFace(Vector3f cameraPosition, float yaw, float pitch, float roll, World world) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }

        Vector3f direction = calculateDirection(yaw, pitch);

        for (float distance = 0; distance <= RAY_MAX_DISTANCE; distance += STEP) {
            RaycastResult result = getBlockAtRayPosition(cameraPosition, direction, distance, world);
            if (result.block() != null) {
                // Entry point is one step back from hit
                Vector3f entryPoint = new Vector3f(
                    result.checkPos().x - direction.x * STEP,
                    result.checkPos().y - direction.y * STEP,
                    result.checkPos().z - direction.z * STEP
                );

                // Get offset from block center to entry
                float dx = entryPoint.x - result.blockPos().x;
                float dy = entryPoint.y - result.blockPos().y;
                float dz = entryPoint.z - result.blockPos().z;

                // Find largest axis offset
                float absDx = Math.abs(dx);
                float absDy = Math.abs(dy);
                float absDz = Math.abs(dz);

                // Return face based on largest offset
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
     * Gets block at position along ray
     * Returns block and ray positions
     */
    private static RaycastResult getBlockAtRayPosition(Vector3f cameraPosition, Vector3f direction, float distance, World world) {
        // Get position along ray
        Vector3f checkPos = new Vector3f(
            cameraPosition.x + direction.x * distance,
            cameraPosition.y + direction.y * distance,
            cameraPosition.z + direction.z * distance
        );

        // Convert to block coordinates
        Vector3f blockPos = new Vector3f(
            (int) Math.floor(checkPos.x + 0.5f),
            (int) Math.floor(checkPos.y + 0.5f),
            (int) Math.floor(checkPos.z + 0.5f)
        );

        Block block = world.getBlock(blockPos);
        return new RaycastResult(block, checkPos, blockPos);
    }

    /**
     * Converts camera angles to direction vector
     * Uses spherical coordinates
     */
    private static Vector3f calculateDirection(float yaw, float pitch) {
        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);

        return new Vector3f(
            (float) Math.sin(yawRad) * (float) Math.cos(pitchRad),
            (float) -Math.sin(pitchRad),
            (float) -Math.cos(yawRad) * (float) Math.cos(pitchRad)
        ).normalize();
    }
}