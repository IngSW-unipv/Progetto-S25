package model;

import view.renderer.WorldObserver;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private GameState gameState;
    private Camera camera;
    private World world;

    private List<WorldObserver> observers = new ArrayList<>();

    public void addObserver(WorldObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        List<Block> visibleBlocks = world.getVisibleBlocks();
        for(WorldObserver observer : observers) {
            observer.onWorldUpdate(visibleBlocks);
        }
    }


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
        notifyObservers();
    }
}