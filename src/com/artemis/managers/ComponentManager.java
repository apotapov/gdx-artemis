package com.artemis.managers;

import java.util.BitSet;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.SafeArray;
import com.badlogic.gdx.utils.Pools;

public class ComponentManager extends Manager {
    protected SafeArray<SafeArray<Component>> componentsByType;
    protected SafeArray<Entity> deleted;

    public ComponentManager() {
        componentsByType = new SafeArray<SafeArray<Component>>();
        deleted = new SafeArray<Entity>();
    }

    public <T extends Component> T createComponent(Class<T> type) {
        return Pools.obtain(type);
    }

    public void removeComponentsOfEntity(Entity e) {
        BitSet componentBits = e.getComponentBits();
        for (int i = componentBits.nextSetBit(0); i >= 0; i = componentBits.nextSetBit(i+1)) {
            removeComponent(e.getId(), i);
        }
        componentBits.clear();
    }

    public void addComponent(Entity e, Component component) {
        SafeArray<Component> components = componentsByType.get(component.getTypeIndex());
        if(components == null) {
            components = new SafeArray<Component>();
            componentsByType.set(component.getTypeIndex(), components);
        }
        components.set(e.getId(), component);

        e.getComponentBits().set(component.getTypeIndex());
    }

    public void removeComponent(Entity e, int typeIndex) {
        if(e.getComponentBits().get(typeIndex)) {
            removeComponent(e.getId(), typeIndex);
            e.getComponentBits().clear(typeIndex);
        }
    }

    public SafeArray<Component> getComponentsByIndex(int index) {
        SafeArray<Component> components = componentsByType.get(index);
        if(components == null) {
            components = new SafeArray<Component>();
            componentsByType.set(index, components);
        }
        return components;
    }

    public Component getComponent(Entity e, int typeIndex) {
        SafeArray<Component> components = componentsByType.get(typeIndex);
        if(components != null) {
            return components.get(e.getId());
        }
        return null;
    }

    public SafeArray<Component> getComponentsFor(Entity e, SafeArray<Component> fillBag) {
        BitSet componentBits = e.getComponentBits();

        for (int i = componentBits.nextSetBit(0); i >= 0; i = componentBits.nextSetBit(i+1)) {
            fillBag.add(componentsByType.get(i).get(e.getId()));
        }

        return fillBag;
    }


    @Override
    public void deleted(Entity e) {
        deleted.add(e);
    }

    public void clean() {
        if(deleted.size > 0) {
            for(int i = 0; deleted.size > i; i++) {
                removeComponentsOfEntity(deleted.get(i));
            }
            deleted.clear();
        }
    }

    protected void removeComponent(int entityId, int typeIndex) {
        SafeArray<Component> components = componentsByType.get(typeIndex);
        if (components != null) {
            Component compoment = components.get(entityId);
            if (compoment != null) {
                components.set(entityId, null);
                Pools.free(compoment);
            }
        }
    }

}
