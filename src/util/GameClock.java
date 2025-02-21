package util;

/**
 * Central time manager for game systems.
 * Tracks frame time and distributes to subsystems.
 */
public class GameClock {
    /** Singleton instance */
    private static GameClock instance;

    /** Current frame's delta time in seconds */
    private float deltaTime = 0.0f;

    /** Total elapsed game time in seconds */
    private float totalTime = 0.0f;

    /** Private constructor for singleton */
    private GameClock() {}

    /** Get singleton instance */
    public static GameClock getInstance() {
        if (instance == null) {
            instance = new GameClock();
        }
        return instance;
    }

    /** Update clock with new delta time */
    public void update(float deltaTime) {
        this.deltaTime = deltaTime;
        this.totalTime += deltaTime;
    }

    /** Get delta time between frames */
    public float getDeltaTime() {
        return deltaTime;
    }

    /** Get total elapsed time */
    public float getTotalTime() {
        return totalTime;
    }
}