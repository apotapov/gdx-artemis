package com.artemis.fsm.testclasses;

import com.artemis.fsm.ComponentProvider;
import com.artemis.testComponents.ComponentB;

/**
 * Created by Vemund Kvam on 17/06/14.
 */
public class ComponentProviderComponentB extends ComponentProvider<ComponentB> {

    public int valueA, valueB;

    @Override
    public void resetValues() {

    }

    @Override
    public Class<ComponentB> retrieveComponentClass() {
        return ComponentB.class;
    }

    @Override
    protected void onRemove(ComponentB component) {
        valueA = component.valueA;
        valueB = component.valueB;
    }

    @Override
    public void onAdd(ComponentB component) {
        component.valueA = valueA;
        component.valueB = valueB;
    }
}