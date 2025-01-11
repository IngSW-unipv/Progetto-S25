package model;

import config.GameConfig;
import org.joml.Vector3f;
import java.util.*;

public class World {
    public static final int CHUNK_SIZE = 4;
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
            chunk.getBlocks().stream()
                    .filter(Block::isVisible)
                    .forEach(visibleBlocks::add);
        }
        return visibleBlocks;
    }

    public Block getBlock(Position position) {
        int chunkX = fastFloor(position.x() / (float)CHUNK_SIZE);
        int chunkZ = fastFloor(position.z() / (float)CHUNK_SIZE);

        Optional<Chunk> chunk = chunks.stream()
                .filter(c -> c.getPosition().equals(new ChunkPosition(chunkX, chunkZ)))
                .findFirst();

        return chunk.map(c -> c.getBlock(position)).orElse(null);
    }

    private void generateSuperFlat() {
        int playerChunkX = fastFloor(lastKnownPlayerPos.x / CHUNK_SIZE);
        int playerChunkZ = fastFloor(lastKnownPlayerPos.z / CHUNK_SIZE);

        // Prima genera tutti i chunk
        for(int x = -GameConfig.RENDER_DISTANCE; x <= GameConfig.RENDER_DISTANCE; x++) {
            for(int z = -GameConfig.RENDER_DISTANCE; z <= GameConfig.RENDER_DISTANCE; z++) {
                ChunkPosition newPos = new ChunkPosition(playerChunkX + x, playerChunkZ + z);
                generateChunkTerrain(newPos);
            }
        }

        // Poi aggiorna tutte le facce
        for(Chunk chunk : chunks) {
            updateChunkBlockFaces(chunk);
        }
    }

    private void updateLoadedChunks(Vector3f playerPos) {
        lastKnownPlayerPos = playerPos;
        int playerChunkX = fastFloor(playerPos.x / CHUNK_SIZE);
        int playerChunkZ = fastFloor(playerPos.z / CHUNK_SIZE);

        // Rimuovi i chunk fuori dal render distance
        chunks.removeIf(chunk -> {
            ChunkPosition pos = chunk.getPosition();
            int dx = Math.abs(pos.x() - playerChunkX);
            int dz = Math.abs(pos.z() - playerChunkZ);
            return dx > GameConfig.RENDER_DISTANCE || dz > GameConfig.RENDER_DISTANCE;
        });

        // Prima genera tutti i nuovi chunk necessari
        List<ChunkPosition> newChunks = new ArrayList<>();
        for(int x = -GameConfig.RENDER_DISTANCE; x <= GameConfig.RENDER_DISTANCE; x++) {
            for(int z = -GameConfig.RENDER_DISTANCE; z <= GameConfig.RENDER_DISTANCE; z++) {
                ChunkPosition newPos = new ChunkPosition(playerChunkX + x, playerChunkZ + z);
                if(chunks.stream().noneMatch(c -> c.getPosition().equals(newPos))) {
                    newChunks.add(newPos);
                }
            }
        }

        // Genera il terreno per tutti i nuovi chunk
        for(ChunkPosition pos : newChunks) {
            generateChunkTerrain(pos);
        }

        // Aggiorna le facce per tutti i chunk interessati e i loro vicini
        Set<ChunkPosition> chunksToUpdate = new HashSet<>(newChunks);
        for(ChunkPosition pos : newChunks) {
            for(int dx = -1; dx <= 1; dx++) {
                for(int dz = -1; dz <= 1; dz++) {
                    chunksToUpdate.add(new ChunkPosition(pos.x() + dx, pos.z() + dz));
                }
            }
        }

        chunksToUpdate.forEach(pos ->
                chunks.stream()
                        .filter(c -> c.getPosition().equals(pos))
                        .forEach(this::updateChunkBlockFaces)
        );
    }

    private void generateChunkTerrain(ChunkPosition pos) {
        Chunk chunk = new Chunk(pos);

        for(int bx = 0; bx < CHUNK_SIZE; bx++) {
            for(int bz = 0; bz < CHUNK_SIZE; bz++) {
                int worldX = bx + (pos.x() * CHUNK_SIZE);
                int worldZ = bz + (pos.z() * CHUNK_SIZE);

                double noise = terrainNoise.noise(worldX / 32.0, worldZ / 32.0);
                int height = (int)(noise * 32) + 32;

                for(int y = 0; y <= height; y++) {
                    double caveValue = caveNoise.noise3D(worldX / 16.0, y / 16.0, worldZ / 16.0);
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

        chunks.add(chunk);
    }

    private void updateChunkBlockFaces(Chunk chunk) {
        for (Block block : chunk.getBlocks()) {
            block.updateVisibleFaces(this);
        }
    }

    private BlockType determineBlockType(int y, int height) {
        if(y == 0) return BlockType.BEDROCK;
        if(y == height) return BlockType.GRASS;
        if(y > height - 4) return BlockType.DIRT;
        return BlockType.STONE;
    }

    private int fastFloor(float value) {
        int i = (int)value;
        return value < i ? i - 1 : i;
    }

    public void update(Vector3f playerPos) {
        updateLoadedChunks(playerPos);
    }
}