package model.game;

import controller.event.BlockEvent;
import controller.event.EventBus;
import controller.event.EventType;
import controller.event.GameEvent;
import model.block.BlockModification;
import model.block.BlockType;
import model.physics.PhysicsSystem;
import model.player.Player;
import model.save.WorldManager;
import model.save.WorldSaveData;
import model.statistics.DatabaseManager;
import model.statistics.GameStatistics;
import model.world.World;
import model.world.WorldData;
import org.joml.Vector3f;
import view.menu.StatisticsDialog;

import java.awt.*;
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
    private final GameStatistics statistics;

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
        this.statistics = new DatabaseManager();

        // Check and save metadata FIRST
        WorldSaveData savedData = WorldManager.loadWorldData(worldName);
        if (savedData == null) {
            // Attempt to save metadata before world creation
            WorldManager.saveWorldMetadata(new WorldData(worldName, seed));
        }

        // Setup initial position
        Vector3f initialPosition;
        float initialPitch = 0;
        float initialYaw = 0;

        if (savedData != null) {
            initialPosition = savedData.getPlayerPosition();
            initialPosition.y += 2;
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

        EventBus.getInstance().subscribe(EventType.BLOCK_MODIFICATION, this::onBlockEvent);
    }

    /** Save block modifications for database records */
    private void onBlockEvent(GameEvent event) {
        if (event instanceof BlockEvent blockEvent) {
            if (blockEvent.isPlacement()) {
                statistics.recordBlockPlaced(blockEvent.type().toString());
            } else {
                statistics.recordBlockDestroyed(blockEvent.type().toString());
            }
        }
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
        statistics.updatePlayTime(deltaTime);

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
        statistics.saveToDatabase(worldName);
    }

    /** Timer validation */
    private boolean shouldAutoSave() {
        return System.currentTimeMillis() - lastSaveTime >= SAVE_INTERVAL;
    }

    /** Core game state accessors */
    public GameState getGameState() { return gameState; }
    public World getWorld() { return world; }
    public Player getPlayer() { return player; }
    public GameStatistics getStatistics() { return statistics; }

    /** Player state accessors */
    public Vector3f getPlayerPosition() { return player.getPosition(); }
    public float getPlayerPitch() { return player.getPitch(); }
    public float getPlayerYaw() { return player.getYaw(); }
}