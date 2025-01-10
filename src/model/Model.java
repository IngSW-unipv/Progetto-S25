package model;

import controller.event.EventBus;
import controller.event.RenderEvent;

public class Model {
    private final GameState gameState;
    private final Camera camera;
    private final World world;
    private final CollisionSystem collisionSystem;

    public Model() {
        this.world = new World();
        this.gameState = new GameState();
        this.collisionSystem = new CollisionSystem(world);
        this.camera = new Camera(collisionSystem);
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
    }
}