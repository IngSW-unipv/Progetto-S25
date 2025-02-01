package model.block;

/**
 * Six cardinal directions in a 3D grid.
 * Stores delta coordinates for each direction.
 */
public enum BlockDirection {
    FRONT(0, 0, 1),     // +Z
    BACK(0, 0, -1),     // -Z
    TOP(0, 1, 0),       // +Y
    BOTTOM(0, -1, 0),   // -Y
    RIGHT(1, 0, 0),     // +X
    LEFT(-1, 0, 0);     // -X

    /** Delta coordinates */
    private final int dx;
    private final int dy;
    private final int dz;


    /**
     * Creates direction with specified deltas
     */
    BlockDirection(int dx, int dy, int dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    /** X-coordinate delta */
    public int getDx() {
        return dx;
    }

    /** Y-coordinate delta */
    public int getDy() {
        return dy;
    }

    /** Z-coordinate delta */
    public int getDz() {
        return dz;
    }
}