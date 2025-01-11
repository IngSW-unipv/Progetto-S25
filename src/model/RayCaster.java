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

        Vector3f lastPos = new Vector3f(position);

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
                float dx = checkPos.x - lastPos.x;
                float dy = checkPos.y - lastPos.y;
                float dz = checkPos.z - lastPos.z;

                float absDx = Math.abs(dx);
                float absDy = Math.abs(dy);
                float absDz = Math.abs(dz);

                if (absDx > absDy && absDx > absDz) {
                    return dx > 0 ? BlockDirection.LEFT : BlockDirection.RIGHT;
                } else if (absDy > absDx && absDy > absDz) {
                    return dy > 0 ? BlockDirection.BOTTOM : BlockDirection.TOP;
                } else {
                    return dz > 0 ? BlockDirection.BACK : BlockDirection.FRONT;
                }
            }

            lastPos.set(checkPos);
        }

        return null;
    }
}