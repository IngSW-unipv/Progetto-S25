package model.world;

/**
 * Day/night cycle with ambient light.
 * Cycles over a period.
 */
public class DayNightCycle {
    /** Day length in seconds */
    private static final float DAY_LENGTH = 120.0f;

    /** Time of day (0-1) */
    private float timeOfDay = 0.0f;


    /** Updates cycle time */
    public void update(float deltaTime) {
        timeOfDay = (timeOfDay + (deltaTime / DAY_LENGTH)) % 1.0f;
    }

    /** Gets ambient light level */
    public float getAmbientLight() {
        float dayProgress = Math.abs(timeOfDay - 0.5f) * 2;
        return Math.max(0.2f, 1.0f - dayProgress);
    }
}