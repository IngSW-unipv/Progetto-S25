package model;

import controller.event.EventBus;
import controller.event.RenderEvent;
import org.joml.Vector3f;
import java.util.HashMap;
import java.util.Map;

/**
 * The Model class represents the core game model, managing the game's state,
 * camera, world, collision system, and block interactions.
 */
public class Model {
    private final GameState gameState;              // Tracks the running state of the game
    private final Camera camera;                    // Manages the player's view and movement
    private final World world;                      // Represents the game world
    private final CollisionSystem collisionSystem;  // Handles collision detection
    private final long worldSeed;                   // Seed for world generation
    private Block highlightedBlock;                 // Currently highlighted block
    private float breakingProgress = 0.0f;          // Progress of breaking a block
    private boolean isBreaking = false;             // Whether a block is being broken
    private final String worldName;                 // Name of the current world
    private long lastSaveTime;                      // Timestamp of last auto-save
    private static final long SAVE_INTERVAL = 5 * 60 * 1000; // Auto-save every 5 minutes

    /**
     * Constructor initializes the game with a specific world name and seed.
     * Loads saved data if it exists, otherwise starts a new world.
     *
     * @param worldName The name of the world
     * @param seed The seed for world generation
     */
    public Model(String worldName, long seed) {
        this.worldName = worldName;
        this.worldSeed = seed;
        this.gameState = new GameState();
        this.lastSaveTime = System.currentTimeMillis();

        // Load saved data if it exists
        WorldSaveData savedData = WorldManager.loadWorldData(worldName);
        Vector3f initialPosition;
        float initialPitch = 0;
        float initialYaw = 0;

        if (savedData != null) {
            initialPosition = savedData.getPlayerPosition();
            initialPitch = savedData.getPlayerPitch();
            initialYaw = savedData.getPlayerYaw();
        } else {
            initialPosition = new Vector3f(0, 50, 0);
        }

        // Load modified blocks from save data
        if (savedData != null && savedData.getModifications() != null) {
            initialPosition = savedData.getPlayerPosition();
            initialPitch = savedData.getPlayerPitch();
            initialYaw = savedData.getPlayerYaw();

            // Load world first
            this.world = new World(initialPosition, worldSeed);

            // Apply modifications
        } else {
            initialPosition = new Vector3f(0, 50, 0);
            this.world = new World(initialPosition, worldSeed);
        }

        this.collisionSystem = new CollisionSystem(world);
        this.camera = new Camera(collisionSystem, initialPosition);

        if (savedData != null) {
            camera.setPitch(initialPitch);
            camera.setYaw(initialYaw);
        }
    }

    /**
     * Saves the current game state to disk.
     */
    public void saveGame() {
        Map<Vector3f, BlockType> modifiedBlocks = world.getModifiedBlocks();
        WorldSaveData saveData = new WorldSaveData(
                modifiedBlocks,
                camera.getRawPosition(),
                camera.getPitch(),
                camera.getYaw()
        );
        WorldManager.saveWorldData(worldName, saveData);
        lastSaveTime = System.currentTimeMillis();
    }

    /**
     * Updates the game logic, including block highlighting, breaking, and world updates.
     *
     * @param deltaTime The time elapsed since the last update
     */
    public void updateGame(float deltaTime) {
        // Reset highlighting for the previously highlighted block
        if (highlightedBlock != null) {
            highlightedBlock.setHighlighted(false);
            highlightedBlock = null;
        }

        // Determine the currently highlighted block
        highlightedBlock = RayCaster.getTargetBlock(
                camera.getPosition(),
                camera.getYaw(),
                camera.getPitch(),
                camera.getRoll(),
                world
        );

        // Update highlighting and breaking logic
        if (highlightedBlock != null) {
            highlightedBlock.setHighlighted(true);
            if (isBreaking && !highlightedBlock.getType().isUnbreakable()) {
                breakingProgress += deltaTime;
                highlightedBlock.setBreakProgress(breakingProgress / highlightedBlock.getType().getBreakTime());
                if (breakingProgress >= highlightedBlock.getType().getBreakTime()) {
                    world.destroyBlock(highlightedBlock.getPosition());
                    breakingProgress = 0.0f;
                    isBreaking = false;
                }
            }
        } else {
            breakingProgress = 0.0f;
            isBreaking = false;
        }

        // Update game state and notify the event bus
        gameState.update();
        EventBus.getInstance().post(new RenderEvent(camera, world.getVisibleBlocks()));
        world.update(camera.getPosition());

        // Check for auto-save
        if (shouldAutoSave()) {
            saveGame();
        }
    }

    /**
     * Checks if it's time for an auto-save.
     *
     * @return true if enough time has passed since the last save
     */
    private boolean shouldAutoSave() {
        return System.currentTimeMillis() - lastSaveTime >= SAVE_INTERVAL;
    }

    /**
     * Initiates block breaking if possible.
     */
    public void startBreaking() {
        if (highlightedBlock == null || highlightedBlock.getType() == BlockType.BEDROCK) {
            return;
        }
        isBreaking = true;

        if (breakingProgress >= highlightedBlock.getType().getBreakTime()) {
            world.destroyBlock(highlightedBlock.getPosition());
            breakingProgress = 0.0f;
            isBreaking = false;
            saveGame(); // Save after block destruction
        }
    }

    /**
     * Stops the breaking process and resets progress.
     */
    public void stopBreaking() {
        isBreaking = false;
        breakingProgress = 0.0f;
        if (highlightedBlock != null) {
            highlightedBlock.setBreakProgress(0.0f);
        }
    }

    /**
     * Attempts to place a block adjacent to the highlighted block.
     */
    public void placeBlock() {
        if (highlightedBlock != null) {
            Vector3f pos = highlightedBlock.getPosition();
            BlockDirection facing = RayCaster.getTargetFace(
                    camera.getPosition(),
                    camera.getYaw(),
                    camera.getPitch(),
                    camera.getRoll(),
                    world
            );

            if (facing != null) {
                Vector3f newPos = new Vector3f(
                        pos.x() + facing.getDx(),
                        pos.y() + facing.getDy(),
                        pos.z() + facing.getDz()
                );

                // Check for collisions and existing blocks
                BoundingBox newBlockBounds = new BoundingBox(1.0f, 1.0f, 1.0f);
                newBlockBounds.update(newPos);

                BoundingBox playerBounds = camera.getBoundingBox();
                boolean intersects = newBlockBounds.intersects(playerBounds);
                boolean existingBlock = world.getBlock(newPos) != null;

                // Place the block if valid
                if (!intersects && !existingBlock) {
                    world.placeBlock(newPos, BlockType.DIRT);
                    saveGame(); // Save after block placement
                }
            }
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public Camera getCamera() {
        return camera;
    }

    public World getWorld() {
        return world;
    }
}