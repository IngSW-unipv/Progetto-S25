package controller.event;

public record MenuActionEvent(MenuAction action) implements GameEvent {
    @Override
    public EventType getType() {
        return EventType.MENU;
    }
}