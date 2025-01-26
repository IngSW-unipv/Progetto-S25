//chunkloader

package model;

import controller.event.EventBus;
import controller.event.WorldGenerationEvent;
import org.joml.Vector3f;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ChunkLoader {
    private final ExecutorService executorService;
    private final LinkedBlockingQueue<ChunkLoadTask> chunkLoadQueue;
    private volatile boolean isRunning;

    public ChunkLoader(int threadCount) {
        this.executorService = Executors.newFixedThreadPool(threadCount);
        this.chunkLoadQueue = new LinkedBlockingQueue<>();
        this.isRunning = true;
        startProcessingQueue();
    }

    private void startProcessingQueue() {
        Thread queueProcessor = new Thread(() -> {
            while (isRunning || !chunkLoadQueue.isEmpty()) {
                try {
                    ChunkLoadTask task = chunkLoadQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (task != null) {
                        executorService.submit(task::execute);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        queueProcessor.setDaemon(true);
        queueProcessor.start();
    }

    public void queueChunkLoad(Vector3f position) {
        EventBus.getInstance().post(new WorldGenerationEvent(position));
        chunkLoadQueue.offer(new ChunkLoadTask(position));
    }

    public void shutdown() {
        isRunning = false;
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}