package model.world;

import config.GameConfig;
import controller.event.*;
import controller.event.EventListener;
import model.block.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import util.PerformanceMetrics;
import java.util.*;

/**
 * Manages voxel world with dynamic chunk loading and terrain generation
 * Handles block manipulation, terrain features, and rendering optimizations
 */
public class World implements EventListener {
    /** Core configuration */
    public static final int CHUNK_SIZE = 6;
    private final long seed;
    private Vector3f lastKnownPlayerPos;

    /** Storage and synchronization */
    private final Set<Chunk> chunks = new HashSet<>();
    private final Map<Vector3f, BlockType> modifiedBlocks = new HashMap<>();
    private final Object chunksLock = new Object();

    /** World systems */
    private final ChunkLoader chunkLoader;
    private final PerlinNoiseGenerator terrainNoise;
    private final PerlinNoiseGenerator caveNoise;
    private final Frustum frustum = new Frustum();
    private final OcclusionCulling occlusionCulling = new OcclusionCulling();
    private final DayNightCycle dayNightCycle = new DayNightCycle();


    /**
     * Creates new world instance with given spawn position and seed
     */
    public World(Vector3f initialPosition, long seed) {
        this.lastKnownPlayerPos = initialPosition;
        this.seed = seed;

        // Initialize generation systems
        Random random = new Random(seed);
        this.terrainNoise = new PerlinNoiseGenerator(random.nextLong());
        this.caveNoise = new PerlinNoiseGenerator(random.nextLong());

        // Setup chunk loading
        this.chunkLoader = new ChunkLoader(Runtime.getRuntime().availableProcessors() - 1);
        EventBus.getInstance().subscribe(EventType.WORLD_GENERATION, this);

        generateSuperFlat();
    }

    /**
     * Handles chunk generation events
     */
    @Override
    public void onEvent(GameEvent event) {
        if (event instanceof WorldGenerationEvent worldGen) {
            generateChunkTerrain(worldGen.chunkPosition());
        }
    }

    /**
     * Returns all blocks that should be rendered this frame.
     * Applies frustum culling and occlusion optimizations.
     * Maintains performance statistics for analysis.
     *
     * @return List of blocks to render based on visibility and culling
     */
    public List<AbstractBlock> getVisibleBlocks() {
        List<AbstractBlock> visibleAbstractBlocks = new ArrayList<>();
        int totalChunkCount = 0;
        int culledChunkCount = 0;

        synchronized(chunksLock) {
            // Process each chunk with frustum culling
            for (Chunk chunk : chunks) {
                totalChunkCount++;
                boolean isChunkVisible = frustum.isChunkInFrustum(chunk.getPosition(), CHUNK_SIZE);

                Collection<AbstractBlock> chunkAbstractBlocks = chunk.getBlocks();
                if (!isChunkVisible) {
                    // Skip culled chunks but track statistics
                    culledChunkCount++;
                    PerformanceMetrics.logBlocks(
                        chunkAbstractBlocks.size(),  // Total blocks in chunk
                        0,                   // No blocks rendered
                        0,                   // None occluded (all culled)
                        chunkAbstractBlocks.size()   // All blocks culled with chunk
                    );
                    continue;
                }

                // Count visible and occluded blocks in visible chunks
                int totalInChunk = chunkAbstractBlocks.size();
                int visibleInChunk = (int) chunkAbstractBlocks.stream()
                        .filter(AbstractBlock::isVisible)
                        .count();
                int occludedInChunk = totalInChunk - visibleInChunk;

                // Log statistics for visible chunks
                PerformanceMetrics.logBlocks(
                    totalInChunk,     // Total blocks
                    visibleInChunk,   // Blocks to render
                    occludedInChunk,  // Hidden by other blocks
                    0                 // None culled (chunk visible)
                );

                // Add visible blocks to render list
                chunkAbstractBlocks.stream()
                    .filter(AbstractBlock::isVisible)
                    .forEach(visibleAbstractBlocks::add);
            }

            // Update chunk culling metrics
            PerformanceMetrics.logChunk(totalChunkCount, culledChunkCount);
        }
        return visibleAbstractBlocks;
    }

    /**
     * Gets block at specified world position
     */
    public AbstractBlock getBlock(Vector3f position) {
        Vector3f chunkPos = calculateChunkCoordinates(position);
        return chunks.stream()
            .filter(c -> c.getPosition().equals(chunkPos))
            .findFirst()
            .map(c -> c.getBlock(position))
            .orElse(null);
    }

    /**
     * Converts world position to chunk coordinates
     */
    private Vector3f calculateChunkCoordinates(Vector3f position) {
        return new Vector3f(
            fastFloor(position.x() / CHUNK_SIZE),
            fastFloor(position.y() / CHUNK_SIZE),
            fastFloor(position.z() / CHUNK_SIZE)
        );
    }

    /**
     * Generates initial terrain around spawn point
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
     * Updates loaded chunks based on player position.
     * Removes chunks outside render distance and generates new chunks as needed.
     *
     * @param playerPos Current player position in world coordinates
     */
    private void updateLoadedChunks(Vector3f playerPos) {
        lastKnownPlayerPos = playerPos;
        Vector3f playerChunkPos = calculateChunkCoordinates(playerPos);

        // Remove out-of-range chunks
        chunks.removeIf(chunk -> isChunkOutOfRange(chunk.getPosition(), playerChunkPos));

        // Load new chunks
        List<Vector3f> newChunks = findMissingChunks(playerChunkPos);
        newChunks.forEach(pos -> EventBus.getInstance().post(new WorldGenerationEvent(pos)));

        updateAffectedChunks(newChunks);
    }

    /**
     * Checks if chunk is beyond render distance
     */
    private boolean isChunkOutOfRange(Vector3f chunkPos, Vector3f playerChunkPos) {
        float dx = Math.abs(chunkPos.x() - playerChunkPos.x());
        float dy = Math.abs(chunkPos.y() - playerChunkPos.y());
        float dz = Math.abs(chunkPos.z() - playerChunkPos.z());
        return dx > GameConfig.RENDER_DISTANCE ||
                dy > GameConfig.RENDER_DISTANCE ||
                dz > GameConfig.RENDER_DISTANCE;
    }

    /**
     * Finds positions requiring new chunk generation
     */
    private List<Vector3f> findMissingChunks(Vector3f playerChunkPos) {
        List<Vector3f> newChunks = new ArrayList<>();

        for (int x = -GameConfig.RENDER_DISTANCE; x <= GameConfig.RENDER_DISTANCE; x++) {
            for (int y = -GameConfig.RENDER_DISTANCE; y <= GameConfig.RENDER_DISTANCE; y++) {
                for (int z = -GameConfig.RENDER_DISTANCE; z <= GameConfig.RENDER_DISTANCE; z++) {
                    Vector3f newPos = new Vector3f(
                        playerChunkPos.x() + x,
                        playerChunkPos.y() + y,
                        playerChunkPos.z() + z
                    );
                    if (chunks.stream().noneMatch(c -> c.getPosition().equals(newPos))) {
                        newChunks.add(newPos);
                    }
                }
            }
        }
        return newChunks;
    }

    /**
     * Updates chunks affected by terrain changes
     */
    private void updateAffectedChunks(List<Vector3f> newChunks) {
        Set<Vector3f> chunksToUpdate = new HashSet<>(newChunks);

        // Add neighboring chunks
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

        // Update block faces
        chunksToUpdate.forEach(pos ->
            chunks.stream()
                .filter(c -> c.getPosition().equals(pos))
                .forEach(this::updateChunkBlockFaces)
        );
    }

    /**
     * Generates terrain for single chunk
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
     * Generates single block during chunk generation
     */
    private void generateBlockAt(Chunk chunk, Vector3f chunkPos, int bx, int by, int bz) {
        float worldX = bx + (chunkPos.x() * CHUNK_SIZE);
        float worldY = by + (chunkPos.y() * CHUNK_SIZE);
        float worldZ = bz + (chunkPos.z() * CHUNK_SIZE);

        // Calculate terrain height
        double noise = terrainNoise.noise(worldX / 32.0, worldZ / 32.0);
        int height = (int)(noise * 32) + 32;

        if (worldY <= height) {
            // Check cave generation
            double caveValue = caveNoise.noise3D(worldX / 16.0, worldY / 16.0, worldZ / 16.0);
            if (caveValue > 0.7 && worldY > 5 && worldY < height - 1) {
                return;
            }

            BlockType type = determineBlockType((int)worldY, height);
            Vector3f blockPos = new Vector3f(worldX, worldY, worldZ);
            chunk.setBlock(BlockFactory.createBlock(type, blockPos));
        }
    }

    /**
     * Updates visibility state of blocks in chunk
     */
    private void updateChunkBlockFaces(Chunk chunk) {
        chunk.getBlocks().forEach(block -> block.updateVisibleFaces(this));
        occlusionCulling.updateOcclusion(chunk, this);
    }

    /**
     * Determines block type based on height and terrain layer
     */
    private BlockType determineBlockType(int y, int height) {
        if (y == 0) return BlockType.BEDROCK;
        if (y == height) return BlockType.GRASS;
        if (y > height - 4) return BlockType.DIRT;
        return BlockType.STONE;
    }

    /**
     * Fast floor implementation for chunk calculations
     */
    private int fastFloor(float value) {
        int i = (int) value;
        return value < i ? i - 1 : i;
    }

    /**
     * Updates blocks at chunk boundaries
     */
    private void updateNeighboringChunks(Vector3f position, Vector3f chunkPos) {
        float localX = position.x() - chunkPos.x() * CHUNK_SIZE;
        float localY = position.y() - chunkPos.y() * CHUNK_SIZE;
        float localZ = position.z() - chunkPos.z() * CHUNK_SIZE;

        if (localX == 0) updateNeighborChunk((int)chunkPos.x() - 1, (int)chunkPos.y(), (int)chunkPos.z());
        if (localX == CHUNK_SIZE - 1) updateNeighborChunk((int)chunkPos.x() + 1, (int)chunkPos.y(), (int)chunkPos.z());
        if (localY == 0) updateNeighborChunk((int)chunkPos.x(), (int)chunkPos.y() - 1, (int)chunkPos.z());
        if (localY == CHUNK_SIZE - 1) updateNeighborChunk((int)chunkPos.x(), (int)chunkPos.y() + 1, (int)chunkPos.z());
        if (localZ == 0) updateNeighborChunk((int)chunkPos.x(), (int)chunkPos.y(), (int)chunkPos.z() - 1);
        if (localZ == CHUNK_SIZE - 1) updateNeighborChunk((int)chunkPos.x(), (int)chunkPos.y(), (int)chunkPos.z() + 1);
    }

    /**
     * Updates single neighboring chunk
     */
    private void updateNeighborChunk(int x, int y, int z) {
        Vector3f neighborPos = new Vector3f(x, y, z);
        chunks.stream()
            .filter(c -> c.getPosition().equals(neighborPos))
            .findFirst()
            .ifPresent(this::updateChunkBlockFaces);
    }

    /**
     * Updates faces of blocks adjacent to modified block
     */
    private void updateAdjacentBlockFaces(Vector3f position) {
        Vector3f[] adjacentPositions = {
            new Vector3f(position.x() + 1, position.y(), position.z()),
            new Vector3f(position.x() - 1, position.y(), position.z()),
            new Vector3f(position.x(), position.y() + 1, position.z()),
            new Vector3f(position.x(), position.y() - 1, position.z()),
            new Vector3f(position.x(), position.y(), position.z() + 1),
            new Vector3f(position.x(), position.y(), position.z() - 1)
        };

        for (Vector3f adjacentPos : adjacentPositions) {
            Optional.ofNullable(getBlock(adjacentPos)).ifPresent(block -> block.updateVisibleFaces(this));
        }
    }

    /**
     * Places new block at specified position
     */
    public void placeBlock(Vector3f position, BlockType type) {
        Vector3f chunkPos = calculateChunkCoordinates(position);
        chunks.stream()
                .filter(c -> c.getPosition().equals(chunkPos))
                .findFirst()
                .ifPresent(chunk -> {
                    AbstractBlock newBlock = BlockFactory.createBlock(type, position);
                    chunk.setBlock(newBlock);
                    updateChunkBlockFaces(chunk);
                    updateNeighboringChunks(position, chunkPos);
                    modifiedBlocks.put(position, type);
                });
        modifiedBlocks.put(new Vector3f(position), type);
    }

    /**
     * Removes block at specified position
     */
    public void destroyBlock(Vector3f position) {
        Vector3f chunkPos = calculateChunkCoordinates(position);
        chunks.stream()
            .filter(c -> c.getPosition().equals(chunkPos))
            .findFirst()
            .ifPresent(chunk -> {
                updateAdjacentBlockFaces(position);
                chunk.removeBlock(position);
                updateChunkBlockFaces(chunk);
                updateNeighboringChunks(position, chunkPos);
            });

        // Mark block as removed in modifications
        modifiedBlocks.put(new Vector3f(position), null);
    }

    /**
     * Returns modified blocks map for world saving
     */
    public Map<Vector3f, BlockType> getModifiedBlocks() {
        return new HashMap<>(modifiedBlocks);
    }

    /**
     * Returns world generation seed
     */
    public long getSeed() {
        return seed;
    }

    /**
     * Cleans up resources used by world systems
     */
    public void cleanup() {
        chunkLoader.shutdown();
    }

    /**
     * Returns day/night cycle controller
     */
    public DayNightCycle getDayNightCycle() {
        return dayNightCycle;
    }

    /**
     * Updates day/night cycle state
     */
    public void updateDayNightCycle(float deltaTime) {
        dayNightCycle.update(deltaTime);
    }

    /**
     * Updates world state for current frame
     * @param playerPos Current player location
     * @param projectionViewMatrix View frustum matrix for culling
     */
    public void update(Vector3f playerPos, Matrix4f projectionViewMatrix) {
        frustum.update(projectionViewMatrix);
        updateLoadedChunks(playerPos);
    }
}