package controller.event;

public record InputEvent(InputAction action, float value) implements GameEvent {

    @Override
    public EventType getType() {
        return EventType.INPUT;
    }
}