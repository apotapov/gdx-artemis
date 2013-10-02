package com.artemis.managers;

import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;


/**
 * If you need to tag any entity, use this. A typical usage would be to tag
 * entities such as "PLAYER", "BOSS" or something that is very unique.
 * 
 * @author Arni Arent
 *
 */
public class TagManager extends Manager {
    protected ObjectMap<String, Entity> entitiesByTag;
    protected ObjectMap<Entity, String> tagsByEntity;

    public TagManager() {
        entitiesByTag = new ObjectMap<String, Entity>();
        tagsByEntity = new ObjectMap<Entity, String>();
    }

    public void register(String tag, Entity e) {
        entitiesByTag.put(tag, e);
        tagsByEntity.put(e, tag);
    }

    public void unregister(String tag) {
        tagsByEntity.remove(entitiesByTag.remove(tag));
    }

    public boolean isRegistered(String tag) {
        return entitiesByTag.containsKey(tag);
    }

    public Entity getEntity(String tag) {
        return entitiesByTag.get(tag);
    }

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
}
