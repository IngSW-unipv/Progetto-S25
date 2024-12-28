package model;

public class Model {
    private Game game;
    private Camera camera;
    private World world;

    public Model() {
        this.game = new Game();
        this.camera = new Camera();
        this.world = new World();
    }

    public World getWorld() {
        return world;
    }

    public Game getGame() {
        return game;
    }

    public Camera getCamera() {
        return camera;
    }

    public void updateGame() {
        game.update();
        world.generateChunksAroundCamera(camera.getPosition());
    }
}