package model;

public class Model {
    private GameState gameState;
    private Camera camera;
    private World world;

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
    }
}