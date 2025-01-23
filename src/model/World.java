package model;

import config.GameConfig;
import org.joml.Vector3f;
import java.util.*;

/**
 * Represents a voxel world that manages terrain generation and modification.
 * Uses chunks for efficient memory usage and Perlin noise for procedural generation.
 * Handles block placement/destruction and world persistence.
 */
public class World {
    /** Size of each chunk in blocks */
    public static final int CHUNK_SIZE = 6;

    /** Currently loaded chunks */
    private final Set<Chunk> chunks = new HashSet<>();

    /** Tracks blocks modified from original generation */
    private final Map<Vector3f, BlockType> modifiedBlocks = new HashMap<>();

    /** Last known player position for chunk loading */
    private Vector3f lastKnownPlayerPos;

    /** Noise generators for terrain and caves */
    private final PerlinNoiseGenerator terrainNoise;
    private final PerlinNoiseGenerator caveNoise;

    /** World generation seed */
    private final long seed;

    private final ChunkLoader chunkLoader;
    private final Object chunksLock = new Object();

    /**
     * Creates a world with specific player position and seed.
     * Initializes noise generators and generates initial terrain.
     */
    public World(Vector3f initialPosition, long seed) {
        this.lastKnownPlayerPos = initialPosition;
        this.seed = seed;

        // Create separate seeds for terrain and cave generation
        Random random = new Random(seed);
        this.terrainNoise = new PerlinNoiseGenerator(random.nextLong());
        this.caveNoise = new PerlinNoiseGenerator(random.nextLong());

        this.chunkLoader = new ChunkLoader(this, Runtime.getRuntime().availableProcessors() - 1);

        generateSuperFlat();
    }

    /**
     * Creates a world with specific player position and random seed.
     */
    public World(Vector3f initialPosition) {
        // Use current time as random seed
        this(initialPosition, System.currentTimeMillis());
    }

    /**
     * Returns all visible blocks from loaded chunks.
     */
    public List<Block> getVisibleBlocks() {
        List<Block> visibleBlocks = new ArrayList<>();
        // Only collect blocks marked as visible
        for (Chunk chunk : chunks) {
            chunk.getBlocks().stream()
                .filter(Block::isVisible)
                .forEach(visibleBlocks::add);
        }
        return visibleBlocks;
    }

    /**
     * Gets block at specified world position.
     */
    public Block getBlock(Vector3f position) {
        // Convert world position to chunk coordinates
        Vector3f chunkPos = calculateChunkCoordinates(position);

        // Find chunk and get block
        return chunks.stream()
            .filter(c -> c.getPosition().equals(chunkPos))
            .findFirst()
            .map(c -> c.getBlock(position))
            .orElse(null);
    }

    /**
     * Converts world position to chunk coordinates.
     */
    private Vector3f calculateChunkCoordinates(Vector3f position) {
        // Use fast floor division for efficiency
        return new Vector3f(
            fastFloor(position.x() / CHUNK_SIZE),
            fastFloor(position.y() / CHUNK_SIZE),
            fastFloor(position.z() / CHUNK_SIZE)
        );
    }

    /**
     * Generates initial terrain around player.
     */
    private void generateSuperFlat() {
        Vector3f playerChunkPos = calculateChunkCoordinates(lastKnownPlayerPos);
        for (int x = -GameConfig.RENDER_DISTANCE; x <= GameConfig.RENDER_DISTANCE; x++) {
            for (int y = -GameConfig.RENDER_DISTANCE; y <= GameConfig.RENDER_DISTANCE; y++) {
                for (int z = -GameConfig.RENDER_DISTANCE; z <= GameConfig.RENDER_DISTANCE; z++) {
                    Vector3f newPos = new Vector3f(
                            playerChunkPos.x() + x,
                            playerChunkPos.y() + y,
                            playerChunkPos.z() + z
                    );
                    chunkLoader.queueChunkLoad(newPos);
                }
            }
        }
    }

    /**
     * Updates loaded chunks based on player movement.
     */
    private void updateLoadedChunks(Vector3f playerPos) {
        lastKnownPlayerPos = playerPos;
        Vector3f playerChunkPos = calculateChunkCoordinates(playerPos);

        // Unload chunks outside render distance
        chunks.removeIf(chunk -> {
            Vector3f pos = chunk.getPosition();
            float dx = Math.abs(pos.x() - playerChunkPos.x());
            float dy = Math.abs(pos.y() - playerChunkPos.y());
            float dz = Math.abs(pos.z() - playerChunkPos.z());
            return dx > GameConfig.RENDER_DISTANCE ||
                    dy > GameConfig.RENDER_DISTANCE ||
                    dz > GameConfig.RENDER_DISTANCE;
        });

        // Find and generate missing chunks
        List<Vector3f> newChunks = findMissingChunks(playerChunkPos);
        newChunks.forEach(this::generateChunkTerrain);

        // Update affected chunks and neighbors
        updateAffectedChunks(newChunks);
    }

    /**
     * Finds positions where new chunks need generation.
     */
    private List<Vector3f> findMissingChunks(Vector3f playerChunkPos) {
        List<Vector3f> newChunks = new ArrayList<>();

        // Check each position in render distance
        for (int x = -GameConfig.RENDER_DISTANCE; x <= GameConfig.RENDER_DISTANCE; x++) {
            for (int y = -GameConfig.RENDER_DISTANCE; y <= GameConfig.RENDER_DISTANCE; y++) {
                for (int z = -GameConfig.RENDER_DISTANCE; z <= GameConfig.RENDER_DISTANCE; z++) {
                    Vector3f newPos = new Vector3f(
                        playerChunkPos.x() + x,
                        playerChunkPos.y() + y,
                        playerChunkPos.z() + z
                    );
                    // Add if chunk doesn't exist
                    if (chunks.stream().noneMatch(c -> c.getPosition().equals(newPos))) {
                        newChunks.add(newPos);
                    }
                }
            }
        }
        return newChunks;
    }

    /**
     * Updates chunks affected by new chunk generation.
     */
    private void updateAffectedChunks(List<Vector3f> newChunks) {
        Set<Vector3f> chunksToUpdate = new HashSet<>(newChunks);

        // Add all neighboring chunks to update set
        for (Vector3f pos : newChunks) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        chunksToUpdate.add(new Vector3f(
                            pos.x() + dx,
                            pos.y() + dy,
                            pos.z() + dz
                        ));
                    }
                }
            }
        }

        // Update each chunk's block faces
        chunksToUpdate.forEach(pos ->
            chunks.stream()
                .filter(c -> c.getPosition().equals(pos))
                .forEach(this::updateChunkBlockFaces)
        );
    }

    /**
     * Generates terrain for a single chunk.
     */
    public synchronized void generateChunkTerrain(Vector3f pos) {
        synchronized(chunksLock) {
            if (chunks.stream().anyMatch(c -> c.getPosition().equals(pos))) {
                return;
            }

            Chunk chunk = new Chunk(pos);
            for (int bx = 0; bx < CHUNK_SIZE; bx++) {
                for (int by = 0; by < CHUNK_SIZE; by++) {
                    for (int bz = 0; bz < CHUNK_SIZE; bz++) {
                        generateBlockAt(chunk, pos, bx, by, bz);
                    }
                }
            }
            chunks.add(chunk);
            updateChunkBlockFaces(chunk);
        }
    }

    /**
     * Generates a single block during chunk generation.
     */
    private void generateBlockAt(Chunk chunk, Vector3f chunkPos, int bx, int by, int bz) {
        // Convert to world coordinates
        float worldX = bx + (chunkPos.x() * CHUNK_SIZE);
        float worldY = by + (chunkPos.y() * CHUNK_SIZE);
        float worldZ = bz + (chunkPos.z() * CHUNK_SIZE);

        // Generate terrain height
        double noise = terrainNoise.noise(worldX / 32.0, worldZ / 32.0);
        int height = (int)(noise * 32) + 32;

        if (worldY <= height) {
            // Check for cave generation
            double caveValue = caveNoise.noise3D(worldX / 16.0, worldY / 16.0, worldZ / 16.0);
            if (caveValue > 0.7 && worldY > 5 && worldY < height - 1) {
                return;
            }

            // Create block with appropriate type
            BlockType type = determineBlockType((int)worldY, height);
            Vector3f blockPos = new Vector3f(worldX, worldY, worldZ);
            Block block = new Block(type, blockPos);
            chunk.setBlock(block);
        }
    }

    /**
     * Updates visibility of block faces in chunk.
     */
    private void updateChunkBlockFaces(Chunk chunk) {
        chunk.getBlocks().forEach(block -> block.updateVisibleFaces(this));
    }

    /**
     * Determines block type based on height.
     */
    private BlockType determineBlockType(int y, int height) {
        // Layer-based block type selection
        if (y == 0) return BlockType.BEDROCK;
        if (y == height) return BlockType.GRASS;
        if (y > height - 4) return BlockType.DIRT;
        return BlockType.STONE;
    }

    /**
     * Fast floor operation for chunk calculations.
     */
    private int fastFloor(float value) {
        int i = (int) value;
        return value < i ? i - 1 : i;
    }

    /**
     * Updates blocks adjacent to chunk boundaries.
     */
    private void updateNeighboringChunks(Vector3f position, Vector3f chunkPos) {
        // Calculate local coordinates within chunk
        float localX = position.x() - chunkPos.x() * CHUNK_SIZE;
        float localY = position.y() - chunkPos.y() * CHUNK_SIZE;
        float localZ = position.z() - chunkPos.z() * CHUNK_SIZE;

        // Update neighboring chunks at boundaries
        if (localX == 0) updateNeighborChunk((int)chunkPos.x() - 1, (int)chunkPos.y(), (int)chunkPos.z());
        if (localX == CHUNK_SIZE - 1) updateNeighborChunk((int)chunkPos.x() + 1, (int)chunkPos.y(), (int)chunkPos.z());
        if (localY == 0) updateNeighborChunk((int)chunkPos.x(), (int)chunkPos.y() - 1, (int)chunkPos.z());
        if (localY == CHUNK_SIZE - 1) updateNeighborChunk((int)chunkPos.x(), (int)chunkPos.y() + 1, (int)chunkPos.z());
        if (localZ == 0) updateNeighborChunk((int)chunkPos.x(), (int)chunkPos.y(), (int)chunkPos.z() - 1);
        if (localZ == CHUNK_SIZE - 1) updateNeighborChunk((int)chunkPos.x(), (int)chunkPos.y(), (int)chunkPos.z() + 1);
    }

    /**
     * Updates a neighboring chunk.
     */
    private void updateNeighborChunk(int x, int y, int z) {
        Vector3f neighborPos = new Vector3f(x, y, z);
        chunks.stream()
            .filter(c -> c.getPosition().equals(neighborPos))
            .findFirst()
            .ifPresent(this::updateChunkBlockFaces);
    }

    /**
     * Updates faces of blocks adjacent to position.
     */
    private void updateAdjacentBlockFaces(Vector3f position) {
        // Define adjacent positions
        Vector3f[] adjacentPositions = {
            new Vector3f(position.x() + 1, position.y(), position.z()),
            new Vector3f(position.x() - 1, position.y(), position.z()),
            new Vector3f(position.x(), position.y() + 1, position.z()),
            new Vector3f(position.x(), position.y() - 1, position.z()),
            new Vector3f(position.x(), position.y(), position.z() + 1),
            new Vector3f(position.x(), position.y(), position.z() - 1)
        };

        // Update each adjacent block
        for (Vector3f adjacentPos : adjacentPositions) {
            Optional.ofNullable(getBlock(adjacentPos))
                .ifPresent(block -> block.updateVisibleFaces(this));
        }
    }

    /**
     * Places block at specified position.
     */
    public void placeBlock(Vector3f position, BlockType type) {
        Vector3f chunkPos = calculateChunkCoordinates(position);
        chunks.stream()
            .filter(c -> c.getPosition().equals(chunkPos))
            .findFirst()
            .ifPresent(chunk -> {
                // Create and place new block
                Block newBlock = new Block(type, position);
                chunk.setBlock(newBlock);
                updateChunkBlockFaces(chunk);
                updateNeighboringChunks(position, chunkPos);
                modifiedBlocks.put(position, type);
            });
        modifiedBlocks.put(new Vector3f(position), type);
    }

    /**
     * Destroys block at specified position.
     */
    public void destroyBlock(Vector3f position) {
        Vector3f chunkPos = calculateChunkCoordinates(position);
        chunks.stream()
            .filter(c -> c.getPosition().equals(chunkPos))
            .findFirst()
            .ifPresent(chunk -> {
                // Update neighbors before removal
                updateAdjacentBlockFaces(position);
                chunk.removeBlock(position);
                updateChunkBlockFaces(chunk);
                updateNeighboringChunks(position, chunkPos);
                modifiedBlocks.remove(position);
            });
        modifiedBlocks.put(new Vector3f(position), null); // null indicates block removal
    }

    /**
     * Gets map of modified blocks.
     */
    public Map<Vector3f, BlockType> getModifiedBlocks() {
        return new HashMap<>(modifiedBlocks);
    }

    /**
     * Gets world generation seed.
     */
    public long getSeed() {
        return seed;
    }

    /**
     * Updates world state based on player position.
     */
    public void update(Vector3f playerPos) {
        updateLoadedChunks(playerPos);
    }

    public void cleanup() {
        chunkLoader.shutdown();
    }
}