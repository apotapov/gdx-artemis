package com.artemis.systems;

import com.artemis.Entity;
import com.artemis.Filter;
import com.artemis.utils.Timer;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

/**
 * The purpose of this class is to allow systems to execute at varying intervals.
 * 
 * An example system would be an ExpirationSystem, that deletes entities after
 * a certain lifetime. Another example system would be an AnimationSystem.
 * You know when you have to animate a certain entity, e.g. in 300 milliseconds.
 * So you can set the system to run in 300 ms. to perform the animation. This
 * will save CPU cycles in some scenarios.
 * 
 * Note, this system stores Entity state and thus is not true to the Entity
 * System priciples. This was done to avoid storing extra information on
 * Entities whether in form of components or otherwise. Implementing an
 * EntitySystem in this manner should generally be avoided.
 * 
 * @author apotapov
 *
 */
public abstract class SkipEntityProcessingSystem extends EntityProcessingSystem {

    /**
     * Timer that keeps track of a delay for each entity and executes
     * processDelayed(e) when a delay expires.
     * 
     * @author apotapov
     *
     */
    private class SkipTimer extends Timer {

        Entity e;

        public SkipTimer(float delay, boolean repeat) {
            super(delay, repeat);
        }

        @Override
        public void execute() {
            processDelayed(e);
        }

        @Override
        public void reset() {
            super.reset();
            e = null;
        }
    }

    // Entities and their associated timers.
    ObjectMap<Entity, SkipTimer> timerMap;

    Pool<SkipTimer> timerPool;

    /**
     * Creates a system with a specified delay. Does not repeat the action.
     * 
     * @param filter Filter for the system.
     * @param delay Delay to execution.
     */
    public SkipEntityProcessingSystem(Filter filter, float delay) {
        this(filter, delay, false);
    }

    /**
     * Creates a system with a specified delay and whether the action is repeatable.
     * 
     * @param filter Filter for the system.
     * @param delay Delay for execution.
     * @param repeat Whether the action should be repeated.
     */
    public SkipEntityProcessingSystem(Filter filter, final float delay, final boolean repeat) {
        super(filter);
        timerMap = new ObjectMap<Entity, SkipTimer>();

        timerPool = new Pool<SkipTimer>() {

            @Override
            protected SkipTimer newObject() {
                return new SkipTimer(delay, repeat);
            }

        };
    }

    protected abstract void processDelayed(Entity e);

    @Override
    protected final void process(Entity e) {
        Timer entityTimer = timerMap.get(e);
        entityTimer.update(world.getDelta());
    }

    @Override
    protected void inserted(Entity e) {
        if (timerMap.containsKey(e)) {
            timerPool.free(timerMap.remove(e));
        }
        SkipTimer timer = timerPool.obtain();
        timer.e = e;
        timerMap.put(e, timer);
    }

    @Override
    protected void removed(Entity e) {
        if (timerMap.containsKey(e)) {
            timerPool.free(timerMap.remove(e));
        }
    }
}
