package model;

import config.GameConfig;
import controller.event.InputAction;
import org.joml.Vector3f;

public class Player {
    private final Camera camera;
    private final World world;
    private final BoundingBox boundingBox;
    private final CollisionSystem collisionSystem;
    private Block targetedBlock;
    private float breakingProgress = 0.0f;
    private boolean isBreaking = false;

    // Movement state
    private float verticalVelocity = 0.0f;
    private boolean isGrounded = false;
    private Vector3f velocity = new Vector3f(0, 0, 0);
    private Vector3f acceleration = new Vector3f(0, 0, 0);

    // Movement flags
    private boolean movingForward = false;
    private boolean movingBackward = false;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean jumping = false;
    private boolean crouching = false;

    // Physics constants from config
    private static final float MOVE_SPEED = GameConfig.CAMERA_MOVE_SPEED;
    private static final float JUMP_FORCE = GameConfig.JUMP_FORCE;
    private static final float GRAVITY = GameConfig.GRAVITY;
    private static final float TERMINAL_VELOCITY = GameConfig.TERMINAL_VELOCITY;

    // Additional physics constants
    private static final float AIR_RESISTANCE = 0.02f;
    private static final float GROUND_FRICTION = 0.1f;
    private static final float ACCELERATION = 30.0f;

    // Player dimensions
    private static final float PLAYER_HEIGHT = 1.8f;
    private static final float PLAYER_WIDTH = 0.6f;
    private static final float EYE_HEIGHT = GameConfig.EYE_HEIGHT;

    public Player(World world, Vector3f spawnPosition, float pitch, float yaw) {
        this.world = world;
        this.collisionSystem = new CollisionSystem(world);
        this.camera = new Camera(spawnPosition);
        this.camera.setPitch(pitch);
        this.camera.setYaw(yaw);
        this.boundingBox = new BoundingBox(PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_WIDTH);
        this.boundingBox.update(spawnPosition);
    }

    public void handleMovement(InputAction action, float value) {
        switch (action) {
            case MOVE_FORWARD -> movingForward = value > 0;
            case MOVE_BACKWARD -> movingBackward = value > 0;
            case MOVE_LEFT -> movingLeft = value > 0;
            case MOVE_RIGHT -> movingRight = value > 0;
            case MOVE_UP -> jumping = value > 0;
            case MOVE_DOWN -> crouching = value > 0;
            case LOOK_X -> camera.rotate(value, 0);
            case LOOK_Y -> camera.rotate(0, value);
        }
    }

    public void update(float deltaTime) {
        updateMovement(deltaTime);
        updatePhysics(deltaTime);
        updatePosition(deltaTime);
        updateTargetedBlock();
        updateBreaking(deltaTime);
    }

    private void updateMovement(float deltaTime) {
        float yaw = (float) Math.toRadians(camera.getYaw());
        Vector3f forward = new Vector3f(
                (float) Math.sin(yaw),
                0,
                -(float) Math.cos(yaw)
        );
        Vector3f right = new Vector3f(
                (float) Math.cos(yaw),
                0,
                (float) Math.sin(yaw)
        );

        acceleration.set(0, 0, 0);

        if (movingForward) acceleration.add(forward.mul(ACCELERATION));
        if (movingBackward) acceleration.add(forward.mul(-ACCELERATION));
        if (movingRight) acceleration.add(right.mul(ACCELERATION));
        if (movingLeft) acceleration.add(right.mul(-ACCELERATION));

        if (acceleration.length() > 0) {
            acceleration.normalize().mul(ACCELERATION);
        }

        if (jumping && isGrounded) {
            verticalVelocity = JUMP_FORCE;
            isGrounded = false;
        }
    }

    private void updatePhysics(float deltaTime) {
        verticalVelocity += GRAVITY * deltaTime;
        verticalVelocity = Math.max(verticalVelocity, TERMINAL_VELOCITY);

        Vector3f horizontalVelocity = new Vector3f(velocity.x, 0, velocity.z);
        float resistance = isGrounded ? GROUND_FRICTION : AIR_RESISTANCE;
        horizontalVelocity.mul(1 - resistance);
        horizontalVelocity.add(new Vector3f(acceleration).mul(deltaTime));

        if (horizontalVelocity.length() > MOVE_SPEED) {
            horizontalVelocity.normalize().mul(MOVE_SPEED);
        }

        velocity.x = horizontalVelocity.x;
        velocity.z = horizontalVelocity.z;
        velocity.y = verticalVelocity;
    }

    private void updatePosition(float deltaTime) {
        Vector3f currentPos = camera.getRawPosition();
        Vector3f newPosition = new Vector3f(currentPos);

        newPosition.x = currentPos.x + velocity.x * deltaTime;
        newPosition.z = currentPos.z + velocity.z * deltaTime;

        boolean horizontalMoved = false;

        if (canMoveToPosition(newPosition, Direction.HORIZONTAL)) {
            camera.setPosition(newPosition);
            horizontalMoved = true;
        }

        if (!horizontalMoved) {
            newPosition.x = currentPos.x + velocity.x * deltaTime;
            newPosition.z = currentPos.z;
            if (canMoveToPosition(newPosition, Direction.HORIZONTAL)) {
                camera.setPosition(newPosition);
                horizontalMoved = true;
            }
        }

        if (!horizontalMoved) {
            newPosition.x = currentPos.x;
            newPosition.z = currentPos.z + velocity.z * deltaTime;
            if (canMoveToPosition(newPosition, Direction.HORIZONTAL)) {
                camera.setPosition(newPosition);
            }
        }

        newPosition = camera.getRawPosition();
        newPosition.y += velocity.y * deltaTime;

        if (canMoveToPosition(newPosition, Direction.VERTICAL)) {
            camera.setPosition(newPosition);
            isGrounded = false;
        } else {
            if (velocity.y < 0) {
                isGrounded = true;
                verticalVelocity = 0;
            } else if (velocity.y > 0) {
                verticalVelocity = 0;
            }
        }

        boundingBox.update(camera.getRawPosition());
    }

    private boolean canMoveToPosition(Vector3f newPosition, Direction direction) {
        Vector3f groundPosition = new Vector3f(camera.getRawPosition());
        Vector3f eyePosition = new Vector3f(camera.getPosition());

        switch (direction) {
            case HORIZONTAL -> {
                groundPosition.x = newPosition.x;
                groundPosition.z = newPosition.z;
                eyePosition.x = newPosition.x;
                eyePosition.z = newPosition.z;
            }
            case VERTICAL -> {
                groundPosition.y = newPosition.y;
                eyePosition.y = newPosition.y + EYE_HEIGHT;
            }
        }

        boundingBox.update(groundPosition);
        if (collisionSystem.checkCollision(boundingBox)) return false;

        boundingBox.update(eyePosition);
        return !collisionSystem.checkCollision(boundingBox);
    }

    public void updateTargetedBlock() {
        if (targetedBlock != null) {
            targetedBlock.setHighlighted(false);
            targetedBlock = null;
        }

        targetedBlock = RayCaster.getTargetBlock(
                camera.getPosition(),
                camera.getYaw(),
                camera.getPitch(),
                camera.getRoll(),
                world
        );

        if (targetedBlock != null) {
            targetedBlock.setHighlighted(true);
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
        if (isBreaking && targetedBlock != null && !targetedBlock.getType().isUnbreakable()) {
            breakingProgress += deltaTime;
            targetedBlock.setBreakProgress(breakingProgress / targetedBlock.getType().getBreakTime());

            if (breakingProgress >= targetedBlock.getType().getBreakTime()) {
                world.destroyBlock(targetedBlock.getPosition());
                breakingProgress = 0.0f;
                isBreaking = false;
            }
        }
    }

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

            BoundingBox blockBounds = new BoundingBox(1.0f, 1.0f, 1.0f);
            blockBounds.update(newPos);

            Vector3f groundPos = new Vector3f(camera.getRawPosition());
            Vector3f eyePos = new Vector3f(camera.getPosition());

            boundingBox.update(groundPos);
            if (boundingBox.intersects(blockBounds)) return;

            boundingBox.update(eyePos);
            if (boundingBox.intersects(blockBounds)) return;

            if (world.getBlock(newPos) == null) {
                world.placeBlock(newPos, BlockType.DIRT);
            }
        }
    }

    public Camera getCamera() { return camera; }
    public Vector3f getPosition() { return camera.getRawPosition(); }
    public BoundingBox getBoundingBox() { return boundingBox; }
}