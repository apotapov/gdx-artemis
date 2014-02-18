package com.artemis.systems.event;

import com.artemis.systems.EntitySystem;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

/**
 * Basic implementation of event system. All the events that are
 * sent in gets put into a buffer. During the processing cycle, the
 * buffered events get transfered to the currentEvents list where
 * they can be consumed when interested parties call getEvents.
 * CurrentEvents gets cleared during every processing cycle.
 * If there are systems that need events to last longer than a single
 * processing cycle, use a different EventSystem such as DedicatedQueueEventSystem.
 * 
 * @author apotapov
 *
 */
public class BasicEventDeliverySystem extends VoidEntitySystem implements EventDeliverySystem {

    /**
     * Event buffer by type of event, that all new events get loaded into.
     */
    protected ObjectMap<Class<? extends SystemEvent>, Array<SystemEvent>> buffer;

    /**
     * After each process cycle the events from the buffer get transferred
     * to currentEvents to be consumed by the systems that call getEvents().
     */
    protected ObjectMap<Class<? extends SystemEvent>, Array<SystemEvent>> currentEvents;

    /**
     * Used to generate unique event identifiers.
     */
    protected int currentEventId;

    /**
     * Default constructor.
     */
    public BasicEventDeliverySystem() {
        this.buffer = new ObjectMap<Class<? extends SystemEvent>, Array<SystemEvent>>();
        this.currentEvents = new ObjectMap<Class<? extends SystemEvent>, Array<SystemEvent>>();
    }

    /**
     * Adds the specified event from the sender to the queue to be processed
     * EntitySystems that care.
     */
    @Override
    public void postEvent(EntitySystem sender, SystemEvent event) {
        synchronized (buffer) {
            //update event with tracking information
            event.eventId = currentEventId++;
            event.sender = sender;

            // get the appropriate buffer queue for the event
            Class<? extends SystemEvent> type = event.getClass();
            Array<SystemEvent> bufferQueue;
            if (buffer.containsKey(type)) {
                bufferQueue = buffer.get(type);
            } else {
                bufferQueue = new Array<SystemEvent>();
                buffer.put(type, bufferQueue);
            }

            // add to the queue
            bufferQueue.add(event);
        }
    }

    /**
     * Retrieves events of specific type and adds them to the events Set.
     */
    @Override
    public <T extends SystemEvent> void getEvents(EntitySystem pollingSystem, Class<T> type, Array<T> events) {
        if (currentEvents.containsKey(type)) {
            for (SystemEvent event : currentEvents.get(type)) {
                if (!event.handled && !events.contains(type.cast(event), false)) {
                    events.add(type.cast(event));
                }
            }
        }
    }

    /**
     * When system is processed, transfer all events from the
     * buffer to currentEvents list.
     */
    @Override
    protected void processSystem() {
        synchronized (buffer) {
            // clear out all the existing events
            for (Array<SystemEvent> queue : currentEvents.values()) {
                SystemEvent.free(queue);
                queue.clear();
            }
            // transfer from buffer to current events
            transferEvents(buffer, currentEvents);

            // clear out the buffer
            clearBuffer();
        }
    }

    /**
     * Helper method to clears the buffer.
     */
    protected void clearBuffer() {
        for (Array<SystemEvent> events : buffer.values()) {
            events.clear();
        }
    }

    /**
     * Helper method to transer events from one object map to another.
     * @param from Map to transfer from.
     * @param to Map to transfer to.
     */
    protected static void transferEvents(ObjectMap<Class<? extends SystemEvent>, Array<SystemEvent>> from,
            ObjectMap<Class<? extends SystemEvent>, Array<SystemEvent>> to) {

        for (Entry<Class<? extends SystemEvent>, Array<SystemEvent>> entry : from.entries()) {

            // ensure that the "to" map has the required event queues
            Array<SystemEvent> queue;
            if (to.containsKey(entry.key)) {
                queue = to.get(entry.key);
            } else {
                queue = new Array<SystemEvent>();
                to.put(entry.key, queue);
            }
            queue.addAll(entry.value);
        }
    }

    @Override
    public void dispose() {
        for (Array<SystemEvent> events : buffer.values()) {
            SystemEvent.free(events);
            events.clear();
        }
        buffer.clear();

        for (Array<SystemEvent> events : currentEvents.values()) {
            SystemEvent.free(events);
            events.clear();
        }
        currentEvents.clear();
    }
}
