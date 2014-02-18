package com.artemis.managers;

import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;


/**
 * DEPRECATED Use SingletonEntityManager instead.
 * 
 * The name of this manager is a bit of a misnomer. It is not a standard tag
 * implementation. If you need that, use the GroupManager or GenericGroupManager.
 * 
 * Tag manager allows tagging of a single entity with a specific tag so it
 * can be retrieved. This way it's more similar to the SingletonComponentManager
 * 
 * A typical usage would be to tag entities such as "PLAYER", "BOSS" or
 * something that is very unique.
 * 
 * @author Arni Arent
 *
 */
@Deprecated
public class TagManager extends Manager {
    protected ObjectMap<String, Entity> entitiesByTag;
    protected ObjectMap<Entity, String> tagsByEntity;

    public TagManager() {
        entitiesByTag = new ObjectMap<String, Entity>();
        tagsByEntity = new ObjectMap<Entity, String>();
    }

    /**
     * Tags the specified entity.
     * 
     * @param tag Tag for the entity.
     * @param e entity to tag.
     */
    public void register(String tag, Entity e) {
        entitiesByTag.put(tag, e);
        tagsByEntity.put(e, tag);
    }

    /**
     * Unregisters a specified tag, and removes all entities that belong to it.
     * 
     * @param tag Tag to unregister.
     */
    public void unregister(String tag) {
        tagsByEntity.remove(entitiesByTag.remove(tag));
    }

    /**
     * Checks if the tag is registered.
     * 
     * @param tag Tag to check.
     * @return Whether the tag is registered.
     */
    public boolean isRegistered(String tag) {
        return entitiesByTag.containsKey(tag);
    }

    /**
     * Get the entity a tag belongs to.
     * 
     * @param tag Tag to check
     * @return entity that is tagged.
     */
    public Entity getEntity(String tag) {
        return entitiesByTag.get(tag);
    }

    /**
     * @return Returns a list of registered tags.
     */
    public Values<String> getRegisteredTags() {
        return tagsByEntity.values();
    }

    @Override
    public void deleted(Entity e) {
        String removedTag = tagsByEntity.remove(e);
        if(removedTag != null) {
            entitiesByTag.remove(removedTag);
        }
    }

    @Override
    public void dispose() {
        entitiesByTag.clear();
        tagsByEntity.clear();
    }
}
