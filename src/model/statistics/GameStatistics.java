package model.statistics;

/**
 * Interface defining contract for tracking game statistics.
 * Provides methods for recording and retrieving player actions and game metrics.
 */
public interface GameStatistics {
    /**
     * Records a block being placed
     * @param blockType Type of block placed
     */
    void recordBlockPlaced(String blockType);

    /**
     * Records a block being destroyed
     * @param blockType Type of block destroyed
     */
    void recordBlockDestroyed(String blockType);

    /**
     * Updates total play time
     * @param deltaTime Time elapsed since last update in seconds
     */
    void updatePlayTime(float deltaTime);

    /**
     * Gets total number of blocks placed
     * @return Number of blocks placed
     */
    int getBlocksPlaced();

    /**
     * Gets total number of blocks destroyed
     * @return Number of blocks destroyed
     */
    int getBlocksDestroyed();

    /**
     * Gets total play time in seconds
     * @return Total play time
     */
    float getTotalPlayTime();

    /**
     * Saves current statistics to database
     * @param worldName Name of current world
     */
    void saveToDatabase(String worldName);
}