package model;

public class Model {
    private Game game;

    public Model() {
        this.game = new Game();
    }

    public Game getGame() {
        return game;
    }

    public void updateGame() {
        game.update();
    }
}