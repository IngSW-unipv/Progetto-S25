package model;

public class GameState {
    private boolean isRunning = true;

    public GameState() {
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    public boolean getRunning() {
        return isRunning;
    }

    public void update() {
        // Game logic updates
    }
}