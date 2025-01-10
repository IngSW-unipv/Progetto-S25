package controller.event;

import model.Block;
import model.Camera;

import java.util.List;

public record RenderEvent(Camera camera, List<Block> blocks) implements GameEvent {

    @Override
    public EventType getType() {
        return EventType.RENDER;
    }
}