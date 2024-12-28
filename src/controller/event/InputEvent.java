package controller.event;

public class InputEvent implements GameEvent {
    private final InputAction action;
    private final float value;

    public InputEvent(InputAction action, float value) {
        this.action = action;
        this.value = value;
    }

    @Override
    public EventType getType() {
        return EventType.INPUT;
    }

    public InputAction getAction() {
        return action;
    }

    public float getValue() {
        return value;
    }
}