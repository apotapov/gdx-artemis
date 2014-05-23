package com.artemis.managers;

import java.util.BitSet;

import com.artemis.Entity;
import com.artemis.utils.IdentifierPool;
import com.artemis.utils.SafeArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * A class that is responsible for managing the life cycle of entities.
 * Used for internal purposes. Should not be accessed directly.
 *
 */
public class EntityManager extends Manager {
    public Array<Entity> entities;
    public Array<Entity> deletedEntities;
    public BitSet disabled;

    public int active;
    public long added;
    public long created;
    public long deleted;

    protected IdentifierPool identifierPool;
    protected Pool<Entity> entityPool;

    public EntityManager() {
        entities = new SafeArray<Entity>();
        deletedEntities = new Array<Entity>();
        disabled = new BitSet();
        identifierPool = new IdentifierPool();
        entityPool = new Pool<Entity>() {

            @Override
            protected Entity newObject() {
                return new Entity(world, identifierPool.checkOut());
            }

            @Override
            public Entity obtain() {
                Entity entity = super.obtain();
                return entity;
            }

            @Override
            public void free (Entity entity) {
                if (entity != null) {
                    identifierPool.checkIn(entity.id);
                    super.free(entity);
                }
            }

            @Override
            public void freeAll (Array<Entity> entities) {
                for (Entity entity : entities) {
                    if (entity != null) {
                        identifierPool.checkIn(entity.id);
                    }
                }
                super.freeAll(entities);
            }

        };
    }

    /**
     * @return Returns an instance of an entity.
     */
    public Entity createEntityInstance() {
        created++;
        return entityPool.obtain();
    }

    @Override
    public void added(Entity e) {
        active++;
        added++;
        entities.set(e.id, e);
    }

    @Override
    public void enabled(Entity e) {
        disabled.clear(e.id);
    }

    @Override
    public void disabled(Entity e) {
        disabled.set(e.id);
    }

    @Override
    public void deleted(Entity e) {
        deletedEntities.add(e);
    }

    /**
     * Cleans up deleted entities.
     */
    public void clean() {
        if(deletedEntities.size > 0) {
            for (Entity e : deletedEntities) {
                entities.set(e.id, null);
                disabled.clear(e.id);
                active--;
                deleted++;
                entityPool.free(e);
            }
            deletedEntities.clear();
        }
    }


    /**
     * Check if this entity is active.
     * Active means the entity is being actively processed.
     * 
     * @param entityId Id of the entity to check.
     * @return true if active, false if not.
     */
    public boolean isActive(int entityId) {
        return entities.get(entityId) != null;
    }

    /**
     * Check if the specified entityId is enabled.
     * 
     * @param entityId Id of the entity to check.
     * @return true if the entity is enabled, false if it is disabled.
     */
    public boolean isEnabled(int entityId) {
        return !disabled.get(entityId);
    }

    /**
     * Get a entity with this id.
     * 
     * @param entityId Id of the entity to return
     * @return Enity of specified id or null if it does not exist.
     */
    public Entity getEntity(int entityId) {
        return entities.get(entityId);
    }

    /**
     * Get how many entities are active in this world.
     * @return how many entities are currently active.
     */
    public int getActiveEntityCount() {
        return active;
    }

    /**
     * Get how many entities have been created in the world since start.
     * Note: A created entity may not have been added to the world, thus
     * created count is always equal or larger than added count.
     * @return how many entities have been created since start.
     */
    public long getTotalCreated() {
        return created;
    }

    /**
     * Get how many entities have been added to the world since start.
     * @return how many entities have been added.
     */
    public long getTotalAdded() {
        return added;
    }

    /**
     * Get how many entities have been deleted from the world since start.
     * @return how many entities have been deleted since start.
     */
    public long getTotalDeleted() {
        return deleted;
    }

    @Override
    public void dispose() {
        entityPool.freeAll(entities);
        entities.clear();
        entityPool.freeAll(deletedEntities);
        deletedEntities.clear();
        disabled.clear();
        active = 0;
        added = 0;
        created = 0;
        deleted = 0;
        identifierPool.dispose();
    }
}
