package model;

import controller.event.*;
import org.joml.Vector3f;
import java.util.Map;

public class Model implements EventListener {
    private final GameState gameState;
    private final Player player;
    private World world;
    private final String worldName;
    private final long lastSaveTime;
    private static final long SAVE_INTERVAL = 5 * 60 * 1000;
    private final DayNightCycle dayNightCycle;

    public Model(String worldName, long seed) {
        this.worldName = worldName;
        this.gameState = new GameState();
        this.lastSaveTime = System.currentTimeMillis();
        this.dayNightCycle = new DayNightCycle();

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

        this.world = new World(initialPosition, seed);

        if (savedData != null && savedData.getModifications() != null) {
            for (BlockModification mod : savedData.getModifications()) {
                if (mod.getType() != null) {
                    world.placeBlock(mod.getPosition(), mod.getType());
                } else {
                    world.destroyBlock(mod.getPosition());
                }
            }
        }

        this.player = new Player(world, initialPosition, initialPitch, initialYaw);
        EventBus.getInstance().subscribe(EventType.INPUT, this);
    }

    @Override
    public void onEvent(GameEvent event) {
        if (event instanceof InputEvent inputEvent) {
            handleInput(inputEvent);
        }
    }

    private void handleInput(InputEvent event) {
        switch (event.action()) {
            case MOVE_FORWARD, MOVE_BACKWARD, MOVE_LEFT, MOVE_RIGHT, MOVE_UP, MOVE_DOWN, LOOK_X, LOOK_Y ->
                    player.handleMovement(event.action(), event.value());
            case PLACE_BLOCK -> {
                if (event.value() > 0) player.placeBlock();
            }
            case DESTROY_BLOCK -> {
                if (event.value() > 0) player.startBreaking();
                else player.stopBreaking();
            }
            case EXIT -> {
                if (event.value() > 0) {
                    saveGame();
                    world.cleanup();
                    gameState.setRunning(false);
                }
            }
        }
    }

    public void update(float deltaTime) {
        player.update(deltaTime);
        gameState.update();
        world.updateDayNightCycle(deltaTime);

        if (shouldAutoSave()) {
            saveGame();
        }
    }

    public void saveGame() {
        Map<Vector3f, BlockType> modifiedBlocks = world.getModifiedBlocks();
        WorldSaveData saveData = new WorldSaveData(
                modifiedBlocks,
                player.getCamera().getRawPosition(),
                player.getCamera().getPitch(),
                player.getCamera().getYaw()
        );
        WorldManager.saveWorldData(worldName, saveData);
    }

    private boolean shouldAutoSave() {
        return System.currentTimeMillis() - lastSaveTime >= SAVE_INTERVAL;
    }

    public GameState getGameState() { return gameState; }
    public Player getPlayer() { return player; }
    public Camera getCamera() { return player.getCamera(); }
    public World getWorld() { return world; }
}