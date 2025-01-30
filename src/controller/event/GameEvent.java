package controller.event;

/**
 * Base interface for all game events.
 * Provides type information for event routing.
 */
public interface GameEvent {
    /**
     * Gets event category for routing.
     * @return Event's type
     */
    EventType getType();
}