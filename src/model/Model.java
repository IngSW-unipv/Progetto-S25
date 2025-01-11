package model;

import controller.event.EventBus;
import controller.event.RenderEvent;
import org.joml.Vector3f;
import java.util.Random;

public class Model {
    private final GameState gameState;
    private final Camera camera;
    private final World world;
    private final CollisionSystem collisionSystem;
    private final long worldSeed;
    private Block highlightedBlock;

    public Model() {
        this.gameState = new GameState();
        Vector3f initialPosition = new Vector3f(0, 50, 0);

        // Generate a random seed for the world
        this.worldSeed = new Random().nextLong();
        this.world = new World(initialPosition, worldSeed);

        this.collisionSystem = new CollisionSystem(world);
        this.camera = new Camera(collisionSystem, initialPosition);
    }

    // Constructor that accepts a specific seed
    public Model(long seed) {
        this.gameState = new GameState();
        Vector3f initialPosition = new Vector3f(0, 50, 0);

        this.worldSeed = seed;
        this.world = new World(initialPosition, worldSeed);

        this.collisionSystem = new CollisionSystem(world);
        this.camera = new Camera(collisionSystem, initialPosition);
    }

    public World getWorld() {
        return world;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Camera getCamera() {
        return camera;
    }

    public long getWorldSeed() {
        return worldSeed;
    }

    public void updateGame() {
        // Reset highlight del blocco precedente
        if (highlightedBlock != null) {
            highlightedBlock.setHighlighted(false);
            highlightedBlock = null;
        }

        // Trova il nuovo blocco puntato
        highlightedBlock = RayCaster.getTargetBlock(
                camera.getPosition(),
                camera.getYaw(),
                camera.getPitch(),
                camera.getRoll(),
                world
        );
        if (highlightedBlock != null) {
            highlightedBlock.setHighlighted(true);
        }

        gameState.update();
        EventBus.getInstance().post(new RenderEvent(camera, world.getVisibleBlocks()));
        world.update(camera.getPosition());
    }
}