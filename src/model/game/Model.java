//model

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

public class Model {
    private final GameState gameState;
    private final Player player;
    private final PhysicsSystem physicsSystem;
    private final World world;
    private final String worldName;
    private final long lastSaveTime;
    private static final long SAVE_INTERVAL = 5 * 60 * 1000;

    public Model(String worldName, long seed) {
        this.worldName = worldName;
        this.gameState = new GameState();
        this.lastSaveTime = System.currentTimeMillis();

        WorldSaveData savedData = WorldManager.loadWorldData(worldName);
        if (savedData == null) {
            // È un nuovo mondo
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

        //System.out.println(this.world.findSpawnHeight());

        if (savedData != null && savedData.getModifications() != null) {
            for (BlockModification mod : savedData.getModifications()) {
                if (mod.getType() != null) {
                    world.placeBlock(mod.getPosition(), mod.getType());
                } else {
                    world.destroyBlock(mod.getPosition());
                }
            }
        }

        this.player = new Player(physicsSystem, initialPosition, initialPitch, initialYaw);
        new PlayerController(player, world);
    }

    public void update(float deltaTime) {

        world.updateDayNightCycle(deltaTime);

        physicsSystem.updatePlayerPhysics(player, deltaTime);
        player.update(deltaTime);

        if (!gameState.isPaused()) {

        }

        if (shouldAutoSave()) {
            saveGame();
        }
    }

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

    private boolean shouldAutoSave() {
        return System.currentTimeMillis() - lastSaveTime >= SAVE_INTERVAL;
    }

    public GameState getGameState() { return gameState; }
    public Player getPlayer() { return player; }
    public World getWorld() { return world; }
    public Vector3f getPlayerPosition() { return player.getPosition(); }
    public float getPlayerPitch() { return player.getPitch(); }
    public float getPlayerYaw() { return player.getYaw(); }
}