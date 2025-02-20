package controller.event;

import model.block.AbstractBlock;
import model.player.Camera;
import model.world.World;
import java.util.List;

/**
 * Contains state needed for rendering a frame.
 */
public record RenderEvent(Camera camera, List<AbstractBlock> abstractBlocks, World world) implements GameEvent {

    /**
     * @return RENDER event type
     */
    @Override
    public EventType getType() {
        return EventType.RENDER;
    }
}