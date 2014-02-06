package com.artemis.systems.event;

import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * WARNING: This is currently very buggy. Use at own risk.
 * 
 * A special type of event system. That ensures fast delivery
 * of events. When an event is added to the buffer, it is
 * automatically added to the currentEvents. This avoids having
 * to wait a cycle to process most current events. The downside of
 * using this system is that the sending and the retrieving of
 * the events is slightly slower.
 * 
 * Since the systems are processed in sequence, if an event gets
 * added to the event system that another system cares about but
 * has already been processed in the current cycle, we ensure
 * that that system still receives the event, but all other
 * systems that have already processed said event don't.
 * 
 * @author apotapov
 *
 */
public class FastDeliveryEventSystem extends BasicEventSystem {

    /**
     * A map of polling entity systems and the last event id
     * that the systems processed. Allows to make sure we don't
     * send duplicate events to the systems.
     */
    protected ObjectIntMap<EntitySystem> lastPolledEvents;

    /**
     * Secondary buffer to make sure no events get cleaned out prematurely.
     */
    protected ObjectMap<Class<? extends SystemEvent>, Array<SystemEvent>> secondaryBuffer;

    public FastDeliveryEventSystem() {
        this.lastPolledEvents = new ObjectIntMap<EntitySystem>();
        this.secondaryBuffer = new ObjectMap<Class<? extends SystemEvent>, Array<SystemEvent>>();
    }

    /**
     * Adds the specified event from the sender to the queue to be processed
     * EntitySystems that care.
     */
    @Override
    public void postEvent(EntitySystem sender, SystemEvent event) {

        // update event with tracking information
        event.eventId = currentEventId++;
        event.sender = sender;

        // make sure there is a queue for this event
        Class<? extends SystemEvent> type = event.getClass();
        Array<SystemEvent> listQueue;
        if (currentEvents.containsKey(type)) {
            listQueue = currentEvents.get(type);
        } else {
            listQueue = new Array<SystemEvent>();
            currentEvents.put(type, listQueue);
        }

        // add event to the queue
        listQueue.add(event);
    }

    /**
     * When retrieving an event, make sure that only events that haven't been
     * processed by the system get returned.
     */
    @Override
    public <T extends SystemEvent> void getEvents(EntitySystem pollingSystem, Class<T> type, Array<T> events) {
        synchronized (currentEvents) {

            if (currentEvents.containsKey(type)) {

                // get the highest event id processed by this system
                int lastPolledEvent = lastPolledEvents.get(pollingSystem, -1);

                // keep track of the highest event id processed this time
                // (keeping this separate just in case the events might not be in order)
                int highestPolledEvent = 0;

                for (SystemEvent event : currentEvents.get(type)) {
                    // only add events if their id is higher than the last time we polled
                    if (!event.handled && !events.contains(type.cast(event), false) && event.eventId > lastPolledEvent) {
                        events.add(type.cast(event));

                        // update the current highest polled event
                        if (event.eventId > highestPolledEvent) {
                            highestPolledEvent = event.eventId;
                        }
                    }
                }

                // update the last polled event with the highest this round
                lastPolledEvents.put(pollingSystem, highestPolledEvent);
            }
        }
    }

    /**
     * Since current events are always stored in currentEvents. We need to make sure
     * that they all get consumed correctly and not freed prematurely. The double
     * buffer here takes care of that. We use the second buffer to know which elements,
     * of the currentEvents is ready to be freed up. The elements in the secondary buffer
     * should have lived for at least two world.process() calls and hence should be safe
     * to delete.
     */
    @Override
    protected void processSystem() {
        synchronized (currentEvents) {

            for (ObjectMap.Entry<Class<? extends SystemEvent>, Array<SystemEvent>> entry : secondaryBuffer.entries()) {
                Array<SystemEvent> currentQueue = currentEvents.get(entry.key);
                if (currentQueue != null) {
                    for (SystemEvent bufferedEvent : entry.value) {
                        currentQueue.removeValue(bufferedEvent, true);
                    }
                }
                SystemEvent.free(entry.value);
                entry.value.clear();
            }

            transferEvents(buffer, secondaryBuffer);

            for (Array<SystemEvent> events : buffer.values()) {
                events.clear();
            }

            // transfer from buffer to current events
            transferEvents(currentEvents, buffer);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        lastPolledEvents.clear();
    }
}
