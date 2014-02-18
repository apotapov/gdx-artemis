package com.artemis.managers;

import com.artemis.Entity;
import com.artemis.utils.SafeArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

/**
 * Abstract Generic Group Manager allows to group entities together.
 * The group identifier type is generic. If you are planning to use String,
 * consider using an Enum instead. However any other class can be used.
 * 
 * An entity can be added to multiple groups.
 * 
 * Example: Tank entities can be in the "units" group.
 * 
 * The reason this class is abstract is because of type erasure in java
 * makes it impossible to get a class of a generic object. So it would
 * be impossible to call world.getManager(GenericGroupManager<T>.class).
 * 
 * @author apotapov
 *
 * @param <T> Group identifier type.
 */
public abstract class GenericGroupManager<T> extends Manager {

    private static final Array<Entity> DUMMY_EMPTY_ENTITY_ARRAY = new Array<Entity>();
    private final Array<T> DUMMY_EMPTY_GROUP_ARRAY = new Array<T>();

    protected ObjectMap<T, Array<Entity>> entitiesByGroup;
    protected ObjectMap<Entity, Array<T>> groupsByEntity;

    protected Pool<Array<Entity>> entityArrayPool;
    protected Pool<Array<T>> groupArrayPool;

    public GenericGroupManager() {
        entitiesByGroup = new ObjectMap<T, Array<Entity>>();
        groupsByEntity = new ObjectMap<Entity, Array<T>>();

        entityArrayPool = new Pool<Array<Entity>>() {
            @Override
            protected Array<Entity> newObject() {
                return new SafeArray<Entity>();
            }
        };

        groupArrayPool = new Pool<Array<T>>() {
            @Override
            protected Array<T> newObject() {
                return new SafeArray<T>();
            }
        };
    }

    /**
     * Set the group of the entity.
     * 
     * @param group group to add the entity into.
     * @param e entity to add into the group.
     */
    public void add(Entity e, T group) {
        Array<Entity> entities = entitiesByGroup.get(group);
        if(entities == null) {
            entities = entityArrayPool.obtain();
            entitiesByGroup.put(group, entities);
        }
        if (!entities.contains(e, true)) {
            entities.add(e);
        }

        Array<T> groups = groupsByEntity.get(e);
        if(groups == null) {
            groups = groupArrayPool.obtain();
            groupsByEntity.put(e, groups);
        }
        if (!groups.contains(group, false)) {
            groups.add(group);
        }
    }

    /**
     * Remove the entity from the specified group.
     * @param e Entity to be removed
     * @param group Group to remove the enity from.
     */
    public void remove(Entity e, T group) {
        Array<Entity> entities = entitiesByGroup.get(group);
        if(entities != null) {
            entities.removeValue(e, true);
            if(entities.size == 0) {
                entityArrayPool.free(entitiesByGroup.remove(group));
            }
        }

        Array<T> groups = groupsByEntity.get(e);
        if(groups != null) {
            groups.removeValue(group, true);
            if (groups.size == 0) {
                groupArrayPool.free(groupsByEntity.remove(e));
            }
        }
    }

    /**
     * Remove entity from all the groups it belongs to.
     * 
     * @param e Entity to remove.
     */
    public void removeFromAllGroups(Entity e) {
        Array<T> groups = groupsByEntity.get(e);
        if(groups != null) {
            for(int i = 0; groups.size > i; i++) {
                Array<Entity> entities = entitiesByGroup.get(groups.get(i));
                if(entities != null) {
                    entities.removeValue(e, true);
                    if(entities.size == 0) {
                        entityArrayPool.free(entitiesByGroup.remove(groups.get(i)));
                    }
                }
            }
            groups.clear();
        }
        Array<T> removedArray = groupsByEntity.remove(e);
        if (removedArray != null) {
            groupArrayPool.free(removedArray);
        }
    }

    /**
     * Get all entities that belong to the provided group.
     * 
     * WARNING: The returned array should not be modified.
     * 
     * @param group name of the group.
     * @return Array of entities belonging to the group.
     */
    public Array<Entity> getEntities(T group) {
        if (entitiesByGroup.containsKey(group)) {
            return entitiesByGroup.get(group);
        } else {
            return DUMMY_EMPTY_ENTITY_ARRAY;
        }
    }

    /**
     * Get groups for a specific entity.
     * 
     * WARNING: The returned array should not be modified.
     * 
     * @param e entity
     * @return the groups the entity belongs to.
     */
    public Array<T> getGroups(Entity e) {
        if (groupsByEntity.containsKey(e)) {
            return groupsByEntity.get(e);
        } else {
            return DUMMY_EMPTY_GROUP_ARRAY;
        }
    }

    /**
     * Checks if the entity belongs to any group.
     * @param e the entity to check.
     * @return true if it is in any group, false if none.
     */
    public boolean isInAnyGroup(Entity e) {
        return getGroups(e).size > 0;
    }

    /**
     * Check if the entity is in the supplied group.
     * @param group the group to check in.
     * @param e the entity to check for.
     * @return true if the entity is in the supplied group, false if not.
     */
    public boolean isInGroup(Entity e, T group) {
        if(group != null) {
            Array<T> groups = groupsByEntity.get(e);
            if (groups != null) {
                for(int i = 0; groups.size > i; i++) {
                    T g = groups.get(i);
                    if(group == g || group.equals(g)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void deleted(Entity e) {
        removeFromAllGroups(e);
    }

    @Override
    public void dispose() {
        entitiesByGroup.clear();
        groupsByEntity.clear();
        entityArrayPool.clear();
        groupArrayPool.clear();
    }

}
