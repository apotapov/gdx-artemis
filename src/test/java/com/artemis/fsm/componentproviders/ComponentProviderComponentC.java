package com.artemis.fsm.componentproviders;

import com.artemis.fsm.ComponentProvider;

/**
 * @author Vemund Kvam on 17/06/14.
 */
public class ComponentProviderComponentC extends ComponentProvider<ComponentC> {

    public int valueA, valueB;

    @Override
    public void onProviderInit() {
        valueA = 0;
        valueB = 0;
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