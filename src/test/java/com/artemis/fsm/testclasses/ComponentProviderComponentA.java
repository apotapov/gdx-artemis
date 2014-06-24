package com.artemis.fsm.testclasses;

import com.artemis.fsm.ComponentProvider;
import com.artemis.testComponents.ComponentA;

/**
 * Created by Vemund Kvam on 17/06/14.
 */
public class ComponentProviderComponentA extends ComponentProvider<ComponentA> {

    public int valueA, valueB;

    @Override
    public void resetValues() {
        valueA = 0;
        valueB = 0;
    }

    @Override
    public Class<ComponentA> retrieveComponentClass() {
        return ComponentA.class;
    }

    @Override
    protected void onRemove(ComponentA component) {
        valueA = component.valueA;
        valueB = component.valueB;
    }

    @Override
    public void onAdd(ComponentA component) {
        component.valueA = valueA;
        component.valueB = valueB;
    }
}