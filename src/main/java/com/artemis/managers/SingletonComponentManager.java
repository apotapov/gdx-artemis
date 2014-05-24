package com.artemis.managers;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * A manager for dealing with Singleton Components. Allows easy storage
 * and retrieval of components that will always have only one instance
 * in the world. All the singleton components will be tied to a single
 * entity. The entity will exist in the world and will be processed
 * by regular means. This manager allows to access the singleton components
 * without having to specify them in EntitySystem's filter declaration.
 * 
 * @author apotapov
 *
 */
public class SingletonComponentManager extends Manager {

    protected Entity singletonEntity;

    /**
     * A map of ComponentMappers by Component class.
     * Allows for easier retrieval of singleton components.
     */
    protected ObjectMap<Class<? extends Component>, ComponentMapper<? extends Component>> mappers;

    public SingletonComponentManager() {
        mappers = new ObjectMap<Class<? extends Component>, ComponentMapper<? extends Component>>();
    }

    @Override
    public void initialize() {
        singletonEntity = world.createEntity();
        world.addEntity(singletonEntity);
    }

    /**
     * Add a singleton component to the manager and the world.
     * 
     * @param component Component to add.
     * @return Entity the component is attached to.
     */
    public Entity addSingletonComponent(Component component) {
        // ensure that we have a proper mapper for the component so that we can
        // retrieve it later.
        Class<? extends Component> componentClass = component.getClass();
        if (!mappers.containsKey(componentClass)) {
            mappers.put(componentClass, world.getMapper(componentClass));
        }
        singletonEntity.addComponent(component);
        return singletonEntity;
    }

    /**
     * Retrieves a singleton component by the specified class.
     * 
     * @param <T> Type of component
     * @param componentClass Type of component to retrieve.
     * @return The component of specified class, or null if the component is not stored.
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T getSingletonComponent(Class<T> componentClass) {
        if (mappers.containsKey(componentClass)) {
            return (T) mappers.get(componentClass).get(singletonEntity);
        }
        return null;
    }

    @Override
    public void dispose() {
        world.deleteEntity(singletonEntity);
        singletonEntity = null;
        mappers.clear();
    }
}
