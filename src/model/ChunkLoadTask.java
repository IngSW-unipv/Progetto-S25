package model;

import org.joml.Vector3f;

public class ChunkLoadTask {
    private final Vector3f position;

    public ChunkLoadTask(Vector3f position) {
        this.position = new Vector3f(position);
    }

    public void execute(World world) {
        world.generateChunkTerrain(position);
    }
}