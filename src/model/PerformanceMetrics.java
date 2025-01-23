package model;

public class PerformanceMetrics {
    private static int totalChunks = 0;
    private static int culledChunks = 0;
    private static int totalBlocks = 0;
    private static int renderedBlocks = 0;
    private static long frameTime = 0;
    private static int frameCount = 0;
    private static long lastSecond = System.currentTimeMillis();
    private static int fps = 0;

    public static void resetFrameMetrics() {
        totalChunks = 0;
        culledChunks = 0;
        totalBlocks = 0;
        renderedBlocks = 0;
        frameTime = System.nanoTime();
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

    public static void logBlocks(int total, int rendered) {
        totalBlocks = total;
        renderedBlocks = rendered;
    }

    public static String getMetricsString() {
        return String.format(
                "FPS: %d | Chunks: %d/%d (%.1f%% culled) | Blocks: %d/%d (%.1f%% culled)",
                fps,
                totalChunks - culledChunks, totalChunks,
                (culledChunks * 100f) / Math.max(1, totalChunks),
                renderedBlocks, totalBlocks,
                ((totalBlocks - renderedBlocks) * 100f) / Math.max(1, totalBlocks)
        );
    }
}