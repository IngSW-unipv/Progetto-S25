package model;

public class Game {
    private Cube cube;

    public Game() {
        this.cube = new Cube(BlockType.DIRT);
    }

    public void update() {
        // Game logic updates
    }

    public Cube getCube() {
        return cube;
    }
}