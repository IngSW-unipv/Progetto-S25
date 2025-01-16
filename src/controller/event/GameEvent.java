package controller.event;

/**
 * Interface representing a game event.
 * Any event in the game should implement this interface to provide the event type.
 */
public interface GameEvent {
    /**
     * Returns the type of the event.
     * @return The event type.
     */
    EventType getType();  // The type of event (e.g., input, world generation, game state, render)
}
