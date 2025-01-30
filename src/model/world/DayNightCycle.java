package model.world;

public class DayNightCycle {
    private static final float DAY_LENGTH = 120.0f; // 120 seconds cycle
    private float timeOfDay = 0.0f; // 0 to 1

    public void update(float deltaTime) {
        timeOfDay = (timeOfDay + (deltaTime / DAY_LENGTH)) % 1.0f;
    }

    public float getAmbientLight() {
        float dayProgress = Math.abs(timeOfDay - 0.5f) * 2;
        return Math.max(0.2f, 1.0f - dayProgress);
    }
}