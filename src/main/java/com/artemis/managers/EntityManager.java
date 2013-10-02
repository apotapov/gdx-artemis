package com.artemis.managers;

import java.util.BitSet;

import com.artemis.Entity;
import com.artemis.utils.IdentifierPool;
import com.artemis.utils.SafeArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class EntityManager extends Manager {
    protected Array<Entity> entities;
    protected Array<Entity> deletedEntities;
    protected BitSet disabled;

    protected int active;
    protected long added;
    protected long created;
    protected long deleted;

    protected IdentifierPool identifierPool;
    protected Pool<Entity> entityPool;

    public EntityManager() {
        entities = new SafeArray<Entity>();
        deletedEntities = new SafeArray<Entity>();
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
                entity.setId(identifierPool.checkOut());
                return entity;
            }

            @Override
            public void free (Entity entity) {
                identifierPool.checkIn(entity.getId());
                super.free(entity);
            }

            @Override
            public void freeAll (Array<Entity> entities) {
                for (int i = 0; i < entities.size; i++) {
                    identifierPool.checkIn(entities.get(i).getId());
                }
                super.freeAll(entities);
            }

        };
    }

    public Entity createEntityInstance() {
        created++;
        return entityPool.obtain();
    }

    @Override
    public void added(Entity e) {
        active++;
        added++;
        entities.set(e.getId(), e);
    }

    @Override
    public void enabled(Entity e) {
        disabled.clear(e.getId());
    }

    @Override
    public void disabled(Entity e) {
        disabled.set(e.getId());
    }

    @Override
    public void deleted(Entity e) {
        deletedEntities.add(e);
    }

    public void clean() {
        if(deletedEntities.size > 0) {
            for(int i = 0; deletedEntities.size > i; i++) {
                Entity e = deletedEntities.get(i);
                entities.set(e.getId(), null);
                disabled.clear(e.getId());
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
     * @param entityId
     * @return true if active, false if not.
     */
    public boolean isActive(int entityId) {
        return entities.get(entityId) != null;
    }

    /**
     * Check if the specified entityId is enabled.
     * 
     * @param entityId
     * @return true if the entity is enabled, false if it is disabled.
     */
    public boolean isEnabled(int entityId) {
        return !disabled.get(entityId);
    }

    /**
     * Get a entity with this id.
     * 
     * @param entityId
     * @return the entity
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
}
