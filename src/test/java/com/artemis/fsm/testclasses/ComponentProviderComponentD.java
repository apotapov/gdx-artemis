package com.artemis.fsm.testclasses;

import com.artemis.fsm.ComponentProvider;
import com.artemis.testComponents.ComponentD;

/**
 * Created by Vemund Kvam on 17/06/14.
 */
public class ComponentProviderComponentD extends ComponentProvider<ComponentD> {

    public int valueA, valueB;

    @Override
    public void resetValues() {

    }

    @Override
    public Class<ComponentD> retrieveComponentClass() {
        return ComponentD.class;
    }

    @Override
    protected void onRemove(ComponentD component) {
        valueA = component.valueA;
        valueB = component.valueB;
    }

    @Override
    public void onAdd(ComponentD component) {
        component.valueA = valueA;
        component.valueB = valueB;
    }
}