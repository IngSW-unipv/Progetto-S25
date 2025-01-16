package model;

import java.util.Objects;

/**
 * Represents the position of a chunk in a 2D coordinate system.
 * This is an immutable record that holds the x and z coordinates of the chunk.
 */
public record ChunkPosition(int x, int z) {

    /**
     * Checks if this ChunkPosition is equal to another object.
     * Two ChunkPosition objects are considered equal if their x and z coordinates are the same.
     *
     * @param o The object to compare with this ChunkPosition.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Reference equality
        if (o == null || getClass() != o.getClass()) return false; // Type check
        ChunkPosition that = (ChunkPosition) o;
        return x == that.x && z == that.z; // Coordinate comparison
    }

    /**
     * Computes the hash code for this ChunkPosition.
     * The hash code is based on the x and z coordinates.
     *
     * @return The hash code of this ChunkPosition.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }
}