package model;

import config.GameConfig;
import org.joml.Vector3f;
import java.util.*;

public class World {
    public static final int CHUNK_SIZE = 6;
    private final Set<Chunk> chunks = new HashSet<>();
    private Vector3f lastKnownPlayerPos;
    private final PerlinNoiseGenerator terrainNoise;
    private final PerlinNoiseGenerator caveNoise;
    private final long seed;

    public World(Vector3f initialPosition, long seed) {
        this.lastKnownPlayerPos = initialPosition;
        this.seed = seed;

        // Use the main seed to generate consistent but different seeds for terrain and caves
        Random random = new Random(seed);
        this.terrainNoise = new PerlinNoiseGenerator(random.nextLong());
        this.caveNoise = new PerlinNoiseGenerator(random.nextLong());

        generateSuperFlat();
    }

    // Default constructor for backwards compatibility
    public World(Vector3f initialPosition) {
        this(initialPosition, System.currentTimeMillis());
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

    private void updateNeighborChunk(int x, int z) {
        chunks.stream()
                .filter(c -> c.getPosition().equals(new ChunkPosition(x, z)))
                .findFirst()
                .ifPresent(this::updateChunkBlockFaces);
    }

    public void placeBlock(Position position, BlockType type) {
        int chunkX = fastFloor(position.x() / (float)CHUNK_SIZE);
        int chunkZ = fastFloor(position.z() / (float)CHUNK_SIZE);

        chunks.stream()
                .filter(c -> c.getPosition().equals(new ChunkPosition(chunkX, chunkZ)))
                .findFirst()
                .ifPresent(chunk -> {
                    Block newBlock = new Block(type, position);
                    chunk.setBlock(newBlock);
                    updateChunkBlockFaces(chunk);

                    // Update neighboring chunks if the block was placed on a border
                    int localX = position.x() - chunkX * CHUNK_SIZE;
                    int localZ = position.z() - chunkZ * CHUNK_SIZE;

                    if (localX == 0) updateNeighborChunk(chunkX - 1, chunkZ);
                    if (localX == CHUNK_SIZE - 1) updateNeighborChunk(chunkX + 1, chunkZ);
                    if (localZ == 0) updateNeighborChunk(chunkX, chunkZ - 1);
                    if (localZ == CHUNK_SIZE - 1) updateNeighborChunk(chunkX, chunkZ + 1);
                });
    }

    private void updateAdjacentBlockFaces(Position position) {
        // Update blocks in all 6 directions
        Position[] adjacentPositions = {
                new Position(position.x() + 1, position.y(), position.z()), // Right
                new Position(position.x() - 1, position.y(), position.z()), // Left
                new Position(position.x(), position.y() + 1, position.z()), // Top
                new Position(position.x(), position.y() - 1, position.z()), // Bottom
                new Position(position.x(), position.y(), position.z() + 1), // Front
                new Position(position.x(), position.y(), position.z() - 1)  // Back
        };

        for (Position adjacentPos : adjacentPositions) {
            Block adjacentBlock = getBlock(adjacentPos);
            if (adjacentBlock != null) {
                adjacentBlock.updateVisibleFaces(this);
            }
        }
    }

    public void destroyBlock(Position position) {
        int chunkX = fastFloor(position.x() / (float)CHUNK_SIZE);
        int chunkZ = fastFloor(position.z() / (float)CHUNK_SIZE);

        chunks.stream()
            .filter(c -> c.getPosition().equals(new ChunkPosition(chunkX, chunkZ)))
            .findFirst()
            .ifPresent(chunk -> {
                // Update faces of adjacent blocks before removing the target block
                updateAdjacentBlockFaces(position);

                chunk.removeBlock(position);
                updateChunkBlockFaces(chunk);

                // Update neighboring chunks if the block was on a border
                int localX = position.x() - chunkX * CHUNK_SIZE;
                int localZ = position.z() - chunkZ * CHUNK_SIZE;

                if (localX == 0) updateNeighborChunk(chunkX - 1, chunkZ);
                if (localX == CHUNK_SIZE - 1) updateNeighborChunk(chunkX + 1, chunkZ);
                if (localZ == 0) updateNeighborChunk(chunkX, chunkZ - 1);
                if (localZ == CHUNK_SIZE - 1) updateNeighborChunk(chunkX, chunkZ + 1);
            });
    }

    public long getSeed() {
        return seed;
    }

    public void update(Vector3f playerPos) {
        updateLoadedChunks(playerPos);
    }
}