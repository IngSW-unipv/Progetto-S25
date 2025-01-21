package config;

public class GameConfig {
    public static int RENDER_DISTANCE = 3; // The maximum distance for rendering chunks
    public static float CAMERA_MOVE_SPEED = 10f; // The speed at which the camera moves
    public static float CAMERA_MOUSE_SENSITIVITY = 0.08f; // The sensitivity of the mouse for camera rotation
    public static float CAMERA_MOVEMENT_INCREMENT = 0.05f; // The increment for camera movement
    public static float GRAVITY = -5f; // The force of gravity affecting the player
    public static float JUMP_FORCE = 6.0f; // The force applied when the player jumps
    public static float TERMINAL_VELOCITY = -1f; // The maximum falling speed (terminal velocity)
    public static float RAY_MAX_DISTANCE = 5.0f; // Maximum raycasting distance in blocks
    public static float STEP = 0.05f; // Size of each ray step in world units

    private GameConfig() {} // Prevent instantiation
}
