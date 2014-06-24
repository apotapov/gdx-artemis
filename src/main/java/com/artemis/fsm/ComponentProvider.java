package com.artemis.fsm;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

import java.lang.reflect.ParameterizedType;

/**
 * Provider of components for {@link com.artemis.fsm.FiniteState states}, different
 * FiniteStates may share provider instances.
 *
 * Concrete providers to be implemented by user.
 *
 * 1. For each individual component that may exist on an entity, there should be one provider.
 * 2. If a provider is shared by two states, components will not be added or
 * removed from the entity on stateChange.
 * 3. If two states have providers of same component type, but different provider instances,
 * the component will be swapped, triggering an entity change.
 *
 * Created by Vemund Kvam on 15/06/14.
 *
 */
public abstract class ComponentProvider<T extends Component> implements Pool.Poolable {
    private Class<T> componentClass;
    protected T lastComponentProduced;
    protected int classIndex=-1;
    protected int instanceIndex=-1;
    protected boolean indicesSet = false;
    protected Entity entity;

    public ComponentProvider() {
        setComponentClass();
    }

    /**
     * Gets the class of T.
     */
    private void setComponentClass() {
        ParameterizedType superclass = (ParameterizedType) getClass().getGenericSuperclass();
        componentClass = (Class<T>) superclass.getActualTypeArguments()[0];
    }

    /**
     * Called before the component is removed from associated {@link com.artemis.Entity Entity}.
     *
     * E.g. save the component values until next time a component is provided.
     * @param component the removed component.
     */
    protected abstract void onRemove(T component);

    /**
     * Called before the component is added to the entity.
     *
     * E.g. Use to restore component values before adding them to the entity.
     * @param component the added component
     */
    public abstract void onAdd(T component);

    /**
     * Called when the componentProvider is removed from its associated
     * {@link com.artemis.fsm.FiniteStateMachine FiniteStateMachine}
     *
     * E.g. restore all default values that are used when adding a component.
     */
    public abstract void resetValues();


    protected void setEntity(Entity entity){
        this.entity = entity;
    }

    protected void setIndices(int classIndex, int instanceIndex){
        this.classIndex=classIndex;
        this.instanceIndex=instanceIndex;
        indicesSet = true;
    }

    protected Component getLastComponent(){
        return lastComponentProduced;
    }

    protected Class<T> getComponentClass(){
        return componentClass;
    }

    protected T createComponent(){
        T component = Pools.obtain(componentClass);
        lastComponentProduced = component;
        onAdd(component);
        return component;
    }
    protected void removedFromEntity(){
        T component = entity.getComponent(getComponentClass());
        if(component==lastComponentProduced){
            onRemove(component);
        }
    }

    @Override
    public void reset() {
        resetValues();
        lastComponentProduced = null;
        classIndex = -1;
        instanceIndex = -1;
        indicesSet=false;
        entity=null;
    }
}
