//
//worldgenerationevent

package controller.event;

import org.joml.Vector3f;

public record WorldGenerationEvent(Vector3f chunkPosition) implements GameEvent {
    @Override
    public EventType getType() {
        return EventType.WORLD_GENERATION;
    }
}