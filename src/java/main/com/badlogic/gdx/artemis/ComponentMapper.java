package com.badlogic.gdx.artemis;

import com.badlogic.gdx.artemis.utils.SafeArray;

/**
 * High performance component retrieval from entities. Use this wherever you
 * need to retrieve components from entities often and fast.
 * 
 * @author Arni Arent
 *
 * @param <A> the class type of the component
 */
public class ComponentMapper<A extends Component> {
    private Class<A> classType;
    private SafeArray<Component> components;

    private ComponentMapper(Class<A> type, World world) {
        components = world.getComponentManager().getComponents(type);
        this.classType = type;
    }

    /**
     * Fast but unsafe retrieval of a component for this entity.
     * No bounding checks, so this could throw an ArrayIndexOutOfBoundsExeption,
     * however in most scenarios you already know the entity possesses this component.
     * 
     * @param e the entity that should possess the component
     * @return the instance of the component
     */
    public A get(Entity e) {
        return classType.cast(components.get(e.getId()));
    }

    /**
     * Fast and safe retrieval of a component for this entity.
     * If the entity does not have this component then null is returned.
     * 
     * @param e the entity that should possess the component
     * @return the instance of the component
     */
    public A getSafe(Entity e) {
        if(e.getId() < components.size) {
            return classType.cast(components.get(e.getId()));
        }
        return null;
    }

    /**
     * Checks if the entity has this type of component.
     * @param e the entity to check
     * @return true if the entity has this component type, false if it doesn't.
     */
    public boolean has(Entity e) {
        return getSafe(e) != null;
    }

    /**
     * Returns a component mapper for this type of components.
     * 
     * @param type the type of components this mapper uses.
     * @param world the world that this component mapper should use.
     * @return a new mapper.
     */
    public static <T extends Component> ComponentMapper<T> getFor(Class<T> type, World world) {
        return new ComponentMapper<T>(type, world);
    }

}
