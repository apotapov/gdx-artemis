package com.artemis;

import com.artemis.managers.ComponentManager;
import com.artemis.managers.EntityManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * The entity class. Cannot be instantiated outside the framework, you must
 * create new entities using World.
 * 
 * @author Arni Arent
 * 
 */
public final class Entity implements Poolable {

    /**
     * The internal id for this entity within the framework. No other entity
     * will have the same ID, but ID's are however reused so another entity may
     * acquire this ID if the previous entity was deleted.
     */
    public int id;
    protected Bits componentBits;
    protected Bits systemBits;

    protected World world;
    protected EntityManager entityManager;
    protected ComponentManager componentManager;

    /**
     * Create an entity for the specified world with the specified id.
     * @param world World this entity belongs to.
     * @param id Entity's id.
     */
    public Entity(World world, int id) {
        this(world);
        this.id = id;
    }
    /**
     * Create an entity for the specified world.
     * @param world World this entity belongs to.
     */
    public Entity(World world) {
        this.world = world;
        this.entityManager = world.getEntityManager();
        this.componentManager = world.getComponentManager();
        systemBits = new Bits();
        componentBits = new Bits();
    }

    /**
     * @return Returns a BitSet instance containing bits of the components the entity possesses.
     * 
     */
    public Bits getComponentBits() {
        return componentBits;
    }

    /**
     * @return Returns a BitSet instance containing bits of the components the entity possesses.
     */
    public Bits getSystemBits() {
        return systemBits;
    }

    /**
     * Make entity ready for re-use.
     * Will generate a new uuid for the entity.
     */
    @Override
    public void reset() {
        systemBits.clear();
        componentBits.clear();
        id = 0;
    }

    @Override
    public String toString() {
        return "Entity[" + id + "]";
    }

    /**
     * Add a component to this entity.
     * 
     * @param component to add to this entity
     * 
     * @return this entity for chaining.
     */
    public Entity addComponent(Component component) {
        componentManager.addComponent(this, component);
        if (isActive()) {
            world.changedEntity(this);
        }
        return this;
    }

    /**
     * Removes the component from this entity.
     * 
     * @param component to remove from this entity.
     * 
     * @return this entity for chaining.
     */
    public Entity removeComponent(Component component) {
        removeComponent(component.getClass());
        return this;
    }

    /**
     * Remove component by its type.
     * @param type Type of component to remove.
     * 
     * @return this entity for chaining.
     */
    public Entity removeComponent(Class<? extends Component> type) {
        componentManager.removeComponent(this, type);
        if (isActive()) {
            world.changedEntity(this);
        }
        return this;
    }

    /**
     * Checks if the entity has been added to the world and has not been deleted from it.
     * If the entity has been disabled this will still return true.
     * 
     * @return if it's active.
     */
    public boolean isActive() {
        return entityManager.entities.get(id) != null;
    }

    /**
     * Will check if the entity is enabled in the world.
     * By default all entities that are added to world are enabled,
     * this will only return false if an entity has been explicitly disabled.
     * 
     * @return if it's enabled
     */
    public boolean isEnabled() {
        return entityManager.isEnabled(id);
    }

    /**
     * Slower retrieval of components from this entity. Minimize usage of this,
     * but is fine to use e.g. when creating new entities and setting data in
     * components. Use mappers instead.
     * 
     * @param <T>
     *            the expected return component type.
     * @param type
     *            the expected return component type.
     * @return component that matches, or null if none is found.
     */
    public <T extends Component> T getComponent(Class<T> type) {
        return componentManager.getComponent(this, type);
    }

    /**
     * Populates provided Array with this Entity's components.
     * 
     * WARNING: This is an efficient way to access entitie's components.
     * Use with care. ComponentMapper is a faster and more efficient way to
     * access components.
     * 
     * @param array to put the components into.
     */
    public void getComponents(Array<Component> array) {
        componentManager.getComponents(this, array);
    }

    /**
     * @return An array of Entity components. This is a generated array,
     * and modifying it will not have an effect on components belonging
     * to this entity.
     * 
     * WARNING: This is an efficient way to access entitie's components.
     * Use with care. ComponentMapper is a faster and more efficient way to
     * access components.
     */
    public Array<Component> getComponents() {
        return componentManager.getComponents(this);
    }

    /**
     * Refresh all changes to components for this entity. After adding or
     * removing components, you must call this method. It will update all
     * relevant systems. It is typical to call this after adding components to
     * a newly created entity.
     */
    public void addToWorld() {
        world.addEntity(this);
    }

    /**
     * This entity has changed, a component added or deleted.
     */
    public void changedInWorld() {
        world.changedEntity(this);
    }

    /**
     * Delete this entity from the world.
     */
    public void deleteFromWorld() {
        world.deleteEntity(this);
    }

    /**
     * (Re)enable the entity in the world, after it having being disabled.
     * Won't do anything unless it was already disabled.
     */
    public void enable() {
        world.enable(this);
    }

    /**
     * Disable the entity from being processed. Won't delete it, it will
     * continue to exist but won't get processed.
     */
    public void disable() {
        world.disable(this);
    }

    /**
     * Returns the world this entity belongs to.
     * @return world of entity.
     */
    public World getWorld() {
        return world;
    }

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof Entity) {
            return id == ((Entity)object).id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
