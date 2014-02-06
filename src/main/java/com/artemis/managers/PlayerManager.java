package com.artemis.managers;

import com.artemis.Entity;
import com.artemis.utils.SafeArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;


/**
 * You may sometimes want to specify to which player an entity belongs to.
 * 
 * An entity can only belong to a single player at a time.
 * 
 * @author Arni Arent
 *
 */
public class PlayerManager extends Manager {
    protected ObjectMap<Entity, String> playerByEntity;
    protected ObjectMap<String, Array<Entity>> entitiesByPlayer;

    public PlayerManager() {
        playerByEntity = new ObjectMap<Entity, String>();
        entitiesByPlayer = new ObjectMap<String, Array<Entity>>();
    }

    /**
     * Adds entity to the specified player.
     * 
     * @param e Entity that belongs to the player.
     * @param player The owner of the entity.
     */
    public void setPlayer(Entity e, String player) {
        playerByEntity.put(e, player);
        Array<Entity> entities = entitiesByPlayer.get(player);
        if(entities == null) {
            entities = new SafeArray<Entity>();
            entitiesByPlayer.put(player, entities);
        }
        entities.add(e);
    }

    /**
     * Returns all entities the belongs to the player.
     * 
     * WARNING: the array should not be modified.
     * 
     * @param player Player to get the entities for.
     * @return An array of entities belonging to the player.
     */
    public Array<Entity> getEntitiesOfPlayer(String player) {
        Array<Entity> entities = entitiesByPlayer.get(player);
        if(entities == null) {
            entities = new SafeArray<Entity>();
        }
        return entities;
    }

    /**
     * Removes the specified entity from the player it belongs to.
     * 
     * @param e Entity to disown.
     */
    public void removeFromPlayer(Entity e) {
        String player = playerByEntity.get(e);
        if(player != null) {
            Array<Entity> entities = entitiesByPlayer.get(player);
            if(entities != null) {
                entities.removeValue(e, true);
            }
        }
    }

    /**
     * Returns the player an entity belongs to.
     * 
     * @param e Entity to check.
     * @return A player that the entity belongs to, or null if none.
     */
    public String getPlayer(Entity e) {
        return playerByEntity.get(e);
    }

    @Override
    public void deleted(Entity e) {
        removeFromPlayer(e);
    }

    @Override
    public void dispose() {
        playerByEntity.clear();
        entitiesByPlayer.clear();
    }

}
