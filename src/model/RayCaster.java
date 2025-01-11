package model;

import org.joml.Vector3f;

public class RayCaster {
    private static final float MAX_DISTANCE = 5.0f;
    private static final float STEP = 0.05f;

    public static Block getTargetBlock(Vector3f cameraPosition, float yaw, float pitch, float roll, World world) {
        Vector3f position = new Vector3f(cameraPosition).add(0, 0.65f, 0);

        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);

        // Correct direction vector calculation
        Vector3f direction = new Vector3f(
                (float) (Math.cos(pitchRad) * Math.sin(yawRad)),
                (float) (-Math.sin(pitchRad)),
                (float) (-Math.cos(pitchRad) * Math.cos(yawRad))
        ).normalize();

        for (float distance = 0; distance <= MAX_DISTANCE; distance += STEP) {
            Vector3f checkPos = new Vector3f(
                    position.x + direction.x * distance,
                    position.y + direction.y * distance,
                    position.z + direction.z * distance
            );

            Position blockPos = new Position(
                    (int) Math.floor(checkPos.x),
                    (int) Math.floor(checkPos.y),
                    (int) Math.floor(checkPos.z)
            );

            Block block = world.getBlock(blockPos);
            if (block != null) {
                return block;
            }
        }

        return null;
    }
}