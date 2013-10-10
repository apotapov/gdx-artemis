package com.artemis.systems.event;

import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

/**
 * A special type of event system that creates a dedicated
 * event queue for each subscriber. This allows systems to poll
 * events at a larger gaps than every processing cycle, and at
 * different rates compared to other systems.
 * 
 * @author apotapov
 *
 */
public class DedicatedQueueEventSystem extends BasicEventSystem {

    /**
     * Queues for each individual subscriber.
     */
    public ObjectMap<EntitySystem, ObjectMap<Class<? extends SystemEvent>, Array<SystemEvent>>> subscriberQueues;


    public DedicatedQueueEventSystem() {
        subscriberQueues = new ObjectMap<EntitySystem, ObjectMap<Class<? extends SystemEvent>, Array<SystemEvent>>>();
    }

    /**
     * Register the system with this event system.
     * @param system System to register.
     */
    public void registerSubscriber(EntitySystem system) {
        if (!subscriberQueues.containsKey(system)) {

            // Hopefully register/unregister will not happen very often
            // so i'm not gonna bother pooling here.
            subscriberQueues.put(system, new ObjectMap<Class<? extends SystemEvent>, Array<SystemEvent>>());
        }
    }

    /**
     * Unregister the system with this event system.
     * @param system System to unregister.
     */
    public void unregisterSubscriber(EntitySystem system) {
        if (subscriberQueues.containsKey(system)) {
            subscriberQueues.remove(system);
        }
    }

    /**
     * Get events for the specific polling system.
     * The caller is then responsible for freeing the events after processing.
     */
    @Override
    public <T extends SystemEvent> void getEvents(EntitySystem pollingSystem, Class<T> type, ObjectSet<T> events) {
        if (subscriberQueues.containsKey(pollingSystem)) {
            currentEvents = subscriberQueues.get(pollingSystem);
            super.getEvents(pollingSystem, type, events);
        }
    }

    /**
     * Method to clear all the events for a specific pollingSystem
     * 
     * @param pollingSystem System to clear the events for.
     */
    public void freeEvents(EntitySystem pollingSystem) {
        if (subscriberQueues.containsKey(pollingSystem)) {
            currentEvents = subscriberQueues.get(pollingSystem);
            for (Array<SystemEvent> events : currentEvents.values()) {
                SystemEvent.free(events);
                events.clear();
            }
        }
    }

    /**
     * Transfers the buffer to the subscriber queues, but does not
     * clear the subscriber queues.
     */
    @Override
    protected void processSystem() {
        synchronized (buffer) {
            for (ObjectMap<Class<? extends SystemEvent>, Array<SystemEvent>> subscriberQueue : subscriberQueues.values()) {
                transferEvents(buffer, subscriberQueue);
            }
            clearBuffer();
        }
    }
}
