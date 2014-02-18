package com.artemis.systems.event;

import com.artemis.Entity;
import com.artemis.Filter;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;

/**
 * System for processing two events. Reduces boiler plate for event
 * processing systems.
 * 
 * @author apotapov
 *
 * @param <T> First event that this system processes.
 * @param <U> Second event that this system processes.
 */
public abstract class EventProcessingSystem2<T extends SystemEvent, U extends SystemEvent> extends EntitySystem {

    Array<T> events;
    Class<T> eventType;

    Array<U> events2;
    Class<U> eventType2;

    /**
     * Constructs an event system
     * @param filter Filter for this system.
     * @param eventType Class of the first event this system will process.
     * @param eventType2 Class of the second event this system will process.
     */
    public EventProcessingSystem2(Filter filter, Class<T> eventType, Class<U> eventType2) {
        super(filter);

        this.eventType = eventType;
        this.events = new Array<T>();

        this.eventType2 = eventType2;
        this.events2 = new Array<U>();
    }

    /**
     * For each event and entity processes them as a pair.
     */
    @Override
    protected final void processEntities(Array<Entity> entities) {
        world.getEvents(this, eventType, events);
        for (T event : events) {
            for (Entity e : entities) {
                processEvent(e, event);
            }
        }

        world.getEvents(this, eventType2, events2);
        for (U event : events2) {
            for (Entity e : entities) {
                processEvent2(e, event);
            }
        }
    }

    /**
     * Processes the event with respect to the specified entity.
     * 
     * @param e Entity to process
     * @param event Event to process
     */
    protected abstract void processEvent(Entity e, T event);

    /**
     * Processes the second event with respect to the specified entity.
     * 
     * @param e Entity to process
     * @param event Event to process
     */
    protected abstract void processEvent2(Entity e, U event);
}
