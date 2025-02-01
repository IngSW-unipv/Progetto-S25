package model.game;

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

        // Load or create world
        WorldSaveData savedData = WorldManager.loadWorldData(worldName);
        if (savedData == null) {
            WorldManager.saveWorldMetadata(new WorldData(worldName, seed));
        }

        // Setup initial position
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

        // Initialize world and physics
        this.world = new World(initialPosition, seed);
        this.physicsSystem = new PhysicsSystem(world);

        // Restore block modifications
        if (savedData != null) {
            restoreModifications(savedData);
        }

        // Create player
        this.player = new Player(physicsSystem, initialPosition, initialPitch, initialYaw);
    }

    /** Restore saved block modifications */
    private void restoreModifications(WorldSaveData savedData) {
        for (BlockModification mod : savedData.getModifications()) {
            if (mod.getType() != null) {
                world.placeBlock(mod.getPosition(), mod.getType());
            } else {
                world.destroyBlock(mod.getPosition());
            }
        }
    }

    /** Update game state */
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
        Map<Vector3f, BlockType> modifiedBlocks = world.getModifiedBlocks();
        WorldSaveData saveData = new WorldSaveData(
                modifiedBlocks,
                player.getPosition(),
                player.getPitch(),
                player.getYaw()
        );
        WorldManager.saveWorldData(worldName, saveData);
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