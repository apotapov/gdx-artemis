package com.artemis.fsm.componentproviders;

import com.artemis.fsm.ComponentProvider;

/**
 * @author Vemund Kvam on 17/06/14.
 */
public class ComponentProviderComponentA extends ComponentProvider<ComponentA> {

    public int valueA, valueB;

    @Override
    protected void onProviderInit() {
        valueA = 0;
        valueB = 0;
    }

    @Override
    protected void onRemove(ComponentA component) {
        valueA = component.valueA;
        valueB = component.valueB;
    }

    @Override
    protected void onAdd(ComponentA component) {
        component.valueA = valueA;
        component.valueB = valueB;
    }
}