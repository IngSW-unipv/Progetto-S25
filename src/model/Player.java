package model;

import org.joml.Vector3f;

/**
 * Represents a player in the voxel world. This class manages player interactions,
 * movement, block manipulation, and camera control.
 * The player has the ability to move around, target blocks, break blocks, and place new blocks.
 */
public class Player {

    /** Player's view and position in the world */
    private final Camera camera;
    /** Reference to the game world */
    private final World world;
    /** Currently targeted block (for interaction) */
    private Block targetedBlock;
    /** Progress of breaking the current block (0.0 to 1.0) */
    private float breakingProgress = 0.0f;
    /** Whether the player is currently breaking a block */
    private boolean isBreaking = false;

    /**
     * Creates a new player in the specified world at the given spawn position.
     *
     * @param world The game world the player exists in
     * @param spawnPosition Initial spawn position for the player
     * @param pitch Initial vertical camera rotation
     * @param yaw Initial horizontal camera rotation
     */
    public Player(World world, Vector3f spawnPosition, float pitch, float yaw) {
        this.world = world;
        // Create collision system for player-world interaction
        CollisionSystem collisionSystem = new CollisionSystem(world);
        // Initialize camera with collision detection
        this.camera = new Camera(collisionSystem, spawnPosition);
        this.camera.setPitch(pitch);
        this.camera.setYaw(yaw);
    }

    /**
     * Updates the player's position based on movement input.
     *
     * @param forward Whether moving forward
     * @param back Whether moving backward
     * @param left Whether moving left
     * @param right Whether moving right
     * @param up Whether moving up (jumping)
     * @param down Whether moving down
     * @param deltaTime Time elapsed since last update
     */
    public void move(boolean forward, boolean back, boolean left, boolean right, boolean up, boolean down, float deltaTime) {
        camera.move(forward, back, left, right, up, down, deltaTime);
    }

    /**
     * Rotates the player's view based on mouse movement.
     *
     * @param dx Horizontal mouse movement
     * @param dy Vertical mouse movement
     */
    public void rotate(float dx, float dy) {
        camera.rotate(dx, dy);
    }

    /**
     * Updates which block the player is currently targeting.
     * Uses raycasting to detect blocks in the player's line of sight.
     * Handles highlighting of targeted blocks.
     */
    public void updateTargetedBlock() {
        // Clear previous target if any
        if (targetedBlock != null) {
            targetedBlock.setHighlighted(false);
            targetedBlock = null;
        }

        // Cast ray from player's view to find targeted block
        targetedBlock = RayCaster.getTargetBlock(
            camera.getPosition(),
            camera.getYaw(),
            camera.getPitch(),
            camera.getRoll(),
            world
        );

        // Highlight new target if found
        if (targetedBlock != null) {
            targetedBlock.setHighlighted(true);
        }
    }

    /**
     * Initiates the block breaking process if a breakable block is targeted.
     */
    public void startBreaking() {
        // Can only break if there's a targeted block and it's not unbreakable
        if (targetedBlock == null || targetedBlock.getType().isUnbreakable()) {
            return;
        }
        isBreaking = true;
    }

    /**
     * Stops the block breaking process and resets breaking progress.
     */
    public void stopBreaking() {
        isBreaking = false;
        breakingProgress = 0.0f;
        if (targetedBlock != null) {
            targetedBlock.setBreakProgress(0.0f);
        }
    }

    /**
     * Updates the breaking progress of the currently targeted block.
     * When breaking progress reaches 100%, the block is destroyed.
     *
     * @param deltaTime Time elapsed since last update
     */
    public void updateBreaking(float deltaTime) {
        if (isBreaking && targetedBlock != null && !targetedBlock.getType().isUnbreakable()) {
            // Increment breaking progress based on time
            breakingProgress += deltaTime;
            // Update visual breaking progress
            targetedBlock.setBreakProgress(breakingProgress / targetedBlock.getType().getBreakTime());

            // Check if block should be destroyed
            if (breakingProgress >= targetedBlock.getType().getBreakTime()) {
                world.destroyBlock(targetedBlock.getPosition());
                breakingProgress = 0.0f;
                isBreaking = false;
            }
        }
    }

    /**
     * Places a new block adjacent to the currently targeted block.
     * The block is placed on the face that was clicked.
     * Ensures the new block doesn't intersect with the player.
     */
    public void placeBlock() {
        if (targetedBlock == null) return;

        // Get position of targeted block
        Vector3f pos = targetedBlock.getPosition();
        // Determine which face was clicked
        BlockDirection facing = RayCaster.getTargetFace(
            camera.getPosition(),
            camera.getYaw(),
            camera.getPitch(),
            camera.getRoll(),
            world
        );

        if (facing != null) {
            // Calculate new block position based on clicked face
            Vector3f newPos = new Vector3f(
                pos.x() + facing.getDx(),
                pos.y() + facing.getDy(),
                pos.z() + facing.getDz()
            );

            // Create bounding box for collision check
            BoundingBox newBlockBounds = new BoundingBox(1.0f, 1.0f, 1.0f);
            newBlockBounds.update(newPos);

            // Place block if it won't intersect with player and space is empty
            if (!newBlockBounds.intersects(camera.getBoundingBox()) && world.getBlock(newPos) == null) {
                world.placeBlock(newPos, BlockType.DIRT);
            }
        }
    }

    /**
     * @return The player's camera instance
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * @return The player's current position in the world
     */
    public Vector3f getPosition() {
        return camera.getRawPosition();
    }

    /**
     * @return The player's vertical view angle (pitch)
     */
    public float getPitch() {
        return camera.getPitch();
    }

    /**
     * @return The player's horizontal view angle (yaw)
     */
    public float getYaw() {
        return camera.getYaw();
    }
}