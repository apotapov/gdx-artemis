package com.badlogic.gdx.artemis.systems;

import com.badlogic.gdx.artemis.Aspect;


/**
 * A system that processes entities at a interval in milliseconds.
 * A typical usage would be a collision system or physics system.
 * 
 * @author Arni Arent
 *
 */
public abstract class IntervalEntitySystem extends EntitySystem {
    protected float acc;
    protected float interval;

    public IntervalEntitySystem(Aspect aspect, float interval) {
        super(aspect);
        this.interval = interval;
    }

    @Override
    protected boolean checkProcessing() {
        acc += world.getDelta();
        if(acc >= interval) {
            acc -= interval;
            return true;
        }
        return false;
    }

}
