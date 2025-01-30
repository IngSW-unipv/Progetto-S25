package controller.input;

import controller.event.EventBus;
import controller.event.EventType;
import controller.event.GameEvent;
import controller.event.InputEvent;
import model.block.Block;
import model.block.BlockDirection;
import model.block.BlockType;
import model.physics.BoundingBox;
import model.player.Player;
import model.player.RayCaster;
import model.world.World;
import org.joml.Vector3f;

public class PlayerController {
    //implements EventListener {
    private final Player player;
    private final World world;

    // Movement state
    private boolean movingForward;
    private boolean movingBackward;
    private boolean movingLeft;
    private boolean movingRight;
    private boolean jumping;

    public float breakingProgress = 0.0f;
    public boolean isBreaking = false;
    private Block targetedBlock;
    private Block lastTargetBlock;


    public PlayerController(Player player, World world) {
        this.player = player;
        this.world = world;
        EventBus eventBus = EventBus.getInstance();
        eventBus.subscribe(EventType.INPUT, this::onEvent);
    }

    public void onEvent(GameEvent event) {
        if (event instanceof InputEvent inputEvent) {
            handleInput(inputEvent);
        }
    }

    public void handleInput(InputEvent event) {
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

    private void handleLookX(float value) {
        float yaw = player.getYaw() + value * config.GameConfig.CAMERA_MOUSE_SENSITIVITY;
        player.setYaw(yaw % 360);
    }

    private void handleLookY(float value) {
        float pitch = player.getPitch() + value * config.GameConfig.CAMERA_MOUSE_SENSITIVITY; // Changed minus to plus
        pitch = Math.max(-89.0f, Math.min(89.0f, pitch));
        player.setPitch(pitch);
    }

    private void updateMovement() {
        Vector3f acceleration = new Vector3f(0);

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

            if (acceleration.length() > 0) {
                acceleration.normalize();
            }
        }

        if (jumping && player.isGrounded()) {
            player.getVelocity().y = config.GameConfig.JUMP_FORCE;
            player.setGrounded(false);
        }

        player.setAcceleration(acceleration);
    }

    private void handleBlockDestruction(boolean isDestroying) {
        if (isDestroying) {
            startBreaking();
        } else {
            stopBreaking();
        }
    }

    private void handleBlockPlacement(boolean isPlacing) {
        if (isPlacing) {
            placeBlock();
        }
    }

    private void updateTargetedBlock() {
        Block newTarget = RayCaster.getTargetBlock(
                player.getCameraPosition(),
                player.getYaw(),
                player.getPitch(),
                0,
                world
        );

        if (targetedBlock != null) {
            targetedBlock.setHighlighted(false);
            if (newTarget != targetedBlock) {
                targetedBlock.setBreakProgress(0.0f);
                stopBreaking();
            }
        }

        targetedBlock = newTarget;
        if (newTarget != null) {
            newTarget.setHighlighted(true);
        }
    }

    public void startBreaking() {
        if (targetedBlock != null && !targetedBlock.getType().isUnbreakable()) {
            isBreaking = true;
        }
    }

    public void stopBreaking() {
        isBreaking = false;
        breakingProgress = 0.0f;
        if (targetedBlock != null) {
            targetedBlock.setBreakProgress(0.0f);
        }
    }

    public void updateBreaking(float deltaTime) {
        if (!isBreaking || targetedBlock == null || targetedBlock.getType().isUnbreakable()) {
            if (targetedBlock != lastTargetBlock) {
                stopBreaking();
            }
            lastTargetBlock = targetedBlock;
            return;
        }

        breakingProgress += deltaTime;
        targetedBlock.setBreakProgress(breakingProgress / targetedBlock.getType().getBreakTime());

        if (breakingProgress >= targetedBlock.getType().getBreakTime()) {
            world.destroyBlock(targetedBlock.getPosition());
            breakingProgress = 0.0f;
            isBreaking = false;
        }

        lastTargetBlock = targetedBlock;
    }

    public void placeBlock() {
        if (targetedBlock == null) return;

        Vector3f pos = targetedBlock.getPosition();
        BlockDirection facing = RayCaster.getTargetFace(
                player.getCameraPosition(),
                player.getYaw(),
                player.getPitch(),
                0,
                world
        );

        if (facing != null) {
            Vector3f newPos = new Vector3f(
                    pos.x() + facing.getDx(),
                    pos.y() + facing.getDy(),
                    pos.z() + facing.getDz()
            );

            BoundingBox blockBounds = new BoundingBox(1.0f, 1.0f, 1.0f);
            blockBounds.update(newPos);

            Vector3f groundPos = player.getRawPosition();
            Vector3f eyePos = player.getCameraPosition();

            player.getBoundingBox().update(groundPos);
            if (player.getBoundingBox().intersects(blockBounds)) return;

            player.getBoundingBox().update(eyePos);
            if (player.getBoundingBox().intersects(blockBounds)) return;

            if (world.getBlock(newPos) == null) {
                world.placeBlock(newPos, BlockType.DIRT);
            }
        }
    }

    public void update(float deltaTime) {
        updateTargetedBlock();
        updateBreaking(deltaTime);
    }

}
