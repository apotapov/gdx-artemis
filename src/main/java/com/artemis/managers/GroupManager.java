package com.artemis.managers;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

/**
 * If you need to group your entities together, e.g. tanks going into "units" group or explosions into "effects",
 * then use this manager. You must retrieve it using world instance.
 * 
 * A entity can be assigned to more than one group.
 * 
 * @author Arni Arent
 *
 */
public class GroupManager extends Manager {

    private static final Array<Entity> DUMMY_EMPTY_ENTITY_ARRAY = new Array<Entity>();
    private static final Array<String> DUMMY_EMPTY_STRING_ARRAY = new Array<String>();

    protected ObjectMap<String, Array<Entity>> entitiesByGroup;
    protected ObjectMap<Entity, Array<String>> groupsByEntity;

    protected Pool<Array<Entity>> entityArrayPool;
    protected Pool<Array<String>> stringArrayPool;

    public GroupManager() {
        entitiesByGroup = new ObjectMap<String, Array<Entity>>();
        groupsByEntity = new ObjectMap<Entity, Array<String>>();

        entityArrayPool = new Pool<Array<Entity>>() {
            @Override
            protected Array<Entity> newObject() {
                return new Array<Entity>();
            }
        };

        stringArrayPool = new Pool<Array<String>>() {
            @Override
            protected Array<String> newObject() {
                return new Array<String>();
            }
        };
    }

    /**
     * Set the group of the entity.
     * 
     * @param group group to add the entity into.
     * @param e entity to add into the group.
     */
    public void add(Entity e, String group) {
        Array<Entity> entities = entitiesByGroup.get(group);
        if(entities == null) {
            entities = entityArrayPool.obtain();
            entitiesByGroup.put(group, entities);
        }
        if (!entities.contains(e, true)) {
            entities.add(e);
        }

        Array<String> groups = groupsByEntity.get(e);
        if(groups == null) {
            groups = stringArrayPool.obtain();
            groupsByEntity.put(e, groups);
        }
        if (!groups.contains(group, false)) {
            groups.add(group);
        }
    }

    /**
     * Remove the entity from the specified group.
     * @param e
     * @param group
     */
    public void remove(Entity e, String group) {
        Array<Entity> entities = entitiesByGroup.get(group);
        if(entities != null) {
            entities.removeValue(e, true);
            if(entities.size == 0) {
                entityArrayPool.free(entitiesByGroup.remove(group));
            }
        }

        Array<String> groups = groupsByEntity.get(e);
        if(groups != null) {
            groups.removeValue(group, true);
            if (groups.size == 0) {
                stringArrayPool.free(groupsByEntity.remove(e));
            }
        }
    }

    public void removeFromAllGroups(Entity e) {
        Array<String> groups = groupsByEntity.get(e);
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
        Array<String> removedArray = groupsByEntity.remove(e);
        if (removedArray != null) {
            stringArrayPool.free(removedArray);
        }
    }

    /**
     * Get all entities that belong to the provided group.
     * @param group name of the group.
     * @return read-only Array of entities belonging to the group.
     */
    public Array<Entity> getEntities(String group) {
        if (entitiesByGroup.containsKey(group)) {
            return entitiesByGroup.get(group);
        } else {
            DUMMY_EMPTY_ENTITY_ARRAY.clear();
            return DUMMY_EMPTY_ENTITY_ARRAY;
        }
    }

    /**
     * @param e entity
     * @return the groups the entity belongs to, null if none.
     */
    public Array<String> getGroups(Entity e) {
        if (groupsByEntity.containsKey(e)) {
            return groupsByEntity.get(e);
        } else {
            DUMMY_EMPTY_STRING_ARRAY.clear();
            return DUMMY_EMPTY_STRING_ARRAY;
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
    public boolean isInGroup(Entity e, String group) {
        if(group != null) {
            Array<String> groups = groupsByEntity.get(e);
            if (groups != null) {
                for(int i = 0; groups.size > i; i++) {
                    String g = groups.get(i);
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
        stringArrayPool.clear();
    }

}
