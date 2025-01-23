package model;

import org.joml.Vector3f;
import java.util.*;

public class LightingSystem {
    private static final int MAX_LIGHT_LEVEL = 15;
    private static final int MIN_LIGHT_LEVEL = 0;
    private final Map<Vector3f, Integer> lightLevels = new HashMap<>();
    private final World world;

    public LightingSystem(World world) {
        this.world = world;
    }

    public void updateLighting(Vector3f position) {
        Queue<Vector3f> lightQueue = new LinkedList<>();
        Set<Vector3f> visited = new HashSet<>();

        lightQueue.offer(position);
        lightLevels.put(position, MAX_LIGHT_LEVEL);

        while (!lightQueue.isEmpty()) {
            Vector3f current = lightQueue.poll();
            int currentLevel = lightLevels.get(current);

            if (currentLevel <= MIN_LIGHT_LEVEL) continue;

            for (BlockDirection direction : BlockDirection.values()) {
                Vector3f neighbor = new Vector3f(
                        current.x + direction.getDx(),
                        current.y + direction.getDy(),
                        current.z + direction.getDz()
                );

                if (visited.contains(neighbor)) continue;

                Block block = world.getBlock(neighbor);
                int reduction = (block != null && block.getType().isOpaque()) ? 2 : 1;
                int neighborLevel = currentLevel - reduction;

                if (neighborLevel > getLightLevel(neighbor)) {
                    lightLevels.put(neighbor, neighborLevel);
                    lightQueue.offer(neighbor);
                }

                visited.add(neighbor);
            }
        }
    }

    public int getLightLevel(Vector3f position) {
        return lightLevels.getOrDefault(position, MIN_LIGHT_LEVEL);
    }

    public void removeLightSource(Vector3f position) {
        lightLevels.remove(position);
        updateLighting(position);
    }
}