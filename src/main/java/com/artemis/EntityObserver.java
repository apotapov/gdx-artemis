package com.artemis;

/**
 * An observer for changes to the entity's state in the world.
 * 
 */
public interface EntityObserver {

    /**
     * Called when an entity is added to the world.
     * @param e Added entity.
     */
    void added(Entity e);

    /**
     * Called when an entity is changed in the world,
     * when a component is added or removed.
     * 
     * @param e Changed entity.
     */
    void changed(Entity e);

    /**
     * Called when an entity is deleted from the world.
     * 
     * @param e Deleted entity.
     */
    void deleted(Entity e);

    /**
     * Called when an entity is enabled in the world.
     * 
     * @param e Enabled entity.
     */
    void enabled(Entity e);

    /**
     * Called when an entity is disabled in the world.
     * 
     * @param e Disabled entity.
     */
    void disabled(Entity e);

}
