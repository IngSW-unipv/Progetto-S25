//
//GamestateEvent

package controller.event;

import model.world.World;
import org.joml.Vector3f;

public record GameStateEvent(
        Vector3f playerPosition,
        float playerPitch,
        float playerYaw,
        World world
) implements GameEvent {

    @Override
    public EventType getType() {
        return EventType.GAME_STATE;
    }
}