package controller.event;

/**
 * Interface for event listeners in the game.
 * Any class that wants to listen for and handle events should implement this interface.
 */
public interface EventListener {
    /**
     * Handles the event when it is triggered.
     * @param event The event that was posted.
     */
    void onEvent(GameEvent event);  // Called when an event is posted, passing the event object
}
