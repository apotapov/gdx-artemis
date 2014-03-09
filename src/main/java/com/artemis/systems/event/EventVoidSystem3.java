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
public abstract class EventVoidSystem3<T extends SystemEvent, U extends SystemEvent, V extends SystemEvent> extends VoidEntitySystem {

    Array<T> events;
    Class<T> eventType;

    Array<U> events2;
    Class<U> eventType2;

    Array<V> events3;
    Class<V> eventType3;

    /**
     * Constructs an event system
     * @param eventType Class of the first event this system will process.
     * @param eventType2 Class of the second event this system will process.
     * @param eventType3 Class of the third event this system will process.
     */
    public EventVoidSystem3(Class<T> eventType, Class<U> eventType2, Class<V> eventType3) {
        this.eventType = eventType;
        this.events = new Array<T>();

        this.eventType2 = eventType2;
        this.events2 = new Array<U>();

        this.eventType3 = eventType3;
        this.events3 = new Array<V>();
    }

    @Override
    public final void processSystem() {
        world.getEvents(this, eventType, events);
        for (T event : events) {
            processEvent(event);
        }

        world.getEvents(this, eventType2, events2);
        for (U event : events2) {
            processEvent2(event);
        }

        world.getEvents(this, eventType3, events3);
        for (V event : events3) {
            processEvent3(event);
        }
    }

    /**
     * Processes the third event.
     * 
     * @param event Event to process
     */
    protected abstract void processEvent(T event);

    /**
     * Processes the second event.
     * 
     * @param event Event to process
     */
    protected abstract void processEvent2(U event);

    /**
     * Processes the third event.
     * 
     * @param event Event to process
     */
    protected abstract void processEvent3(V event);
}
