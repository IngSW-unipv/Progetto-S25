package model;

import java.util.LinkedList;
import java.util.Queue;

public class PerformanceMetrics {
    private static final Queue<Long> frameTimes = new LinkedList<>();
    private static long lastFrameTime = System.nanoTime();
    private static int totalChunks = 0;
    private static int culledChunks = 0;
    private static int totalBlocks = 0;
    private static int renderedBlocks = 0;
    private static int occludedBlocks = 0;
    private static int frameCount = 0;
    private static long lastSecond = System.currentTimeMillis();
    private static int fps = 0;

    public static void startFrame() {
        long currentTime = System.nanoTime();
        long frameTime = currentTime - lastFrameTime;
        lastFrameTime = currentTime;

        frameTimes.offer(frameTime);
        if (frameTimes.size() > 100) {
            frameTimes.poll();
        }

        // Reset counters at start of frame
        totalChunks = 0;
        culledChunks = 0;
        totalBlocks = 0;
        renderedBlocks = 0;
        occludedBlocks = 0;
    }

    public static void resetFrameMetrics() {
        totalChunks = 0;
        culledChunks = 0;
        totalBlocks = 0;
        renderedBlocks = 0;
        occludedBlocks = 0;
    }

    public static void updateFrameMetrics() {
        frameCount++;
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastSecond >= 1000) {
            fps = frameCount;
            frameCount = 0;
            lastSecond = currentTime;
        }
    }

    public static void logChunk(boolean culled) {
        totalChunks++;
        if(culled) culledChunks++;
    }

    public static void logBlocks(int total, int rendered, int occluded) {
        totalBlocks = total;
        renderedBlocks = rendered;
        occludedBlocks = occluded;
    }

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