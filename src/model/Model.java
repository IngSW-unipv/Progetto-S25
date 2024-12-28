package model;

public class Model {
    private Game game;
    private Camera camera;

    public Model() {
        this.game = new Game();
        this.camera = new Camera();
    }

    public Game getGame() {
        return game;
    }

    public Camera getCamera() {
        return camera;
    }

    public void updateGame() {
        game.update();
    }
}