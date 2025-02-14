package controller.event;

import model.block.BlockType;

/**
 * Event triggered when blocks are placed or destroyed in the world.
 * Used for tracking block modifications and updating statistics.
 *
 * @see model.block.BlockType
 * @see model.statistics.GameStatistics
 */
public record BlockEvent(BlockType type, boolean isPlacement) implements GameEvent {

    /**
     * Gets the event type for routing through the event system.
     * Block modification events are used to track player actions.
     *
     * @return BLOCK_MODIFICATION event type
     */
    @Override
    public EventType getType() {
        return EventType.BLOCK_MODIFICATION;
    }
}