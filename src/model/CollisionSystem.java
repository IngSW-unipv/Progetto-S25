package model;

public class CollisionSystem {
    private final World world;

    public CollisionSystem(World world) {
        this.world = world;
    }

    public boolean checkCollision(BoundingBox boundingBox) {
        for (Block block : world.getVisibleBlocks()) {
            if (boundingBox.intersects(block.getBoundingBox())) {
                return true;
            }
        }
        return false;
    }
}