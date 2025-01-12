package config;

public class GameConfig {
    public static int RENDER_DISTANCE = 3;
    public static float CAMERA_MOVE_SPEED = 10f;
    public static float CAMERA_MOUSE_SENSITIVITY = 0.08f;
    public static float CAMERA_MOVEMENT_INCREMENT = 0.05f;
    public static float GRAVITY = -5f;
    public static float JUMP_FORCE = 6.0f;
    public static float TERMINAL_VELOCITY = -1f;

    private GameConfig() {} // Prevent instantiation
}