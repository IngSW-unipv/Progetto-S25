package model.game;

/**
 * Core game state tracking.
 * Manages pause and running states.
 */
public class GameState {
    /** Game state flags */
    private boolean isRunning = true;
    private boolean isPaused = false;


    /** Creates default running state */
    public GameState() {}

    /** Sets pause state */
    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    /** Gets pause state */
    public boolean isPaused() {
        return isPaused;
    }

    /** Sets running state */
    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    /** Gets running state */
    public boolean isRunning() {
        return isRunning;
    }
}