package com.artemis.systems.event;

import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.utils.Array;

/**
 * Void Entity System for processing a single event. Reduces boiler plate for event
 * processing systems.
 * 
 * @author apotapov
 *
 * @param <T> Event that this system processes.
 */
public abstract class EventVoidSystem<T extends SystemEvent> extends VoidEntitySystem {

    Array<T> events;
    Class<T> eventType;

    /**
     * Constructs an event system
     * @param eventType Class of event this system will process.
     */
    public EventVoidSystem(Class<T> eventType) {
        this.eventType = eventType;
        this.events = new Array<T>();
    }

    @Override
    public final void processSystem() {
        world.getEvents(this, eventType, events);
        for (T event : events) {
            processEvent(event);
        }
    }

    /**
     * Processes the event.
     * 
     * @param event Event to process
     */
    protected abstract void processEvent(T event);
}
