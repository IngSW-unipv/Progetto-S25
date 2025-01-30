package controller.event;

public record StartGameMenuEvent(String worldName, long seed) implements GameEvent {
    @Override
    public EventType getType() {
        return EventType.MENU;
    }
}