package com.artemis.fsm.componentproviders;

import com.artemis.fsm.ComponentProvider;

/**
 * @author Vemund Kvam on 17/06/14.
 */
public class ComponentProviderComponentB extends ComponentProvider<ComponentB> {

    public int valueA, valueB;

    @Override
    protected void onProviderInit() {
        valueA = 0;
        valueB = 0;
    }

    @Override
    protected void onRemove(ComponentB component) {
        valueA = component.valueA;
        valueB = component.valueB;
    }

    @Override
    protected void onAdd(ComponentB component) {
        component.valueA = valueA;
        component.valueB = valueB;
    }

}