package com.artemis.systems.event;

import com.artemis.Entity;
import com.artemis.Filter;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;

/**
 * System for processing a single event. Reduces boiler plate for event
 * processing systems.
 * 
 * @author apotapov
 *
 * @param <T> Event that this system processes.
 */
public abstract class EventProcessingSystem<T extends SystemEvent> extends EntitySystem {

    Array<T> events;
    Class<T> eventType;

    /**
     * Constructs an event system
     * @param filter Filter for this system.
     * @param eventType Class of event this system will process.
     */
    public EventProcessingSystem(Filter filter, Class<T> eventType) {
        super(filter);
        this.eventType = eventType;
        this.events = new Array<T>();
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
    }

    /**
     * Processes the event with respect to the specified entity.
     * 
     * @param e Entity to process
     * @param event Event to process
     */
    protected abstract void processEvent(Entity e, T event);
}
