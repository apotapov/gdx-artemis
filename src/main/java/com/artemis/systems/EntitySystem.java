package com.artemis.systems;

import com.artemis.Entity;
import com.artemis.EntityObserver;
import com.artemis.Filter;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectIntMap;

/**
 * The most raw entity system. It should not typically be used, but you
 * can create your own entity system handling by extending this.
 * It is recommended that you use the other provided entity system
 * implementations.
 * 
 * @author Arni Arent
 *
 */
public abstract class EntitySystem implements EntityObserver {
    protected final int systemIndex;

    protected World world;

    protected Array<Entity> actives;

    protected Filter filter;

    protected boolean passive;

    protected boolean dummySystem;

    /**
     * Creates an entity system that uses the specified filter
     * as a matcher against entities.
     * 
     * @param filter to match against entities
     */
    public EntitySystem(Filter filter) {
        actives = new Array<Entity>();
        this.filter = filter;
        systemIndex = SystemIndexManager.getIndexFor(this.getClass());

        // This system can't possibly be interested in any entity,
        // so it must be "dummy system"
        dummySystem = filter.allSet.isEmpty() && filter.anySet.isEmpty();
    }

    /**
     * Called before processing of entities begins.
     */
    protected void begin() {
    }

    /**
     * Process all entities that are targeted by this system.
     */
    public final void process() {
        if(checkProcessing()) {
            begin();
            processEntities(actives);
            end();
        }
    }

    /**
     * Called after the processing of entities ends.
     */
    protected void end() {
    }

    /**
     * Any implementing entity system must implement this method and the logic
     * to process the given entities of the system.
     * 
     * @param entities the entities this system contains.
     */
    protected abstract void processEntities(Array<Entity> entities);

    /**
     * 
     * @return true if the system should be processed, false if not.
     */
    protected boolean checkProcessing() {
        return true;
    }

    /**
     * Override to implement code that gets executed when systems are initialized.
     */
    public void initialize() {};

    /**
     * Called if the system has received a entity it is interested in,
     * e.g. created or a component was added to it.
     * 
     * @param e the entity that was added to this system.
     */
    protected void inserted(Entity e) {};

    /**
     * Called if a entity was removed from this system, e.g. deleted
     * or had one of it's components removed.
     * 
     * @param e the entity that was removed from this system.
     */
    protected void removed(Entity e) {};

    /**
     * Will check if the entity is of interest to this system.
     * @param e entity to check
     */
    protected final void check(Entity e) {
        if(dummySystem) {
            return;
        }

        boolean contains = e.getSystemBits().get(systemIndex);
        boolean interested = true; // possibly interested, let's try to prove it wrong.

        Bits componentBits = e.getComponentBits();

        // Check if the entity possesses ALL of the components defined in the filter.
        if(!filter.allSet.isEmpty()) {
            for (int i = filter.allSet.nextSetBit(0); i >= 0; i = filter.allSet.nextSetBit(i+1)) {
                if(!componentBits.get(i)) {
                    interested = false;
                    break;
                }
            }
        }

        // Check if the entity possesses ANY of the components in the anySet.
        // If so, the system is interested.
        if(interested && !filter.anySet.isEmpty()) {
            interested = filter.anySet.intersects(componentBits);
        }

        // Check if the entity possesses ANY of the exclusion components,
        // if it does then the system is not interested.
        if(interested && !filter.exclusionSet.isEmpty()) {
            interested = !filter.exclusionSet.intersects(componentBits);
        }

        if (interested && !contains) {
            insertToSystem(e);
        } else if (!interested && contains) {
            removeFromSystem(e);
        }
    }

    /**
     * Remove entity from the system.
     * 
     * @param e Entity to remove.
     */
    protected void removeFromSystem(Entity e) {
        actives.removeValue(e, true);
        e.getSystemBits().clear(systemIndex);
        removed(e);
    }

    /**
     * Inserts entity into the system.
     * 
     * @param e Entity to insert.
     */
    protected void insertToSystem(Entity e) {
        actives.add(e);
        e.getSystemBits().set(systemIndex);
        inserted(e);
    }


    @Override
    public final void added(Entity e) {
        check(e);
    }

    @Override
    public final void changed(Entity e) {
        check(e);
    }

    @Override
    public final void deleted(Entity e) {
        if(e.getSystemBits().get(systemIndex)) {
            removeFromSystem(e);
        }
    }

    @Override
    public final void disabled(Entity e) {
        if(e.getSystemBits().get(systemIndex)) {
            removeFromSystem(e);
        }
    }

    @Override
    public final void enabled(Entity e) {
        check(e);
    }


    public final void setWorld(World world) {
        this.world = world;
    }

    public boolean isPassive() {
        return passive;
    }

    public void setPassive(boolean passive) {
        this.passive = passive;
    }

    public Array<Entity> getActives() {
        return actives;
    }



    /**
     * Used to generate a unique bit for each system.
     * Only used internally in EntitySystem.
     */
    protected static class SystemIndexManager {
        protected static int INDEX = 0;
        protected static ObjectIntMap<Class<? extends EntitySystem>> indices =
                new ObjectIntMap<Class<? extends EntitySystem>>();

        protected static int getIndexFor(Class<? extends EntitySystem> es){
            int index;
            if (!indices.containsKey(es)) {
                index = INDEX++;
                indices.put(es, index);
            } else {
                index = indices.get(es, -1);
            }
            return index;
        }
    }

}
