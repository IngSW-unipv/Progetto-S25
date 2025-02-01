package model.world;

import controller.event.EventBus;
import controller.event.WorldGenerationEvent;
import org.joml.Vector3f;

/**
 * Chunk load work unit.
 * Posts generation event for position.
 */
public class ChunkLoadTask {
    /** Target chunk position */
    private final Vector3f position;


    /** Creates task for position */
    public ChunkLoadTask(Vector3f position) {
        this.position = new Vector3f(position);
    }

    /** Posts generation event */
    public void execute() {
        EventBus.getInstance().post(new WorldGenerationEvent(position));
    }
}
