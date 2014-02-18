package com.artemis.managers;

import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectMap.Keys;


/**
 * This Manager helps manage singleton entities such as Player or Boss.
 * An entity can be assigned a name and use that name to be looked up.
 * 
 * An entity can only be stored under one name.
 * 
 * @author apotapov
 *
 */
public class SingletonEntityManager extends Manager {
    protected ObjectMap<String, Entity> entitiesByName;

    public SingletonEntityManager() {
        entitiesByName = new ObjectMap<String, Entity>();
    }

    /**
     * Names the specified entity.
     * 
     * @param name Name for the entity.
     * @param e entity to name.
     */
    public void setName(Entity e, String name) {
        remove(e);
        entitiesByName.put(name, e);
    }

    /**
     * Unregisters a specified name, and removes the entity that it belongs to.
     * 
     * @param name Name to unregister.
     */
    public void remove(String name) {
        entitiesByName.remove(name);
    }

    /**
     * Removes specified entity.
     * 
     * @param e Entity to remove.
     */
    public void remove(Entity e) {
        for (Entry<String, Entity> entry : entitiesByName.entries()) {
            if (entry.value.equals(e)) {
                entitiesByName.remove(entry.key);
                break;
            }
        }
    }

    /**
     * Checks if the name is registered.
     * 
     * @param name Name to check.
     * @return Whether the name is registered.
     */
    public boolean isSet(String name) {
        return entitiesByName.containsKey(name);
    }

    /**
     * Get the entity by name.
     * 
     * @param name Name of the entity
     * @return Named entity.
     */
    public Entity getEntity(String name) {
        return entitiesByName.get(name);
    }

    /**
     * @return Returns a list of entity names.
     */
    public Keys<String> getNames() {
        return entitiesByName.keys();
    }

    @Override
    public void deleted(Entity e) {
        remove(e);
    }

    @Override
    public void dispose() {
        entitiesByName.clear();
    }
}
