package model;

import org.joml.Vector3f;

public class RayCaster {
    private static final float MAX_DISTANCE = 5.0f;
    private static final float STEP = 0.05f;

    public static Block getTargetBlock(Vector3f cameraPosition, float yaw, float pitch, float roll, World world) {
        Vector3f position = new Vector3f(cameraPosition).add(0, 0.65f, 0);

        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);

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

    public static BlockDirection getTargetFace(Vector3f cameraPosition, float yaw, float pitch, float roll, World world) {
        Vector3f position = new Vector3f(cameraPosition).add(0, 0.65f, 0);

        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);

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

            // Posizione nel blocco (0-1)
            float blockX = checkPos.x - (float)Math.floor(checkPos.x);
            float blockY = checkPos.y - (float)Math.floor(checkPos.y);
            float blockZ = checkPos.z - (float)Math.floor(checkPos.z);

            Position blockPos = new Position(
                    (int) Math.floor(checkPos.x),
                    (int) Math.floor(checkPos.y),
                    (int) Math.floor(checkPos.z)
            );

            Block block = world.getBlock(blockPos);
            if (block != null) {
                // Determina quale faccia è stata colpita basandosi sulla posizione di impatto
                if (blockX < STEP) return BlockDirection.LEFT;
                if (blockX > 1-STEP) return BlockDirection.RIGHT;
                if (blockY < STEP) return BlockDirection.BOTTOM;
                if (blockY > 1-STEP) return BlockDirection.TOP;
                if (blockZ < STEP) return BlockDirection.BACK;
                if (blockZ > 1-STEP) return BlockDirection.FRONT;
            }
        }
        return null;
    }
}