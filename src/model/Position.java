package model;

/**
 * Represents a 3D position in a block-based coordinate system.
 * This immutable record holds the x, y, and z coordinates of a position.
 */
public record Position(int x, int y, int z) {

    /**
     * Constructs a new Position record.
     *
     * @param x The x-coordinate of the position.
     * @param y The y-coordinate of the position.
     * @param z The z-coordinate of the position.
     */
    public Position {
        // Validation or normalization logic can be added here if necessary.
    }
}
