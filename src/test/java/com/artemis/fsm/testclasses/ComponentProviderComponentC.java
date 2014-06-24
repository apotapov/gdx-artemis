package com.artemis.fsm.testclasses;

import com.artemis.fsm.ComponentProvider;
import com.artemis.testComponents.ComponentC;

/**
 * Created by Vemund Kvam on 17/06/14.
 */
public class ComponentProviderComponentC extends ComponentProvider<ComponentC> {

    public int valueA, valueB;

    @Override
    public void resetValues() {

    }

    @Override
    public Class<ComponentC> retrieveComponentClass() {
        return ComponentC.class;
    }

    @Override
    protected void onRemove(ComponentC component) {
        valueA = component.valueA;
        valueB = component.valueB;
    }

    @Override
    public void onAdd(ComponentC component) {
        component.valueA = valueA;
        component.valueB = valueB;
    }
}