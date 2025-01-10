package model;

import controller.event.EventBus;
import controller.event.RenderEvent;
import org.joml.Vector3f;

public class Model {
    private final GameState gameState;
    private final Camera camera;
    private final World world;
    private final CollisionSystem collisionSystem;

    public Model() {
        this.gameState = new GameState();
        Vector3f initialPosition = new Vector3f(0, 5, 0);
        this.world = new World(initialPosition);
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

    public void updateGame() {
        gameState.update();
        EventBus.getInstance().post(new RenderEvent(camera, world.getVisibleBlocks()));
        world.update(camera.getPosition());
    }
}