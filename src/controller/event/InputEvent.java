package controller.event;

/**
 * Represents an input event that triggers specific actions in the game.
 * This event includes an action and a value associated with the action.
 */
public record InputEvent(InputAction action, float value) implements GameEvent {

    /**
     * Returns the type of the event, which is INPUT in this case.
     * @return The event type (INPUT).
     */
    @Override
    public EventType getType() {
        return EventType.INPUT;
    }
}
