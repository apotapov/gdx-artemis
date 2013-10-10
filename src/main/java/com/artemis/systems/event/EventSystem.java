package com.artemis.systems.event;

import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.ObjectSet;

/**
 * Interface for event systems to adhere to.
 * 
 * @author apotapov
 */
public interface EventSystem {
    /**
     * Post an event to people who care.
     * 
     * @param sender The sender of the event
     * @param event The event to send.
     */
    public void postEvent(EntitySystem sender, SystemEvent event);

    /**
     * Method used to poll the event system for any posted events and add them to fillArray
     * 
     * @param pollingSystem System that is polling system.
     * @param eventType Type of event to get.
     * @param events Set that the posted events will be loaded into.
     */
    public <T extends SystemEvent> void getEvents(EntitySystem pollingSystem, Class<T> eventType, ObjectSet<T> events);
}
