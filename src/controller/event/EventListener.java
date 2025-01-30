package controller.event;

/**
 * Defines standard event listener interface.
 * Implemented by classes needing to handle game events.
 */
public interface EventListener {
    /**
     * Called when subscribed event occurs.
     *
     * @param event The triggered event
     */
    void onEvent(GameEvent event);
}