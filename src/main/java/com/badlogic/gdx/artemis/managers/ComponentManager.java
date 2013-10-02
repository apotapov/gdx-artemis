package com.badlogic.gdx.artemis.managers;

import java.util.BitSet;

import com.badlogic.gdx.artemis.Component;
import com.badlogic.gdx.artemis.Entity;
import com.badlogic.gdx.artemis.utils.SafeArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.Pools;

/**
 * Responsible for pooling and managing of Components and their
 * mapping to entities.
 */
public class ComponentManager extends Manager {
    protected Array<Array<Component>> componentsByType;
    protected Array<Entity> deleted;

    protected static int nextComponentClassIndex = 0;
    protected static ObjectIntMap<Class<? extends Component>> componentClassIndeces =
            new ObjectIntMap<Class<? extends Component>>();

    /**
     * Returns the index of a Component class. Indices are cached, so retrieval
     * should be fast.
     * 
     * @param type Component class to retrieve the index for.
     * @return Index of a specific component class.
     */
    public static int getComponentClassIndex(Class<? extends Component> type) {
        if (componentClassIndeces.containsKey(type)) {
            return componentClassIndeces.get(type, -1);
        } else {
            componentClassIndeces.put(type, nextComponentClassIndex);
            return nextComponentClassIndex++;
        }
    }

    /**
     * Default constructor
     */
    public ComponentManager() {
        componentsByType = new SafeArray<Array<Component>>();
        deleted = new SafeArray<Entity>();
    }

    /**
     * Preferred way to create Components to allow for pooling.
     * 
     * @param type
     * @return
     */
    public <T extends Component> T createComponent(Class<T> type) {
        return Pools.obtain(type);
    }

    /**
     * Clean up Components belonging to the Entity
     * @param e Entity to clear components for.
     */
    public void removeComponentsOfEntity(Entity e) {
        BitSet componentBits = e.getComponentBits();
        for (int i = componentBits.nextSetBit(0); i >= 0; i = componentBits.nextSetBit(i+1)) {
            removeComponent(e.getId(), i);
        }
        componentBits.clear();
    }

    /**
     * Adds a Component bellonging to the specified Entity to the manager.
     * 
     * @param e Entity the component belongs to
     * @param component Component to add
     */
    public void addComponent(Entity e, Component component) {
        int classIndex = getComponentClassIndex(component.getClass());
        Array<Component> components = componentsByType.get(classIndex);
        if(components == null) {
            components = new SafeArray<Component>();
            componentsByType.set(classIndex, components);
        }
        components.set(e.getId(), component);

        e.getComponentBits().set(classIndex);
    }

    /**
     * Remove Component of specified class for a given Entity.
     * 
     * @param e Entity to remove the component for.
     * @param type Component class to remove.
     */
    public void removeComponent(Entity e, Class<? extends Component> type) {
        int classIndex = getComponentClassIndex(type);
        if(e.getComponentBits().get(classIndex)) {
            removeComponent(e.getId(), classIndex);
            e.getComponentBits().clear(classIndex);
        }
    }

    /**
     * Returns an Array of all Components of specified type.
     * 
     * @param type Type of Componets to return
     * @return an Array of said components.
     */
    public Array<Component> getComponents(Class<? extends Component> type) {
        int classIndex = getComponentClassIndex(type);
        Array<Component> components = componentsByType.get(classIndex);
        if(components == null) {
            components = new SafeArray<Component>();
            componentsByType.set(classIndex, components);
        }
        return components;
    }

    /**
     * Returns Component of the specified type belonging to specified Entity.
     * Null if not found.
     * 
     * @param e Entity to return Component for.
     * @param type Type of Component to return.
     * @return Component or null if not found.
     */
    public <T extends Component> T getComponent(Entity e, Class<T> type) {
        int classIndex = getComponentClassIndex(type);
        Array<Component> components = componentsByType.get(classIndex);
        if(components != null) {
            return type.cast(components.get(e.getId()));
        }
        return null;
    }

    /**
     * Fills an array with Components belonging to the specified Entity.
     * @param e Entity to get Components with.
     * @param array Array of Components to fill.
     */
    public void getComponents(Entity e, Array<Component> array) {
        BitSet componentBits = e.getComponentBits();

        for (int i = componentBits.nextSetBit(0); i >= 0; i = componentBits.nextSetBit(i+1)) {
            array.add(componentsByType.get(i).get(e.getId()));
        }
    }

    /**
     * Marks entity for clean up.
     */
    @Override
    public void deleted(Entity e) {
        deleted.add(e);
    }

    /**
     * Cleans up deleted Entry's components. Need to do it separately
     * to avoid freeing Components while other observers are processing
     * the removal of an entity.
     */
    public void clean() {
        if(deleted.size > 0) {
            for(int i = 0; deleted.size > i; i++) {
                removeComponentsOfEntity(deleted.get(i));
            }
            deleted.clear();
        }
    }

    /**
     * Helper method to remove a Component for specified Entity and
     * Component class index. Frees the Component to the pool.
     * 
     * @param entityId Entity to remove the component for.
     * @param componentClassIndex Component index to remove.
     */
    protected void removeComponent(int entityId, int componentClassIndex) {
        Array<Component> components = componentsByType.get(componentClassIndex);
        if (components != null) {
            Component compoment = components.get(entityId);
            if (compoment != null) {
                components.set(entityId, null);
                Pools.free(compoment);
            }
        }
    }

}
