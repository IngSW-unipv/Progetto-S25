package model;

import org.joml.Vector3f;
import java.util.*;

public class World {
    private static final int RENDER_DISTANCE = 2;
    public static final int CHUNK_SIZE = 3;
    private final Set<Chunk> chunks = new HashSet<>();
    private Vector3f lastKnownPlayerPos;
    private final PerlinNoiseGenerator terrainNoise;
    private final PerlinNoiseGenerator caveNoise;

    public World(Vector3f initialPosition) {
        this.lastKnownPlayerPos = initialPosition;
        this.terrainNoise = new PerlinNoiseGenerator(1234);
        this.caveNoise = new PerlinNoiseGenerator(5678);
        generateSuperFlat();
    }

    public List<Block> getVisibleBlocks() {
        List<Block> visibleBlocks = new ArrayList<>();
        for(Chunk chunk : chunks) {
            visibleBlocks.addAll(chunk.getBlocks());
        }
        return visibleBlocks;
    }

    public Block getBlock(Position position) {
        int chunkX = (int) Math.floor(position.x() / CHUNK_SIZE);
        int chunkZ = (int) Math.floor(position.z() / CHUNK_SIZE);

        Optional<Chunk> chunk = chunks.stream()
            .filter(c -> c.getPosition().equals(new ChunkPosition(chunkX, chunkZ)))
            .findFirst();

        return chunk.map(c -> c.getBlock(position)).orElse(null);
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

        // Rimuovi i chunk fuori dal render distance
        chunks.removeIf(chunk -> {
            ChunkPosition pos = chunk.getPosition();
            int dx = Math.abs(pos.x() - playerChunkX);
            int dz = Math.abs(pos.z() - playerChunkZ);
            return dx > RENDER_DISTANCE || dz > RENDER_DISTANCE;
        });

        // Genera nuovi chunk dentro il render distance
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

                double noise = terrainNoise.noise(worldX / 32.0, worldZ / 32.0);
                int height = (int)(noise * 32) + 32; // Altezza tra 32 e 64 blocchi

                for(int y = 0; y <= height; y++) {
                    double caveValue = caveNoise.noise3D(worldX / 16.0, y / 16.0, worldZ / 16.0);

                    // Salta se è una caverna
                    if(caveValue > 0.7 && y > 5 && y < height - 1) {
                        continue;
                    }

                    BlockType type = determineBlockType(y, height);
                    Position blockPos = new Position(worldX, y, worldZ);
                    Block block = new Block(type, blockPos);
                    chunk.setBlock(block);
                }
            }
        }

        // Aggiorna le facce visibili di tutti i blocchi nel chunk
        updateChunkBlockFaces(chunk);
        chunks.add(chunk);
    }

    private BlockType determineBlockType(int y, int height) {
        if(y == 0) return BlockType.BEDROCK;
        if(y == height) return BlockType.GRASS;
        if(y > height - 4) return BlockType.DIRT;
        return BlockType.STONE;
    }

    private void updateChunkBlockFaces(Chunk chunk) {
        for (Block block : chunk.getBlocks()) {
            block.updateVisibleFaces(this);
        }
    }

    public void update(Vector3f playerPos) {
        // Aggiorna i chunk caricati
        updateLoadedChunks(playerPos);

        // Aggiorna le facce visibili dei blocchi al bordo dei chunk
        updateBorderBlocksFaces();
    }

    private void updateBorderBlocksFaces() {
        for (Chunk chunk : chunks) {
            for (Block block : chunk.getBlocks()) {
                Position pos = block.getPosition();
                // Aggiorna solo i blocchi al bordo del chunk
                if (isBlockAtChunkBorder(pos)) {
                    block.updateVisibleFaces(this);
                }
            }
        }
    }

    private boolean isBlockAtChunkBorder(Position pos) {
        int localX = Math.floorMod(pos.x(), CHUNK_SIZE);
        int localZ = Math.floorMod(pos.z(), CHUNK_SIZE);
        return localX == 0 || localX == CHUNK_SIZE - 1 ||
                localZ == 0 || localZ == CHUNK_SIZE - 1;
    }
}