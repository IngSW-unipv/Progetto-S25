package controller.event;

import model.Block;
import model.Camera;

import java.util.List;

/**
 * Represents a render event that involves rendering the game scene.
 * This event contains information about the camera and the list of blocks to be rendered.
 */
public record RenderEvent(Camera camera, List<Block> blocks) implements GameEvent {

    /**
     * Returns the type of the event, which is RENDER in this case.
     * @return The event type (RENDER).
     */
    @Override
    public EventType getType() {
        return EventType.RENDER;
    }
}
