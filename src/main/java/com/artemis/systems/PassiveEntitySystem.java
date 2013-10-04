package com.artemis.systems;

/**
 * A passive entity system that does not perform any actions. This is
 * useful for things like sound system, entity/component factory.
 * 
 * @author apotapov
 */
public abstract class PassiveEntitySystem extends VoidEntitySystem {

    public PassiveEntitySystem() {
        super.setPassive(true);
    }

    @Override
    public final void setPassive(boolean passive) {
        // do not change the passive status of the system
    }

    @Override
    protected final void processSystem() {
    }

}
