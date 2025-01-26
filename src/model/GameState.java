//gamestate

package model;

/**
 * Represents the state of the game, managing whether the game is running
 * and providing a method for updating game logic.
 */
public class GameState {
    private boolean isRunning = true; // Indicates whether the game is currently running
    private boolean isPaused = false;

    /**
     * Constructs a new GameState instance with the default state of running.
     */
    public GameState() {
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Sets the running state of the game.
     *
     * @param running A boolean indicating whether the game should be running.
     */
    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    /**
     * Retrieves the current running state of the game.
     *
     * @return {@code true} if the game is running, {@code false} otherwise.
     */
    public boolean getRunning() {
        return isRunning;
    }

    /**
     * Updates the game state. This method is intended for game logic updates
     * and can be extended to include additional functionality as needed.
     */
    public void update() {
        // Placeholder for game logic updates
    }
}
