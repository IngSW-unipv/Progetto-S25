package model.world;

/**
 * Stores basic metadata about a saved world including name and generation seed
 * Immutable record used for world management and loading
 */
public record WorldData(String name, long seed) {
    /**
     * Formats world data for display in user interface
     */
    @Override
    public String toString() {
        return name + "\nSeed: " + seed;
    }
}