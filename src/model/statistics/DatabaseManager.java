package model.statistics;

import model.save.WorldManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages game statistics tracking and SQLite database operations.
 * Stores block manipulation counts, play time, and world records.
 * Provides leaderboard functionality for world statistics.
 * Uses SQLite for persistent storage across game sessions.
 *
 * @see GameStatistics
 * @see WorldManager
 */
public class DatabaseManager implements GameStatistics {
    /** Maps block types to placement counts */
    private final Map<String, Integer> blocksPlaced = new HashMap<>();

    /** Maps block types to destruction counts */
    private final Map<String, Integer> blocksDestroyed = new HashMap<>();

    /** Tracks total play time for current session */
    private float totalPlayTime = 0;

    /** SQLite database connection URL */
    private static final String DB_URL = "jdbc:sqlite:game_stats.db";

    /**
     * Initializes database connection and creates tables.
     * Verifies SQLite JDBC driver availability on startup.
     */
    public DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found. Please ensure sqlite-jdbc is in the classpath");
            e.printStackTrace();
        }
    }

    /**
     * Creates required database tables if they don't exist.
     * Sets up schema for tracking world statistics with composite primary key.
     */
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Create statistics table with world name and seed as composite key
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS game_statistics (
                    world_name TEXT,
                    world_seed INTEGER,
                    blocks_placed INTEGER,
                    blocks_destroyed INTEGER,
                    play_time_seconds REAL,
                    PRIMARY KEY (world_name, world_seed)
                )""");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    @Override
    public void recordBlockPlaced(String blockType) {
        blocksPlaced.merge(blockType, 1, Integer::sum);
    }

    @Override
    public void recordBlockDestroyed(String blockType) {
        blocksDestroyed.merge(blockType, 1, Integer::sum);
    }

    @Override
    public void updatePlayTime(float deltaTime) {
        totalPlayTime += deltaTime;
    }

    @Override
    public int getBlocksPlaced() {
        return blocksPlaced.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public int getBlocksDestroyed() {
        return blocksDestroyed.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public float getTotalPlayTime() {
        return totalPlayTime;
    }

    /**
     * Saves current statistics to database for specified world.
     * Updates existing record if found, otherwise creates new entry.
     * Combines current session stats with existing totals.
     * Resets session counters after saving.
     *
     * @param worldName Name of world to save statistics for
     */
    @Override
    public void saveToDatabase(String worldName) {
        // Get world seed for composite key
        long seed = WorldManager.getWorldSeed(worldName);

        // Prepare SQL statements
        String selectSQL = "SELECT blocks_placed, blocks_destroyed, play_time_seconds FROM game_statistics WHERE world_name = ? AND world_seed = ?";
        String insertSQL = "INSERT INTO game_statistics (world_name, world_seed, blocks_placed, blocks_destroyed, play_time_seconds) VALUES (?, ?, ?, ?, ?)";
        String updateSQL = "UPDATE game_statistics SET blocks_placed = ?, blocks_destroyed = ?, play_time_seconds = ? WHERE world_name = ? AND world_seed = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Check for existing record
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {
                selectStmt.setString(1, worldName);
                selectStmt.setLong(2, seed);

                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    // Update existing statistics
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                        updateStmt.setInt(1, rs.getInt("blocks_placed") + getBlocksPlaced());
                        updateStmt.setInt(2, rs.getInt("blocks_destroyed") + getBlocksDestroyed());
                        updateStmt.setFloat(3, rs.getFloat("play_time_seconds") + totalPlayTime);
                        updateStmt.setString(4, worldName);
                        updateStmt.setLong(5, seed);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Create new statistics record
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                        insertStmt.setString(1, worldName);
                        insertStmt.setLong(2, seed);
                        insertStmt.setInt(3, getBlocksPlaced());
                        insertStmt.setInt(4, getBlocksDestroyed());
                        insertStmt.setFloat(5, totalPlayTime);
                        insertStmt.executeUpdate();
                    }
                }
            }

            // Reset session counters
            blocksPlaced.clear();
            blocksDestroyed.clear();
            totalPlayTime = 0;

        } catch (SQLException e) {
            System.err.println("Error saving statistics: " + e.getMessage());
        }
    }

    /**
     * Retrieves leaderboard data for all worlds.
     * Orders worlds by total abstractBlocks placed.
     * Combines world name and seed for display.
     *
     * @return List of WorldStats records containing statistics
     */
    public List<WorldStats> getLeaderboard() {
        List<WorldStats> stats = new ArrayList<>();
        String query = """
            SELECT
                world_name || ' (Seed: ' || world_seed || ')' as display_name,
                blocks_placed,
                blocks_destroyed,
                play_time_seconds
            FROM game_statistics
            GROUP BY world_name, world_seed
            ORDER BY blocks_placed DESC
            """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                stats.add(new WorldStats(
                        rs.getString("display_name"),
                        rs.getInt("blocks_placed"),
                        rs.getInt("blocks_destroyed"),
                        rs.getFloat("play_time_seconds")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting leaderboard: " + e.getMessage());
        }
        return stats;
    }

    /**
     * Record containing world statistics for leaderboard display.
     * Includes formatted world name, block counts and play time.
     */
    public record WorldStats(
            String worldName,
            int blocksPlaced,
            int blocksDestroyed,
            float playTimeSeconds
    ) {
        @Override
        public String toString() {
            return String.format("""
                World: %s
                Blocks Placed: %d
                Blocks Destroyed: %d
                Play Time: %.1f seconds
                """,
                    worldName, blocksPlaced, blocksDestroyed, playTimeSeconds
            );
        }
    }
}