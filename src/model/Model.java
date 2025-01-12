package model;

import controller.event.EventBus;
import controller.event.RenderEvent;
import org.joml.Vector3f;
import java.util.Random;

public class Model {
    private final GameState gameState;
    private final Camera camera;
    private final World world;
    private final CollisionSystem collisionSystem;
    private final long worldSeed;
    private Block highlightedBlock;
    private float breakingProgress = 0.0f;
    private boolean isBreaking = false;

    public Model() {
        this.gameState = new GameState();
        Vector3f initialPosition = new Vector3f(0, 50, 0);
        this.worldSeed = new Random().nextLong();
        this.world = new World(initialPosition, worldSeed);
        this.collisionSystem = new CollisionSystem(world);
        this.camera = new Camera(collisionSystem, initialPosition);
    }

    public Model(long seed) {
        this.gameState = new GameState();
        Vector3f initialPosition = new Vector3f(0, 50, 0);
        this.worldSeed = seed;
        this.world = new World(initialPosition, worldSeed);
        this.collisionSystem = new CollisionSystem(world);
        this.camera = new Camera(collisionSystem, initialPosition);
    }

    public GameState getGameState() {
        return gameState;
    }

    public Camera getCamera() {
        return camera;
    }

    public void updateGame(float deltaTime) {
        if (highlightedBlock != null) {
            highlightedBlock.setHighlighted(false);
            highlightedBlock = null;
        }

        highlightedBlock = RayCaster.getTargetBlock(
            camera.getPosition(),
            camera.getYaw(),
            camera.getPitch(),
            camera.getRoll(),
            world
        );

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

        gameState.update();
        EventBus.getInstance().post(new RenderEvent(camera, world.getVisibleBlocks()));
        world.update(camera.getPosition());
    }

    public void startBreaking() {
        if (highlightedBlock != null && highlightedBlock.getType() != BlockType.BEDROCK) {
            isBreaking = true;
        }
    }

    public void stopBreaking() {
        isBreaking = false;
        breakingProgress = 0.0f;
        if (highlightedBlock != null) {
            highlightedBlock.setBreakProgress(0.0f);
        }
    }

    public void placeBlock() {
        // Debug print
        //System.out.println("Attempting to place block...");
        //System.out.println("Highlighted block: " + (highlightedBlock != null ? highlightedBlock.getPosition() : "null"));

        if (highlightedBlock != null) {
            Position pos = highlightedBlock.getPosition();
            BlockDirection facing = RayCaster.getTargetFace(
                camera.getPosition(),
                camera.getYaw(),
                camera.getPitch(),
                camera.getRoll(),
                world
            );

            // Debug print
            //System.out.println("Target face: " + facing);

            if (facing != null) {
                Position newPos = new Position(
                    pos.x() + facing.getDx(),
                    pos.y() + facing.getDy(),
                    pos.z() + facing.getDz()
                );

                // Debug print
                //System.out.println("New position: " + newPos);

                BoundingBox newBlockBounds = new BoundingBox(1.0f, 1.0f, 1.0f);
                newBlockBounds.update(new Vector3f(newPos.x(), newPos.y(), newPos.z()));

                BoundingBox playerBounds = camera.getBoundingBox();
                boolean intersects = newBlockBounds.intersects(playerBounds);
                boolean existingBlock = world.getBlock(newPos) != null;

                // Debug print
                //System.out.println("Intersects with player: " + intersects);
                //System.out.println("Existing block at position: " + existingBlock);

                if (!intersects && !existingBlock) {
                    world.placeBlock(newPos, BlockType.DIRT);
                    // Debug print
                    //System.out.println("Block placed successfully");
                }
            }
        }
    }
}