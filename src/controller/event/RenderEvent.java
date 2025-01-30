package controller.event;

import model.block.Block;
import model.player.Camera;
import model.world.World;
import java.util.List;

/**
 * Represents a render event that involves rendering the game scene.
 * Contains references to the camera, blocks to render, and the world.
 */
public record RenderEvent(Camera camera, List<Block> blocks, World world) implements GameEvent {

    /**
     * Returns the type of the event.
     *
     * @return The event type (RENDER)
     */
    @Override
    public EventType getType() {
        return EventType.RENDER;
    }
}