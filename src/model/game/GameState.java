package model.game;

/**
 * Core game state tracking.
 * Manages pause and running states.
 */
public class GameState {
    /** Game state flags */
    private boolean isRunning = true;


    /** Creates default running state */
    public GameState() {}

    /** Sets running state */
    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    /** Gets running state */
    public boolean isRunning() {
        return isRunning;
    }
}