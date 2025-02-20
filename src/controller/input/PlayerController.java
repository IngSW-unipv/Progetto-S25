package controller.input;

import controller.event.*;
import model.block.AbstractBlock;
import model.block.BlockDirection;
import model.block.BlockType;
import model.physics.BoundingBox;
import model.player.Player;
import model.player.RayCaster;
import model.world.World;
import org.joml.Vector3f;

/**
 * Controls player movement, camera orientation and block interactions.
 * Translates input events into player actions and state updates.
 */
public class PlayerController {
    /** Reference to player entity */
    private final Player player;

    /** Reference to game world */
    private final World world;

    /** Movement state flags */
    private boolean movingForward;
    private boolean movingBackward;
    private boolean movingLeft;
    private boolean movingRight;
    private boolean jumping;

    /** Block breaking state */
    public float breakingProgress = 0.0f;
    public boolean isBreaking = false;
    private AbstractBlock targetedAbstractBlock;
    private AbstractBlock lastTargetAbstractBlock;

    /** Time between block placements in seconds */
    private static final float PLACE_COOLDOWN = 0.2f;

    /** Timestamp of last block placement */
    private long lastPlaceTime = 0;


    /**
     * Initializes controller and subscribes to input events.
     *
     * @param player The player entity to control
     * @param world The game world reference
     */
    public PlayerController(Player player, World world) {
        // Store references and subscribe to input events
        this.player = player;
        this.world = world;
        EventBus.getInstance().subscribe(EventType.INPUT, this::onEvent);
    }

    /**
     * Routes input events to appropriate handlers.
     *
     * @param event Game event to process
     */
    public void onEvent(GameEvent event) {
        if (event instanceof InputEvent inputEvent) {
            handleInput(inputEvent);
        }
    }

    /**
     * Processes input events and updates player state accordingly.
     * Handles movement, camera rotation, block interactions.
     *
     * @param event Input event to process
     */
    public void handleInput(InputEvent event) {
        // Update movement flags based on input
        switch (event.action()) {
            case MOVE_FORWARD -> movingForward = event.value() > 0;
            case MOVE_BACKWARD -> movingBackward = event.value() > 0;
            case MOVE_LEFT -> movingLeft = event.value() > 0;
            case MOVE_RIGHT -> movingRight = event.value() > 0;
            case SPRINT -> {
                if (player.isGrounded()) {
                    player.setSprinting(event.value() > 0);
                }
            }
            case MOVE_UP -> jumping = event.value() > 0;
            case LOOK_X -> handleLookX(event.value());
            case LOOK_Y -> handleLookY(event.value());
            case DESTROY_BLOCK -> handleBlockDestruction(event.value() > 0);
            case PLACE_BLOCK -> handleBlockPlacement(event.value() > 0);
        }

        updateMovement();
    }

    /**
     * Handles horizontal camera rotation.
     *
     * @param value Amount to rotate in degrees
     */
    private void handleLookX(float value) {
        // Apply sensitivity and wrap angle
        float yaw = player.getYaw() + value * config.GameConfig.CAMERA_MOUSE_SENSITIVITY;
        player.setYaw(yaw % 360);
    }

    /**
     * Handles vertical camera rotation.
     *
     * @param value Amount to rotate in degrees
     */
    private void handleLookY(float value) {
        // Apply sensitivity and clamp pitch
        float pitch = player.getPitch() + value * config.GameConfig.CAMERA_MOUSE_SENSITIVITY;
        pitch = Math.max(-89.0f, Math.min(89.0f, pitch));
        player.setPitch(pitch);
    }

    /**
     * Updates player movement based on input flags.
     */
    private void updateMovement() {
        Vector3f acceleration = new Vector3f(0);

        // Calculate movement direction from input
        if (movingForward || movingBackward || movingLeft || movingRight) {
            float yaw = (float) Math.toRadians(player.getYaw());

            if (movingForward) {
                acceleration.add(
                        (float) Math.sin(yaw),
                        0,
                        -(float) Math.cos(yaw)
                );
            }
            if (movingBackward) {
                acceleration.add(
                        -(float) Math.sin(yaw),
                        0,
                        (float) Math.cos(yaw)
                );
            }
            if (movingLeft) {
                acceleration.add(
                        -(float) Math.cos(yaw),
                        0,
                        -(float) Math.sin(yaw)
                );
            }
            if (movingRight) {
                acceleration.add(
                        (float) Math.cos(yaw),
                        0,
                        (float) Math.sin(yaw)
                );
            }

            // Normalize combined movement vector
            if (acceleration.length() > 0) {
                acceleration.normalize();
            }
        }

        // Apply jump if grounded
        if (jumping && player.isGrounded()) {
            player.getVelocity().y = config.GameConfig.JUMP_FORCE;
            player.setGrounded(false);
        }

        player.setAcceleration(acceleration);
    }

    /**
     * Handles block breaking input state.
     *
     * @param isDestroying Whether block breaking is active
     */
    private void handleBlockDestruction(boolean isDestroying) {
        if (isDestroying) {
            startBreaking();
        } else {
            stopBreaking();
        }
    }

    /**
     * Handles block placement input state.
     *
     * @param isPlacing Whether block placement is triggered
     */
    private void handleBlockPlacement(boolean isPlacing) {
        if (isPlacing) {
            placeBlock();
        }
    }

    /**
     * Updates targeted block and highlight state.
     */
    private void updateTargetedBlock() {
        // Get new target from raycast
        AbstractBlock newTarget = RayCaster.getTargetBlock(
                player.getCameraPosition(),
                player.getYaw(),
                player.getPitch(),
                0,
                world
        );

        // Update highlight state and breaking progress
        if (targetedAbstractBlock != null) {
            targetedAbstractBlock.setHighlighted(false);
            if (newTarget != targetedAbstractBlock) {
                targetedAbstractBlock.setBreakProgress(0.0f);
                stopBreaking();
            }
        }

        targetedAbstractBlock = newTarget;
        if (newTarget != null) {
            newTarget.setHighlighted(true);
        }
    }

    /**
     * Starts block breaking if target is valid.
     */
    public void startBreaking() {
        if (targetedAbstractBlock != null && !targetedAbstractBlock.isUnbreakable()) {
            isBreaking = true;
        }
    }

    /**
     * Stops block breaking and resets progress.
     */
    public void stopBreaking() {
        isBreaking = false;
        breakingProgress = 0.0f;
        if (targetedAbstractBlock != null) {
            targetedAbstractBlock.setBreakProgress(0.0f);
        }
    }

    /**
     * Updates block breaking progress and destroys block if complete.
     *
     * @param deltaTime Time elapsed since last update
     */
    public void updateBreaking(float deltaTime) {
        // Check if breaking should continue
        if (!isBreaking || targetedAbstractBlock == null || targetedAbstractBlock.isUnbreakable()) {
            if (targetedAbstractBlock != lastTargetAbstractBlock) {
                stopBreaking();
            }
            lastTargetAbstractBlock = targetedAbstractBlock;
            return;
        }

        // Update progress and visual state
        breakingProgress += deltaTime;
        float breakTime = targetedAbstractBlock.getBreakTime();
        targetedAbstractBlock.setBreakProgress(breakingProgress / breakTime);

        // Destroy block if breaking complete
        if (breakingProgress >= breakTime) {
            BlockType type = targetedAbstractBlock.getType();
            world.destroyBlock(targetedAbstractBlock.getPosition());
            EventBus.getInstance().post(new BlockEvent(type, false));
            breakingProgress = 0.0f;
            isBreaking = false;
        }

        lastTargetAbstractBlock = targetedAbstractBlock;
    }

    /**
     * Places a block adjacent to targeted block if possible.
     */
    public void placeBlock() {
        if (targetedAbstractBlock == null) return;

        // Check placement cooldown
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPlaceTime < PLACE_COOLDOWN * 1000) {
            return;
        }

        // Get placement position from raycast
        Vector3f pos = targetedAbstractBlock.getPosition();
        BlockDirection facing = RayCaster.getTargetFace(
                player.getCameraPosition(),
                player.getYaw(),
                player.getPitch(),
                0,
                world
        );

        if (facing != null) {
            // Calculate new block position
            Vector3f newPos = new Vector3f(
                    pos.x() + facing.getDx(),
                    pos.y() + facing.getDy(),
                    pos.z() + facing.getDz()
            );

            // Check for collision with player
            BoundingBox blockBounds = new BoundingBox(1.0f, 1.0f, 1.0f);
            blockBounds.update(newPos);

            Vector3f groundPos = player.getRawPosition();
            Vector3f eyePos = player.getCameraPosition();

            player.getBoundingBox().update(groundPos);
            if (player.getBoundingBox().intersects(blockBounds)) return;

            player.getBoundingBox().update(eyePos);
            if (player.getBoundingBox().intersects(blockBounds)) return;

            // Place block if position is valid
            if (world.getBlock(newPos) == null) {
                world.placeBlock(newPos, BlockType.DIRT);
                EventBus.getInstance().post(new BlockEvent(BlockType.DIRT, true));
                lastPlaceTime = currentTime;
            }
        }
    }

    /**
     * Updates targeting and block breaking each frame.
     *
     * @param deltaTime Time elapsed since last update
     */
    public void update(float deltaTime) {
        updateTargetedBlock();
        updateBreaking(deltaTime);
    }
}