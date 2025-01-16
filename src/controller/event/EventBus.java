package controller.event;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    private static EventBus instance; // Singleton instance of EventBus
    private final Map<EventType, List<EventListener>> listeners; // Map to hold event listeners for each event type

    // Private constructor to prevent external instantiation
    private EventBus() {
        listeners = new ConcurrentHashMap<>(); // Initialize the map to store listeners
        // Initialize a listener list for each event type
        for (EventType type : EventType.values()) {
            listeners.put(type, new CopyOnWriteArrayList<>()); // Use CopyOnWriteArrayList for thread-safe operations
        }
    }

    // Singleton access method
    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus(); // Instantiate the EventBus if not already created
        }
        return instance; // Return the single instance of EventBus
    }

    // Method to subscribe a listener to an event type
    public void subscribe(EventType type, EventListener listener) {
        listeners.get(type).add(listener); // Add the listener to the list for the given event type
    }

    // Method to unsubscribe a listener from an event type
    public void unsubscribe(EventType type, EventListener listener) {
        listeners.get(type).remove(listener); // Remove the listener from the list for the given event type
    }

    // Method to post an event to all subscribed listeners
    public void post(GameEvent event) {
        // Iterate through the listeners for the event's type and notify them
        for (EventListener listener : listeners.get(event.getType())) {
            listener.onEvent(event); // Trigger the event listener's onEvent method
        }
    }
}
