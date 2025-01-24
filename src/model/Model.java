package model;

import controller.event.EventBus;
import controller.event.RenderEvent;
import org.joml.Vector3f;
import java.util.Map;

/**
 * Core model class implementing the Model component of MVC pattern.
 * Manages the game state, world, player and coordinates their interactions.
 * Handles saving/loading of game data and updates the game state.
 */
public class Model {
    /** Tracks the running state of the game */
    private final GameState gameState;
    /** Player entity with position, camera and interaction capabilities */
    private final Player player;
    /** The game world containing all blocks and terrain */
    private World world;
    /** Name of the current world for save/load operations */
    private final String worldName;
    /** Timestamp of the last auto-save operation */
    private final long lastSaveTime;
    /** Interval between auto-saves in milliseconds (5 minutes) */
    private static final long SAVE_INTERVAL = 5 * 60 * 1000;

    private final DayNightCycle dayNightCycle;

    /**
     * Creates a new Model instance, initializing or loading a world.
     * Handles loading saved game data or creating new world if no save exists.
     *
     * @param worldName Name of the world to load or create
     * @param seed Seed for world generation if creating new world
     */
    public Model(String worldName, long seed) {
        this.worldName = worldName;
        this.gameState = new GameState();
        this.lastSaveTime = System.currentTimeMillis();
        this.dayNightCycle = new DayNightCycle();

        // Load saved game data if it exists
        WorldSaveData savedData = WorldManager.loadWorldData(worldName);
        Vector3f initialPosition;
        float initialPitch = 0;
        float initialYaw = 0;

        // Set initial player position and rotation from save data or defaults
        if (savedData != null) {
            initialPosition = savedData.getPlayerPosition();
            initialPitch = savedData.getPlayerPitch();
            initialYaw = savedData.getPlayerYaw();
        } else {
            initialPosition = new Vector3f(0, 50, 0);
        }

        // Always create the world first
        this.world = new World(initialPosition, seed);

        // Apply saved modifications if they exist
        if (savedData != null && savedData.getModifications() != null) {
            for (BlockModification mod : savedData.getModifications()) {
                if (mod.getType() != null) {
                    world.placeBlock(mod.getPosition(), mod.getType());
                } else {
                    world.destroyBlock(mod.getPosition());
                }
            }
        }

        // Initialize player with position and rotation
        this.player = new Player(world, initialPosition, initialPitch, initialYaw);
    }

    /**
     * Saves the current game state including player position, rotation,
     * and all modified blocks in the world.
     */
    public void saveGame() {
        Map<Vector3f, BlockType> modifiedBlocks = world.getModifiedBlocks(); // This method doesn't exist
        WorldSaveData saveData = new WorldSaveData(
            modifiedBlocks,
            player.getCamera().getRawPosition(),
            player.getCamera().getPitch(),
            player.getCamera().getYaw()
        );
        WorldManager.saveWorldData(worldName, saveData);
    }

    /**
     * Updates the game state, including player interaction, world state,
     * and triggers auto-save if needed.
     *
     * @param deltaTime Time elapsed since last update in seconds
     */
    public void updateGame(float deltaTime) {
        player.updateTargetedBlock();
        player.updateBreaking(deltaTime);
        gameState.update();
        world.updateDayNightCycle(deltaTime);

        // Aggiornare RenderEvent per includere il world
        EventBus.getInstance().post(new RenderEvent(player.getCamera(), world.getVisibleBlocks(), world));

        // Check for auto-save
        if (shouldAutoSave()) {
            saveGame();
        }
    }

    /**
     * Checks if enough time has passed for an auto-save.
     *
     * @return true if it's time to auto-save, false otherwise
     */
    private boolean shouldAutoSave() {
        return System.currentTimeMillis() - lastSaveTime >= SAVE_INTERVAL;
    }

    /**
     * Initiates block breaking process through the player.
     */
    public void startBreaking() {
        player.startBreaking();
    }

    /**
     * Stops the block breaking process through the player.
     */
    public void stopBreaking() {
        player.stopBreaking();
    }

    /**
     * Places a block through the player and saves the game.
     */
    public void placeBlock() {
        player.placeBlock();
        saveGame();
    }

    /**
     * @return The current game state
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * @return The player instance
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return The player's camera instance
     */
    public Camera getCamera() {
        return player.getCamera();
    }

    /**
     * @return The game world instance
     */
    public World getWorld() {
        return world;
    }
}