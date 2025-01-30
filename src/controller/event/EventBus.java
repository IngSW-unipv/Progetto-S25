package controller.event;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Core event system managing publish/subscribe functionality.
 * Uses singleton pattern to ensure centralized event handling.
 */
public class EventBus {
    /** Singleton instance */
    private static EventBus instance;

    /** Thread-safe map holding event listeners */
    private final Map<EventType, List<EventListener>> listeners;


    /**
     * Initializes thread-safe collections for event handling.
     */
    private EventBus() {
        listeners = new ConcurrentHashMap<>();
        for (EventType type : EventType.values()) {
            listeners.put(type, new CopyOnWriteArrayList<>());
        }
    }

    /**
     * Gets singleton instance, creating if needed.
     *
     * @return The EventBus instance
     */
    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    /**
     * Registers listener for specified event type.
     *
     * @param type Event type to listen for
     * @param listener Listener to notify
     */
    public void subscribe(EventType type, EventListener listener) {
        listeners.get(type).add(listener);
    }

    /**
     * Removes listener from specified event type.
     *
     * @param type Event type to unsubscribe from
     * @param listener Listener to remove
     */
    public void unsubscribe(EventType type, EventListener listener) {
        listeners.get(type).remove(listener);
    }

    /**
     * Notifies all listeners registered for event's type.
     *
     * @param event Event to broadcast
     */
    public void post(GameEvent event) {
        for (EventListener listener : listeners.get(event.getType())) {
            listener.onEvent(event);
        }
    }
}