package com.artemis.systems.event;

import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

/**
 * Abstract class all events should extend.
 * 
 * @author apotapov
 *
 */
public abstract class SystemEvent implements Poolable {

    /**
     * Event tracking information.
     */
    public int eventId;
    public EntitySystem sender;

    /**
     * Called when an event is rolled back into the pool.
     */
    @Override
    public final void reset() {
        eventId = -1;
        sender = null;
        resetForPooling();
    }

    /**
     * Additional reset logic for specific events.
     */
    protected abstract void resetForPooling();

    /**
     * Factory method to create an event of a specified type. Uses pooling.
     * 
     * @param type Type of event to create.
     * @return Pooled event.
     */
    public static <T extends SystemEvent> T createEvent(Class<T> type) {
        return Pools.obtain(type);
    }

    /**
     * Helper method to free an event after it's processed.
     * 
     * @param event Event to free.
     */
    public static void free(SystemEvent event) {
        Pools.free(event);
    }

    /**
     * Helper method to free an array events after they are processed.
     * 
     * @param events Events to free.
     */
    public static void free(Array<SystemEvent> events) {
        Pools.freeAll(events);
    }

    /**
     * Used to make sure no duplicate events.
     */
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof SystemEvent) {
            return eventId == ((SystemEvent)o).eventId;
        }
        return false;
    }

    /**
     * Hashcode is based on unique event id.
     */
    @Override
    public int hashCode() {
        return eventId;
    }
}
