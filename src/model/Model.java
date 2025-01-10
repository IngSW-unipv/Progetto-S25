package model;

import controller.event.EventBus;
import controller.event.RenderEvent;

public class Model {
    private final GameState gameState;
    private final Camera camera;
    private final World world;

    public Model() {
        this.gameState = new GameState();
        this.camera = new Camera();
        this.world = new World();
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