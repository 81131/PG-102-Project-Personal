package goldenflame.observer;

import java.util.ArrayList;
import java.util.List;

public class DomainEventsPublisher {
    private final List<EventListener> listeners = new ArrayList<>();

    public void registerListener(EventListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(EventListener listener) {
        listeners.remove(listener);
    }

    public void publishEvent(Event event) {
        for (EventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}
