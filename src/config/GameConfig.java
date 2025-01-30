package config;

/**
 * This class holds the configuration settings for the game.
 * It includes settings for rendering, camera movement, physics, and raycasting.
 */
public class GameConfig {
    /**
     * The maximum distance at which the game renders blocks.
     */
    public static int RENDER_DISTANCE = 3;

    /**
     * The height of the player's eyes from the ground.
     */
    public static float EYE_HEIGHT = 1.6f;

    /**
     * The speed at which the camera moves.
     */
    public static float CAMERA_MOVE_SPEED = 5.0f;

    /**
     * The sensitivity of the camera to mouse movements.
     */
    public static float CAMERA_MOUSE_SENSITIVITY = 0.08f;

    /**
     * The increment by which the camera movement speed is adjusted.
     */
    public static float CAMERA_MOVEMENT_INCREMENT = 0.05f;

    /**
     * The gravitational force applied to the player.
     */
    public static float GRAVITY = -20.0f;

    /**
     * The force applied to the player when jumping.
     */
    public static float JUMP_FORCE = 100.0f;

    /**
     * The maximum velocity a player can reach due to gravity.
     */
    public static float TERMINAL_VELOCITY = -40.0f;

    /**
     * The base speed at which the player moves.
     */
    public static float MOVE_SPEED = 2.0f;

    /**
     * The acceleration due to gravity.
     */
    public static float GRAVITY_ACCELERATION = -1.5f;

    /**
     * The maximum distance a ray can travel in the world.
     */
    public static float RAY_MAX_DISTANCE = 5.0f;

    /**
     * The step size used in raycasting to check for block intersections.
     */
    public static float STEP = 0.05f;


    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private GameConfig() {}
}