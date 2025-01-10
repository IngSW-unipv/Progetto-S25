package model;

import org.joml.Vector3f;

import java.util.*;

public class World {
    private static final int RENDER_DISTANCE = 2;
    private static final int CHUNK_SIZE = 3;
    private final Set<Chunk> chunks = new HashSet<>();

    private Vector3f lastKnownPlayerPos;

    public World(Vector3f initialPosition) {
        this.lastKnownPlayerPos = initialPosition;
        generateSuperFlat();
    }

    public List<Block> getVisibleBlocks() {
        List<Block> visibleBlocks = new ArrayList<>();
        for(Chunk chunk : chunks) {
            visibleBlocks.addAll(chunk.getBlocks());
        }
        return visibleBlocks;
    }

    private void generateSuperFlat() {
        int playerChunkX = (int) Math.floor(lastKnownPlayerPos.x / CHUNK_SIZE);
        int playerChunkZ = (int) Math.floor(lastKnownPlayerPos.z / CHUNK_SIZE);

        for(int x = -RENDER_DISTANCE; x <= RENDER_DISTANCE; x++) {
            for(int z = -RENDER_DISTANCE; z <= RENDER_DISTANCE; z++) {
                ChunkPosition newPos = new ChunkPosition(playerChunkX + x, playerChunkZ + z);
                generateChunk(newPos);
            }
        }
    }

    private void updateLoadedChunks(Vector3f playerPos) {
        lastKnownPlayerPos = playerPos;
        int playerChunkX = (int) Math.floor(playerPos.x / CHUNK_SIZE);
        int playerChunkZ = (int) Math.floor(playerPos.z / CHUNK_SIZE);

        chunks.removeIf(chunk -> {
            ChunkPosition pos = chunk.getPosition();
            int dx = Math.abs(pos.x() - playerChunkX);
            int dz = Math.abs(pos.z() - playerChunkZ);
            return dx > RENDER_DISTANCE || dz > RENDER_DISTANCE;
        });

        for(int x = -RENDER_DISTANCE; x <= RENDER_DISTANCE; x++) {
            for(int z = -RENDER_DISTANCE; z <= RENDER_DISTANCE; z++) {
                ChunkPosition newPos = new ChunkPosition(playerChunkX + x, playerChunkZ + z);
                if(chunks.stream().noneMatch(c -> c.getPosition().equals(newPos))) {
                    generateChunk(newPos);
                }
            }
        }
    }


    private void generateChunk(ChunkPosition pos) {
        Chunk chunk = new Chunk(pos);
        for(int bx = 0; bx < CHUNK_SIZE; bx++) {
            for(int bz = 0; bz < CHUNK_SIZE; bz++) {
                int worldX = bx + (pos.x() * CHUNK_SIZE);
                int worldZ = bz + (pos.z() * CHUNK_SIZE);

                chunk.setBlock(new Block(BlockType.BEDROCK, new Position(worldX, 0, worldZ)));
                chunk.setBlock(new Block(BlockType.STONE, new Position(worldX, 1, worldZ)));
                chunk.setBlock(new Block(BlockType.DIRT, new Position(worldX, 2, worldZ)));
                chunk.setBlock(new Block(BlockType.GRASS, new Position(worldX, 3, worldZ)));
            }
        }
        chunks.add(chunk);
    }

    public void update(Vector3f playerPos) {
        updateLoadedChunks(playerPos);
    }
}