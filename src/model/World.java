package model;

import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;

public class World {
    private final HashMap<Vector3i, Chunk> chunks;
    private static final int RENDER_DISTANCE = 8;

    public World() {
        this.chunks = new HashMap<>();
    }

    public void generateChunksAroundCamera(Vector3f cameraPos) {
        Vector3i chunkPos = new Vector3i(
                Math.floorDiv((int)cameraPos.x, Chunk.SIZE),
                Math.floorDiv((int)cameraPos.y, Chunk.SIZE),
                Math.floorDiv((int)cameraPos.z, Chunk.SIZE)
        );

        for(int x = -RENDER_DISTANCE; x <= RENDER_DISTANCE; x++) {
            for(int z = -RENDER_DISTANCE; z <= RENDER_DISTANCE; z++) {
                Vector3i pos = new Vector3i(
                        chunkPos.x + x,
                        chunkPos.y,
                        chunkPos.z + z
                );
                if(!chunks.containsKey(pos)) {
                    chunks.put(pos, new Chunk(pos));
                }
            }
        }
    }

    public Map<Vector3i, Chunk> getChunks() {
        return chunks;
    }

    public Block getBlock(int x, int y, int z) {
        Vector3i chunkPos = new Vector3i(
                Math.floorDiv(x, Chunk.SIZE),
                Math.floorDiv(y, Chunk.SIZE),
                Math.floorDiv(z, Chunk.SIZE)
        );
        Chunk chunk = chunks.get(chunkPos);
        if(chunk == null) return null;

        return chunk.getBlock(
                Math.floorMod(x, Chunk.SIZE),
                Math.floorMod(y, Chunk.SIZE),
                Math.floorMod(z, Chunk.SIZE)
        );
    }
}