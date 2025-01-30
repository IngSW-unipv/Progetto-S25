package controller.event;

/**
 * Represents player input events with associated values.
 */
public record InputEvent(InputAction action, float value) implements GameEvent {

    /**
     * @return INPUT event type
     */
    @Override
    public EventType getType() {
        return EventType.INPUT;
    }
}