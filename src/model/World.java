package model;

import config.GameConfig;
import org.joml.Vector3f;
import java.util.*;

/**
 * Represents the game world, consisting of chunks that contain blocks.
 * Handles terrain generation, chunk updates, and block interactions.
 */
public class World {
    public static final int CHUNK_SIZE = 6;             // The size of each chunk in blocks.

    private final Set<Chunk> chunks = new HashSet<>();  // Set of all chunks currently loaded in the world.

    private Vector3f lastKnownPlayerPos;                // The last known position of the player, used for chunk updates.

    private final PerlinNoiseGenerator terrainNoise;    // Perlin noise generator for terrain generation.

    private final PerlinNoiseGenerator caveNoise;       // Perlin noise generator for cave generation.

    private final long seed;                            // Seed used for random generation, ensuring consistent world generation.


    /**
     * Constructs a new world with the specified initial player position and seed.
     *
     * @param initialPosition The initial position of the player in the world.
     * @param seed The seed for random generation.
     */
    public World(Vector3f initialPosition, long seed) {
        this.lastKnownPlayerPos = initialPosition;
        this.seed = seed;

        // Create unique seeds for terrain and cave noise using the main seed.
        Random random = new Random(seed);
        this.terrainNoise = new PerlinNoiseGenerator(random.nextLong());
        this.caveNoise = new PerlinNoiseGenerator(random.nextLong());

        generateSuperFlat();
    }

    /**
     * Constructs a new world with the specified initial position and a random seed.
     *
     * @param initialPosition The initial position of the player in the world.
     */
    public World(Vector3f initialPosition) {
        this(initialPosition, System.currentTimeMillis());
    }

    /**
     * Retrieves all visible blocks in the currently loaded chunks.
     *
     * @return A list of all visible blocks.
     */
    public List<Block> getVisibleBlocks() {
        List<Block> visibleBlocks = new ArrayList<>();
        for (Chunk chunk : chunks) {
            chunk.getBlocks().stream()
                .filter(Block::isVisible)
                .forEach(visibleBlocks::add);
        }
        return visibleBlocks;
    }

    /**
     * Retrieves a block at the specified position.
     *
     * @param position The position of the block.
     * @return The block at the specified position, or null if it does not exist.
     */
    public Block getBlock(Vector3f position) {
        int chunkX = fastFloor(position.x() / (float) CHUNK_SIZE);
        int chunkZ = fastFloor(position.z() / (float) CHUNK_SIZE);

        Optional<Chunk> chunk = chunks.stream()
            .filter(c -> c.getPosition().equals(new ChunkPosition(chunkX, chunkZ)))
            .findFirst();

        return chunk.map(c -> c.getBlock(position)).orElse(null);
    }

    /**
     * Generates a super-flat terrain within the render distance of the player.
     */
    private void generateSuperFlat() {
        int playerChunkX = fastFloor(lastKnownPlayerPos.x / CHUNK_SIZE);
        int playerChunkZ = fastFloor(lastKnownPlayerPos.z / CHUNK_SIZE);

        // Generate all chunks within the render distance.
        for (int x = -GameConfig.RENDER_DISTANCE; x <= GameConfig.RENDER_DISTANCE; x++) {
            for (int z = -GameConfig.RENDER_DISTANCE; z <= GameConfig.RENDER_DISTANCE; z++) {
                ChunkPosition newPos = new ChunkPosition(playerChunkX + x, playerChunkZ + z);
                generateChunkTerrain(newPos);
            }
        }

        // Update block faces for all chunks.
        for (Chunk chunk : chunks) {
            updateChunkBlockFaces(chunk);
        }
    }

    /**
     * Updates the loaded chunks based on the player's current position.
     *
     * @param playerPos The current position of the player.
     */
    private void updateLoadedChunks(Vector3f playerPos) {
        lastKnownPlayerPos = playerPos;
        int playerChunkX = fastFloor(playerPos.x / CHUNK_SIZE);
        int playerChunkZ = fastFloor(playerPos.z / CHUNK_SIZE);

        // Remove chunks outside the render distance.
        chunks.removeIf(chunk -> {
            ChunkPosition pos = chunk.getPosition();
            int dx = Math.abs(pos.x() - playerChunkX);
            int dz = Math.abs(pos.z() - playerChunkZ);
            return dx > GameConfig.RENDER_DISTANCE || dz > GameConfig.RENDER_DISTANCE;
        });

        // Generate new chunks as needed.
        List<ChunkPosition> newChunks = new ArrayList<>();
        for (int x = -GameConfig.RENDER_DISTANCE; x <= GameConfig.RENDER_DISTANCE; x++) {
            for (int z = -GameConfig.RENDER_DISTANCE; z <= GameConfig.RENDER_DISTANCE; z++) {
                ChunkPosition newPos = new ChunkPosition(playerChunkX + x, playerChunkZ + z);
                if (chunks.stream().noneMatch(c -> c.getPosition().equals(newPos))) {
                    newChunks.add(newPos);
                }
            }
        }

        for (ChunkPosition pos : newChunks) {
            generateChunkTerrain(pos);
        }

        // Update block faces for affected chunks.
        Set<ChunkPosition> chunksToUpdate = new HashSet<>(newChunks);
        for (ChunkPosition pos : newChunks) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
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

    /**
     * Generates terrain for a specific chunk position.
     *
     * @param pos The position of the chunk to generate.
     */
    private void generateChunkTerrain(ChunkPosition pos) {
        Chunk chunk = new Chunk(pos);

        for (int bx = 0; bx < CHUNK_SIZE; bx++) {
            for (int bz = 0; bz < CHUNK_SIZE; bz++) {
                int worldX = bx + (pos.x() * CHUNK_SIZE);
                int worldZ = bz + (pos.z() * CHUNK_SIZE);

                double noise = terrainNoise.noise(worldX / 32.0, worldZ / 32.0);
                int height = (int) (noise * 32) + 32;

                for (int y = 0; y <= height; y++) {
                    double caveValue = caveNoise.noise3D(worldX / 16.0, y / 16.0, worldZ / 16.0);
                    if (caveValue > 0.7 && y > 5 && y < height - 1) {
                        continue;
                    }

                    BlockType type = determineBlockType(y, height);
                    Vector3f blockPos = new Vector3f(worldX, y, worldZ);
                    Block block = new Block(type, blockPos);
                    chunk.setBlock(block);
                }
            }
        }

        chunks.add(chunk);
    }

    /**
     * Updates the visible faces of all blocks in a chunk.
     *
     * @param chunk The chunk to update.
     */
    private void updateChunkBlockFaces(Chunk chunk) {
        for (Block block : chunk.getBlocks()) {
            block.updateVisibleFaces(this);
        }
    }

    /**
     * Determines the block type for a specific height.
     *
     * @param y The current height.
     * @param height The maximum height of the terrain at this location.
     * @return The block type for the given height.
     */
    private BlockType determineBlockType(int y, int height) {
        if (y == 0) return BlockType.BEDROCK;
        if (y == height) return BlockType.GRASS;
        if (y > height - 4) return BlockType.DIRT;
        return BlockType.STONE;
    }

    /**
     * Floors a floating-point value quickly.
     *
     * @param value The value to floor.
     * @return The floored integer value.
     */
    private int fastFloor(float value) {
        int i = (int) value;
        return value < i ? i - 1 : i;
    }

    /**
     * Updates the block faces in a neighboring chunk based on its coordinates.
     *
     * @param x The x-coordinate of the neighboring chunk.
     * @param z The z-coordinate of the neighboring chunk.
     */
    private void updateNeighborChunk(int x, int z) {
        chunks.stream()
            .filter(c -> c.getPosition().equals(new ChunkPosition(x, z)))
            .findFirst()
            .ifPresent(this::updateChunkBlockFaces);
    }

    /**
     * Places a block at the specified position and updates neighboring chunks if necessary.
     *
     * @param position The position where the block should be placed.
     * @param type     The type of block to place.
     */
    public void placeBlock(Vector3f position, BlockType type) {
        int chunkX = fastFloor(position.x() / (float) CHUNK_SIZE);
        int chunkZ = fastFloor(position.z() / (float) CHUNK_SIZE);

        chunks.stream()
            .filter(c -> c.getPosition().equals(new ChunkPosition(chunkX, chunkZ)))
            .findFirst()
            .ifPresent(chunk -> {
                Block newBlock = new Block(type, position);
                chunk.setBlock(newBlock);
                updateChunkBlockFaces(chunk);

                // Update neighboring chunks if the block is placed on a chunk border
                float localX = position.x() - chunkX * CHUNK_SIZE;
                float localZ = position.z() - chunkZ * CHUNK_SIZE;

                if (localX == 0) updateNeighborChunk(chunkX - 1, chunkZ);
                if (localX == CHUNK_SIZE - 1) updateNeighborChunk(chunkX + 1, chunkZ);
                if (localZ == 0) updateNeighborChunk(chunkX, chunkZ - 1);
                if (localZ == CHUNK_SIZE - 1) updateNeighborChunk(chunkX, chunkZ + 1);
            });
    }

    /**
     * Updates the visible faces of blocks adjacent to the specified position.
     *
     * @param position The position of the block whose neighbors should be updated.
     */
    private void updateAdjacentBlockFaces(Vector3f position) {
        // Define all six adjacent positions
        Vector3f[] adjacentPositions = {
            new Vector3f(position.x() + 1, position.y(), position.z()), // Right
            new Vector3f(position.x() - 1, position.y(), position.z()), // Left
            new Vector3f(position.x(), position.y() + 1, position.z()), // Top
            new Vector3f(position.x(), position.y() - 1, position.z()), // Bottom
            new Vector3f(position.x(), position.y(), position.z() + 1), // Front
            new Vector3f(position.x(), position.y(), position.z() - 1)  // Back
        };

        // Update the visible faces for each adjacent block
        for (Vector3f adjacentPos : adjacentPositions) {
            Block adjacentBlock = getBlock(adjacentPos);
            if (adjacentBlock != null) {
                adjacentBlock.updateVisibleFaces(this);
            }
        }
    }

    /**
     * Destroys a block at the specified position and updates neighboring chunks if necessary.
     *
     * @param position The position of the block to destroy.
     */
    public void destroyBlock(Vector3f position) {
        int chunkX = fastFloor(position.x() / (float) CHUNK_SIZE);
        int chunkZ = fastFloor(position.z() / (float) CHUNK_SIZE);

        chunks.stream()
            .filter(c -> c.getPosition().equals(new ChunkPosition(chunkX, chunkZ)))
            .findFirst()
            .ifPresent(chunk -> {
                // Update the faces of adjacent blocks before removing the target block
                updateAdjacentBlockFaces(position);

                chunk.removeBlock(position);
                updateChunkBlockFaces(chunk);

                // Update neighboring chunks if the block was on a chunk border
                float localX = position.x() - chunkX * CHUNK_SIZE;
                float localZ = position.z() - chunkZ * CHUNK_SIZE;

                if (localX == 0) updateNeighborChunk(chunkX - 1, chunkZ);
                if (localX == CHUNK_SIZE - 1) updateNeighborChunk(chunkX + 1, chunkZ);
                if (localZ == 0) updateNeighborChunk(chunkX, chunkZ - 1);
                if (localZ == CHUNK_SIZE - 1) updateNeighborChunk(chunkX, chunkZ + 1);
            });
    }

    /**
     * Retrieves the seed used for generating the world.
     *
     * @return The world seed.
     */
    public long getSeed() {
        return seed;
    }

    /**
     * Updates the world based on the player's current position, ensuring that the correct chunks are loaded.
     *
     * @param playerPos The current position of the player.
     */
    public void update(Vector3f playerPos) {
        updateLoadedChunks(playerPos);
    }
}