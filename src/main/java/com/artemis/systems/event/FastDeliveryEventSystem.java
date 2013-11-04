package com.artemis.systems.event;

import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;

/**
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

    public FastDeliveryEventSystem() {
        this.lastPolledEvents = new ObjectIntMap<EntitySystem>();
    }

    /**
     * After adding the event to the buffer, add it to the
     * currentEvents as well.
     */
    @Override
    protected void postPost(SystemEvent event) {

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
        if (currentEvents.containsKey(type)) {

            // get the highest event id processed by this system
            int lastPolledEvent = lastPolledEvents.get(pollingSystem, -1);

            // keep track of the highest event id processed this time
            // (keeping this separate just in case the events might not be in order)
            int highestPolledEvent = 0;

            for (SystemEvent event : currentEvents.get(type)) {
                // only add events if their id is higher than the last time we polled
                if (!event.handled && events.contains(type.cast(event), false) && event.eventId > lastPolledEvent) {
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

    @Override
    public void dispose() {
        super.dispose();
        lastPolledEvents.clear();
    }
}
