package util;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Tracks performance metrics for render-time analysis and optimization
 * Collects frame timing, chunk culling and block visibility statistics
 */
public class PerformanceMetrics {
    /** Frame timing data collection */
    private static final Queue<Long> frameTimes = new LinkedList<>();
    private static long lastFrameTime = System.nanoTime();
    private static int totalChunks = 0;
    private static int culledChunks = 0;
    private static int totalBlocks = 0;
    private static int renderedBlocks = 0;
    private static int occludedBlocks = 0;
    private static int culledBlocks = 0;
    private static int frameCount = 0;
    private static long lastSecond = System.currentTimeMillis();
    private static int fps = 0;


    /**
     * Initializes frame timing and resets counters
     * Called at start of each render frame
     */
    public static void startFrame() {
        long currentTime = System.nanoTime();
        long frameTime = currentTime - lastFrameTime;
        lastFrameTime = currentTime;

        frameTimes.offer(frameTime);
        if (frameTimes.size() > 100) {
            frameTimes.poll();
        }

        resetFrameMetrics();
    }

    /**
     * Resets all metric counters to initial state
     */
    public static void resetFrameMetrics() {
        totalChunks = 0;
        culledChunks = 0;
        totalBlocks = 0;
        renderedBlocks = 0;
        occludedBlocks = 0;
    }

    /**
     * Updates frame count and FPS calculation
     * Called once per frame
     */
    public static void updateFrameMetrics() {
        frameCount++;
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastSecond >= 1000) {
            fps = frameCount;
            frameCount = 0;
            lastSecond = currentTime;
        }
    }

    /**
     * Records chunk culling metrics
     * @param culled Whether chunk was culled this frame
     */
    public static void logChunk(int total, int culled) {
        totalChunks = total;
        culledChunks = culled;
    }

    /**
     * Records block visibility metrics
     * @param total Total blocks in view
     * @param rendered Blocks actually rendered
     * @param occluded Blocks hidden by occlusion
     */
    public static void logBlocks(int total, int rendered, int occluded, int culled) {
        totalBlocks += total;
        renderedBlocks += rendered;
        occludedBlocks += occluded;
        culledBlocks += culled;
    }

    /**
     * Formats current metrics into human-readable string
     * Includes FPS, frame time, chunk and block statistics
     */
    public static String getMetricsString() {
        double avgFrameTime = frameTimes.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0) / 1_000_000.0;

        return String.format(
            "FPS: %d (%.2fms/frame) | " +
                    "Chunks: %d/%d (%.1f%% culled) | " +
                    "Blocks: %d/%d (%.1f%% culled, %.1f%% occluded)",
            fps, avgFrameTime,
            totalChunks - culledChunks, totalChunks,
            (culledChunks * 100f) / Math.max(1, totalChunks),
            renderedBlocks, totalBlocks,
            ((totalBlocks - renderedBlocks) * 100f) / Math.max(1, totalBlocks),
            (occludedBlocks * 100f) / Math.max(1, totalBlocks)
        );
    }
}