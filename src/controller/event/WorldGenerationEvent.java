package controller.event;

import org.joml.Vector3f;

/**
 * Triggers generation of new world chunks.
 */
public record WorldGenerationEvent(Vector3f chunkPosition) implements GameEvent {

    /**
     * @return WORLD_GENERATION event type
     */
    @Override
    public EventType getType() {
        return EventType.WORLD_GENERATION;
    }
}