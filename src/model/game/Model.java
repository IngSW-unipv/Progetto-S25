package model.game;

import controller.input.PlayerController;
import model.block.BlockModification;
import model.block.BlockType;
import model.physics.PhysicsSystem;
import model.player.Player;
import model.save.WorldManager;
import model.save.WorldSaveData;
import model.world.World;
import model.world.WorldData;
import org.joml.Vector3f;

import java.util.Map;

/**
 * Core game model managing world, player and game state.
 * Handles loading, saving and game loop updates.
 */
public class Model {
    /** Core game components */
    private final GameState gameState;
    private final Player player;
    private final PhysicsSystem physicsSystem;
    private final World world;

    /** Save data fields */
    private final String worldName;
    private final long lastSaveTime;
    private static final long SAVE_INTERVAL = 5 * 60 * 1000; // 5 min


    /**
     * Creates game model with world and save data
     */
    public Model(String worldName, long seed) {
        this.worldName = worldName;
        this.gameState = new GameState();
        this.lastSaveTime = System.currentTimeMillis();

        WorldSaveData savedData = WorldManager.loadWorldData(worldName);
        if (savedData == null) {
            WorldManager.saveWorldMetadata(new WorldData(worldName, seed));
        }

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

        this.world = new World(initialPosition, seed);
        this.physicsSystem = new PhysicsSystem(world);

        // Restore block modifications after world initialization
        if (savedData != null) {
            restoreModifications(savedData);
        }

        this.player = new Player(physicsSystem, initialPosition, initialPitch, initialYaw);
    }

    /**
     * Updates game state including physics and day/night cycle.
     * Handles automatic saving at configured intervals.
     *
     * @param deltaTime Time elapsed since last update in seconds
     */
    public void update(float deltaTime) {
        world.updateDayNightCycle(deltaTime);
        physicsSystem.updatePlayerPhysics(player, deltaTime);
        player.update(deltaTime);

        if (shouldAutoSave()) {
            saveGame();
        }
    }

    /** Save current game state */
    public void saveGame() {
        // Get current block modifications and create save data
        Map<Vector3f, BlockType> modifiedBlocks = world.getModifiedBlocks();
        WorldSaveData saveData = new WorldSaveData(
                modifiedBlocks,
                player.getPosition(),
                player.getPitch(),
                player.getYaw()
        );

        // Save to file
        WorldManager.saveWorldData(worldName, saveData);
    }

    /** Restore saved block modifications */
    private void restoreModifications(WorldSaveData savedData) {
        // Process each modification in order
        for (BlockModification mod : savedData.getModifications()) {
            if (mod.getType() != null) {
                // Place block
                world.placeBlock(mod.getPosition(), mod.getType());
            } else {
                // Remove block
                world.destroyBlock(mod.getPosition());
            }
        }
    }

    /** Timer validation */
    private boolean shouldAutoSave() {
        return System.currentTimeMillis() - lastSaveTime >= SAVE_INTERVAL;
    }

    /** Core game state accessors */
    public GameState getGameState() { return gameState; }
    public World getWorld() { return world; }
    public Player getPlayer() { return player; }

    /** Player state accessors */
    public Vector3f getPlayerPosition() { return player.getPosition(); }
    public float getPlayerPitch() { return player.getPitch(); }
    public float getPlayerYaw() { return player.getYaw(); }
}