package model;

import controller.event.EventBus;
import controller.event.RenderEvent;
import org.joml.Vector3f;
import java.util.Random;

/**
 * The Model class represents the core game model, managing the game's state,
 * camera, world, collision system, and block interactions.
 */
public class Model {
    private final GameState gameState;              // Tracks the running state of the game
    private final Camera camera;                    // Manages the player's view and movement
    private final World world;                      // Represents the game world
    private final CollisionSystem collisionSystem;  // Handles collision detection
    private final long worldSeed;                   // Seed for world generation
    private Block highlightedBlock;                 // Currently highlighted block
    private float breakingProgress = 0.0f;          // Progress of breaking a block
    private boolean isBreaking = false;             // Whether a block is being broken

    /**
     * Default constructor initializes the game with a random world seed.
     */
    public Model() {
        this.gameState = new GameState();
        Vector3f initialPosition = new Vector3f(0, 50, 0);
        this.worldSeed = new Random().nextLong();
        this.world = new World(initialPosition, worldSeed);
        this.collisionSystem = new CollisionSystem(world);
        this.camera = new Camera(collisionSystem, initialPosition);
    }

    /**
     * Constructor initializes the game with a specific world seed.
     *
     * @param seed The seed for world generation.
     */
    public Model(long seed) {
        this.gameState = new GameState();
        Vector3f initialPosition = new Vector3f(0, 50, 0);
        this.worldSeed = seed;
        this.world = new World(initialPosition, worldSeed);
        this.collisionSystem = new CollisionSystem(world);
        this.camera = new Camera(collisionSystem, initialPosition);
    }

    // Getters for core components
    public GameState getGameState() {
        return gameState;
    }

    public Camera getCamera() {
        return camera;
    }

    /**
     * Updates the game logic, including block highlighting, breaking, and world updates.
     *
     * @param deltaTime The time elapsed since the last update, in seconds.
     */
    public void updateGame(float deltaTime) {
        // Reset highlighting for the previously highlighted block
        if (highlightedBlock != null) {
            highlightedBlock.setHighlighted(false);
            highlightedBlock = null;
        }

        // Determine the currently highlighted block
        highlightedBlock = RayCaster.getTargetBlock(
            camera.getPosition(),
            camera.getYaw(),
            camera.getPitch(),
            camera.getRoll(),
            world
        );

        // Update highlighting and breaking logic
        if (highlightedBlock != null) {
            highlightedBlock.setHighlighted(true);
            if (isBreaking && highlightedBlock.getType() != BlockType.BEDROCK) {
                breakingProgress += deltaTime;
                highlightedBlock.setBreakProgress(breakingProgress / highlightedBlock.getType().getBreakTime());
                if (breakingProgress >= highlightedBlock.getType().getBreakTime()) {
                    world.destroyBlock(highlightedBlock.getPosition());
                    breakingProgress = 0.0f;
                    isBreaking = false;
                }
            }
        } else {
            breakingProgress = 0.0f;
            isBreaking = false;
        }

        // Update game state and notify the event bus
        gameState.update();
        EventBus.getInstance().post(new RenderEvent(camera, world.getVisibleBlocks()));
        world.update(camera.getPosition());
    }

    /**
     * Initiates the breaking process for the highlighted block, if applicable.
     */
    public void startBreaking() {
        if (highlightedBlock != null && highlightedBlock.getType() != BlockType.BEDROCK) {
            isBreaking = true;
        }
    }

    /**
     * Stops the breaking process and resets progress.
     */
    public void stopBreaking() {
        isBreaking = false;
        breakingProgress = 0.0f;
        if (highlightedBlock != null) {
            highlightedBlock.setBreakProgress(0.0f);
        }
    }

    /**
     * Attempts to place a block adjacent to the highlighted block.
     */
    public void placeBlock() {
        if (highlightedBlock != null) {
            Position pos = highlightedBlock.getPosition();
            BlockDirection facing = RayCaster.getTargetFace(
                    camera.getPosition(),
                    camera.getYaw(),
                    camera.getPitch(),
                    camera.getRoll(),
                    world
            );

            if (facing != null) {
                // Calculate the new block position
                Position newPos = new Position(
                        pos.x() + facing.getDx(),
                        pos.y() + facing.getDy(),
                        pos.z() + facing.getDz()
                );

                // Check for collisions and existing blocks
                BoundingBox newBlockBounds = new BoundingBox(1.0f, 1.0f, 1.0f);
                newBlockBounds.update(new Vector3f(newPos.x(), newPos.y(), newPos.z()));

                BoundingBox playerBounds = camera.getBoundingBox();
                boolean intersects = newBlockBounds.intersects(playerBounds);
                boolean existingBlock = world.getBlock(newPos) != null;

                // Place the block if valid
                if (!intersects && !existingBlock) {
                    world.placeBlock(newPos, BlockType.DIRT);
                }
            }
        }
    }
}
