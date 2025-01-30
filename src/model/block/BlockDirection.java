package model.block;

/**
 * Represents the six possible directions relative to a block in a 3D grid.
 * Each direction is defined by a delta in the x, y, and z coordinates.
 */
public enum BlockDirection {
    FRONT(0, 0, 1),     // Positive Z direction
    BACK(0, 0, -1),     // Negative Z direction
    TOP(0, 1, 0),       // Positive Y direction
    BOTTOM(0, -1, 0),   // Negative Y direction
    RIGHT(1, 0, 0),     // Positive X direction
    LEFT(-1, 0, 0);     // Negative X direction

    private final int dx;           // Change in x-coordinate
    private final int dy;           // Change in y-coordinate
    private final int dz;           // Change in z-coordinate

    /**
     * Constructs a BlockDirection with the specified deltas.
     *
     * @param dx The change in the x-coordinate.
     * @param dy The change in the y-coordinate.
     * @param dz The change in the z-coordinate.
     */
    BlockDirection(int dx, int dy, int dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    /**
     * Gets the change in the x-coordinate for this direction.
     *
     * @return The x-coordinate delta.
     */
    public int getDx() {
        return dx;
    }

    /**
     * Gets the change in the y-coordinate for this direction.
     *
     * @return The y-coordinate delta.
     */
    public int getDy() {
        return dy;
    }

    /**
     * Gets the change in the z-coordinate for this direction.
     *
     * @return The z-coordinate delta.
     */
    public int getDz() {
        return dz;
    }
}