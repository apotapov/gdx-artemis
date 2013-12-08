package com.artemis.managers;

import com.artemis.Entity;
import com.artemis.utils.SafeArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

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
    protected ObjectMap<String, Array<Entity>> entitiesByGroup;
    protected ObjectMap<Entity, Array<String>> groupsByEntity;

    public GroupManager() {
        entitiesByGroup = new ObjectMap<String, Array<Entity>>();
        groupsByEntity = new ObjectMap<Entity, Array<String>>();
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
            entities = new SafeArray<Entity>();
            entitiesByGroup.put(group, entities);
        }
        if (!entities.contains(e, true)) {
            entities.add(e);
        }

        Array<String> groups = groupsByEntity.get(e);
        if(groups == null) {
            groups = new SafeArray<String>();
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
        }

        Array<String> groups = groupsByEntity.get(e);
        if(groups != null) {
            groups.removeValue(group, true);
            if (groupsByEntity.size == 0) {
                groupsByEntity.remove(e);
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
                }
            }
            groups.clear();
        }
        groupsByEntity.remove(e);
    }

    /**
     * Get all entities that belong to the provided group.
     * @param group name of the group.
     * @return read-only Array of entities belonging to the group.
     */
    public Array<Entity> getEntities(String group) {
        Array<Entity> entities = entitiesByGroup.get(group);
        if(entities == null) {
            entities = new SafeArray<Entity>();
            entitiesByGroup.put(group, entities);
        }
        return entities;
    }

    /**
     * @param e entity
     * @return the groups the entity belongs to, null if none.
     */
    public Array<String> getGroups(Entity e) {
        return groupsByEntity.get(e);
    }

    /**
     * Checks if the entity belongs to any group.
     * @param e the entity to check.
     * @return true if it is in any group, false if none.
     */
    public boolean isInAnyGroup(Entity e) {
        return getGroups(e) != null;
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
            for(int i = 0; groups.size > i; i++) {
                String g = groups.get(i);
                if(group == g || group.equals(g)) {
                    return true;
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
    }

}
