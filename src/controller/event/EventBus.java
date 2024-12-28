package controller.event;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    private static EventBus instance;
    private final Map<EventType, List<EventListener>> listeners;

    private EventBus() {
        listeners = new ConcurrentHashMap<>();
        for (EventType type : EventType.values()) {
            listeners.put(type, new CopyOnWriteArrayList<>());
        }
    }

    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    public void subscribe(EventType type, EventListener listener) {
        listeners.get(type).add(listener);
    }

    public void unsubscribe(EventType type, EventListener listener) {
        listeners.get(type).remove(listener);
    }

    public void post(GameEvent event) {
        for (EventListener listener : listeners.get(event.getType())) {
            listener.onEvent(event);
        }
    }
}