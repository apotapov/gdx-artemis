package com.artemis.systems;

import com.artemis.Entity;
import com.artemis.Filter;

/**
 * The purpose of this system is to delete an entity after a specified time.
 * 
 * @author apotapov
 *
 */
public final class ExpirationEntitySystem extends SkipEntityProcessingSystem {

    /**
     * Will expire the entity after the specified delay.
     * 
     * @param filter Filter for the system.
     * @param delay Delay to execution.
     */
    public ExpirationEntitySystem(Filter filter, float delay) {
        super(filter, delay, false);
    }

    @Override
    protected void processDelayed(Entity e) {
        e.deleteFromWorld();
    }
}
