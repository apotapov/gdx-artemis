package com.artemis.fsm;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

/**
 * Created by Vemund Kvam on 15/06/14.
 */
public abstract class ComponentProvider<T extends Component> implements Pool.Poolable {
    private Class<T> componentClass;
    protected T lastComponentProduced;
    protected int classIndex=-1;
    protected int instanceIndex=-1;
    protected boolean indicesSet = false;
    protected Entity entity;

    public ComponentProvider(){
        setComponentClass();
    }

    public void setEntity(Entity entity){
        this.entity = entity;
    }

    public void setIndices(int classIndex, int instanceIndex){
        this.classIndex=classIndex;
        this.instanceIndex=instanceIndex;
        indicesSet = true;
    }

    public void setComponentClass() {
        this.componentClass = retrieveComponentClass();
    }

    public abstract Class<T> retrieveComponentClass();

    protected Component getLastComponent(){
        return lastComponentProduced;
    }

    public Class<T> getComponentClass(){
        return componentClass;
    }

    public T createComponent(){
        T component = Pools.obtain(componentClass);
        lastComponentProduced = component;
        onAdd(component);
        return component;
    }
    public void removedFromEntity(){
        T component = entity.getComponent(getComponentClass());
        if(component==lastComponentProduced){
            onRemove(component);
        }
    }

    protected abstract void onRemove(T component);

    public abstract void onAdd(T component);

    @Override
    public void reset() {
        componentClass = null;
        lastComponentProduced = null;
        classIndex = -1;
        instanceIndex = -1;
        indicesSet=false;
        entity=null;
    }
}
