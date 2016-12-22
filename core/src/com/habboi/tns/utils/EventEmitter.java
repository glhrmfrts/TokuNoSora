package com.habboi.tns.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class EventEmitter {

    public interface EventHandler {
        public void onEvent(Object value);
    }

    private static EventEmitter instance;
    private HashMap<String, ArrayList<EventHandler>> events = new HashMap<>();

    public static EventEmitter get() {
        if (instance == null) {
            instance = new EventEmitter();
        }

        return instance;
    }

    public void listen(String event, EventHandler handler) {
        ArrayList<EventHandler> handlers = events.get(event);

        if (handlers == null) {
            handlers = new ArrayList<>();
            events.put(event, handlers);
        }

        handlers.add(handler);
    }

    public void notify(String event, Object value) {
        ArrayList<EventHandler> handlers = events.get(event);
        if (handlers == null) return;

        for (EventHandler h : handlers) {
            h.onEvent(value);
        }
    }
}
