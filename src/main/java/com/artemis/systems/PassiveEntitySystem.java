package com.artemis.systems;

/**
 * A passive entity system that does not perform any actions.
 * This is useful for a system that strictly processes events
 * or perhaps an Enity/Component factory system.
 * 
 * @author apotapov
 */
public abstract class PassiveEntitySystem extends VoidEntitySystem {

    public PassiveEntitySystem() {
        super.setPassive(true);
    }

    @Override
    protected final void processSystem() {
    }
}
