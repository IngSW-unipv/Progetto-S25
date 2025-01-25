package model;

import config.GameConfig;
import org.joml.Vector3f;

/**
 * Represents a player in the voxel world.
 * Manages movement, physics, collision detection, block targeting and interaction.
 * Handles collision detection at both ground level and eye level for all interactions.
 */
public class Player {
    /** Player's view and position */
    private final Camera camera;

    /** Game world reference */
    private final World world;

    /** Player's collision bounds */
    private final BoundingBox boundingBox;

    /** For detecting collisions with world */
    private final CollisionSystem collisionSystem;

    /** Currently targeted block for interaction */
    private Block targetedBlock;

    /** Progress of breaking current block (0-1) */
    private float breakingProgress = 0.0f;

    /** Whether currently breaking a block */
    private boolean isBreaking = false;

    /** Vertical movement velocity in units/sec */
    private float verticalVelocity = 0.0f;

    /** Whether player is on solid ground */
    private boolean isGrounded = false;

    private float fallSpeed = 0.0f;  // Velocità attuale di caduta

    private static final float COLLISION_MARGIN = 0.05f;

    /** Player dimensions */
    private static final float PLAYER_HEIGHT = 1.8f;
    private static final float PLAYER_WIDTH = 0.6f;
    private static final float EYE_HEIGHT = 1.6f;

    /** Movement constants from config */
    private static final float CAMERA_MOVEMENT_INCREMENT = GameConfig.CAMERA_MOVEMENT_INCREMENT;
    private static final float CAMERA_MOVE_SPEED = GameConfig.CAMERA_MOVE_SPEED;
    private static final float JUMP_FORCE = GameConfig.JUMP_FORCE;
    private static final float TERMINAL_VELOCITY = GameConfig.TERMINAL_VELOCITY;
    private static final float GRAVITY = GameConfig.GRAVITY;

    /** Physics constants */
    private static final float INITIAL_JUMP_VELOCITY = 20.0f;
    private static final float MAX_FALL_SPEED = -20.0f;
    private static final float FALL_ACCELERATION = 20.0f;

    /**
     * Creates a new player in the specified world.
     *
     * @param world Game world instance
     * @param spawnPosition Initial spawn coordinates
     * @param pitch Starting vertical camera angle
     * @param yaw Starting horizontal camera angle
     */
    public Player(World world, Vector3f spawnPosition, float pitch, float yaw) {
        this.world = world;
        this.collisionSystem = new CollisionSystem(world);
        this.camera = new Camera(spawnPosition);
        this.camera.setPitch(pitch);
        this.camera.setYaw(yaw);
        this.boundingBox = new BoundingBox(PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_WIDTH);
        this.boundingBox.update(spawnPosition);
    }

    /**
     * Tests if a movement would cause collision at both ground and eye level.
     *
     * @param newPosition Position to test
     * @param direction HORIZONTAL or VERTICAL movement
     * @return true if movement is valid (no collision at either height)
     */
    private boolean canMoveToPosition(Vector3f newPosition, Direction direction) {
        Vector3f groundCheckOffset = new Vector3f(0, -0.1f, 0);

        if (direction == Direction.HORIZONTAL) {
            // Check posizione attuale
            boundingBox.update(newPosition);
            if (collisionSystem.checkCollision(boundingBox)) {
                return false;
            }

            // Check se c'è terreno sotto
            Vector3f groundPosition = new Vector3f(newPosition).add(groundCheckOffset);
            boundingBox.update(groundPosition);
            if (!collisionSystem.checkCollision(boundingBox)) {
                isGrounded = false;
            }

            return true;
        } else {
            boundingBox.update(newPosition);
            return !collisionSystem.checkCollision(boundingBox);
        }
    }

    /**
     * Updates player position based on input and physics.
     * Handles movement, jumping, gravity and collisions at both ground and eye level.
     *
     * @param forward Forward movement
     * @param back Backward movement
     * @param left Strafe left
     * @param right Strafe right
     * @param up Jump
     * @param down Unused
     * @param deltaTime Seconds since last update
     */
    public void move(boolean forward, boolean back, boolean left, boolean right, boolean up, boolean down, float deltaTime) {
        // Movimento orizzontale
        float dx = 0, dz = 0;
        if (forward) dz -= CAMERA_MOVEMENT_INCREMENT;
        if (back) dz += CAMERA_MOVEMENT_INCREMENT;
        if (left) dx -= CAMERA_MOVEMENT_INCREMENT;
        if (right) dx += CAMERA_MOVEMENT_INCREMENT;

        // Normalizzazione diagonale
        if (dx != 0 && dz != 0) {
            dx *= 0.707f;
            dz *= 0.707f;
        }

        // Gestione gravità e salto
        if (up && isGrounded) {
            verticalVelocity = JUMP_FORCE;
            isGrounded = false;
        }

        if (!isGrounded) {
            verticalVelocity += GRAVITY * deltaTime;
            if (verticalVelocity < TERMINAL_VELOCITY) {
                verticalVelocity = TERMINAL_VELOCITY;
            }
        }

        // Movimento nel world space
        float angle = (float) Math.toRadians(camera.getYaw());
        Vector3f currentPos = camera.getRawPosition();
        Vector3f newPosition = new Vector3f(currentPos);

        // Movimento orizzontale con collisioni
        Vector3f horizontalMove = new Vector3f(
                (float)(dx * Math.cos(angle) - dz * Math.sin(angle)) * CAMERA_MOVE_SPEED * deltaTime,
                0,
                (float)(dx * Math.sin(angle) + dz * Math.cos(angle)) * CAMERA_MOVE_SPEED * deltaTime
        );

        // Test movimento completo
        newPosition.x = currentPos.x + horizontalMove.x;
        newPosition.z = currentPos.z + horizontalMove.z;

        boolean moved = false;

        // Prova movimento completo
        if (canMoveToPosition(newPosition, Direction.HORIZONTAL)) {
            camera.setPosition(newPosition);
            moved = true;
        }

        // Se bloccato, prova movimento singoli assi
        if (!moved) {
            // Prova X
            newPosition.set(currentPos);
            newPosition.x += horizontalMove.x;
            if (canMoveToPosition(newPosition, Direction.HORIZONTAL)) {
                camera.setPosition(newPosition);
                moved = true;
            }

            // Prova Z
            if (!moved) {
                newPosition.set(currentPos);
                newPosition.z += horizontalMove.z;
                if (canMoveToPosition(newPosition, Direction.HORIZONTAL)) {
                    camera.setPosition(newPosition);
                }
            }
        }

        // Movimento verticale con collisioni
        newPosition = new Vector3f(camera.getRawPosition());
        newPosition.y += verticalVelocity * deltaTime;

        if (canMoveToPosition(newPosition, Direction.VERTICAL)) {
            camera.setPosition(newPosition);
            isGrounded = false;
        } else {
            // Se c'è collisione verso il basso
            if (verticalVelocity < 0) {
                isGrounded = true;
            }
            // Se c'è collisione verso l'alto
            if (verticalVelocity > 0) {
                verticalVelocity = 0;
            }
        }

        // Forza la velocità verticale a 0 quando grounded
        if (isGrounded) {
            verticalVelocity = 0;
        }

        // Aggiorna bounding box
        boundingBox.update(camera.getRawPosition());
    }

    /**
     * Places a new block adjacent to the currently targeted block.
     * Ensures the new block doesn't intersect with player at any height.
     */
    public void placeBlock() {
        if (targetedBlock == null) return;

        Vector3f pos = targetedBlock.getPosition();
        BlockDirection facing = RayCaster.getTargetFace(
            camera.getPosition(),
            camera.getYaw(),
            camera.getPitch(),
            camera.getRoll(),
            world
        );

        if (facing != null) {
            Vector3f newPos = new Vector3f(
                pos.x() + facing.getDx(),
                pos.y() + facing.getDy(),
                pos.z() + facing.getDz()
            );

            // Check collisions at ground and eye levels
            BoundingBox blockBounds = new BoundingBox(1.0f, 1.0f, 1.0f);
            blockBounds.update(newPos);

            Vector3f groundPos = new Vector3f(camera.getRawPosition());
            Vector3f eyePos = new Vector3f(camera.getPosition());

            boundingBox.update(groundPos);
            if (boundingBox.intersects(blockBounds)) return;

            boundingBox.update(eyePos);
            if (boundingBox.intersects(blockBounds)) return;

            // Place block only if no collisions detected
            if (world.getBlock(newPos) == null) {
                world.placeBlock(newPos, BlockType.DIRT);
            }
        }
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
     */
    public void updateTargetedBlock() {
        Block previousTarget = targetedBlock;

        // Clear highlight from old target
        if (targetedBlock != null) {
            targetedBlock.setHighlighted(false);
        }

        // Find new target
        targetedBlock = RayCaster.getTargetBlock(
                camera.getPosition(),
                camera.getYaw(),
                camera.getPitch(),
                camera.getRoll(),
                world
        );

        // If target changed, reset breaking state
        if (previousTarget != null && previousTarget != targetedBlock) {
            previousTarget.setBreakProgress(0.0f); // Clear breaking effect from old target
            if (isBreaking) {
                stopBreaking(); // Reset breaking state for new target
            }
        }

        // Highlight new target if any
        if (targetedBlock != null) {
            targetedBlock.setHighlighted(true);
        }
    }

    /**
     * Initiates block breaking if target is breakable.
     */
    public void startBreaking() {
        if (targetedBlock != null && !targetedBlock.getType().isUnbreakable()) {
            isBreaking = true;
        }
    }

    /**
     * Stops block breaking and resets progress.
     */
    public void stopBreaking() {
        isBreaking = false;
        breakingProgress = 0.0f;
        if (targetedBlock != null) {
            targetedBlock.setBreakProgress(0.0f);
        }
    }

    /**
     * Updates block breaking progress and destroys block when complete.
     *
     * @param deltaTime Time elapsed since last update
     */
    public void updateBreaking(float deltaTime) {
        // If we're breaking but have no target or it's unbreakable, stop breaking
        if (isBreaking && (targetedBlock == null || targetedBlock.getType().isUnbreakable())) {
            stopBreaking();
            return;
        }

        if (isBreaking && targetedBlock != null) {
            breakingProgress += deltaTime;
            targetedBlock.setBreakProgress(breakingProgress / targetedBlock.getType().getBreakTime());

            if (breakingProgress >= targetedBlock.getType().getBreakTime()) {
                world.destroyBlock(targetedBlock.getPosition());
                stopBreaking();
            }
        }
    }

    public Camera getCamera() {
        return camera;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public Vector3f getPosition() {
        return camera.getRawPosition();
    }
}