package com.artemis.fsm;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by Vemund Kvam on 25/06/14.
 */
public class EntityStateMachineSimpleTest {

    public static class ComponentA implements Component {
        public int valueA;
        @Override
        public void reset() {
        }
    }

    public static class ComponentB implements Component {
        public int valueA;
        @Override
        public void reset() {
        }
    }

    public static class ComponentProviderA extends ComponentProvider<ComponentA> {
        public int value, initialValue;
        @Override
        protected void onProviderInit() {
            value = initialValue;
        }
        @Override
        protected void onRemove(ComponentA component) {
            value = component.valueA;
        }
        @Override
        protected void onAdd(ComponentA component) {
            component.valueA = value;
        }
    }

    public static class ComponentProviderB extends ComponentProvider<ComponentB> {
        public int value, initialValue;
        @Override
        protected void onProviderInit() {
            value = initialValue;
        }
        @Override
        protected void onRemove(ComponentB component) {
            value = component.valueA;
        }
        @Override
        protected void onAdd(ComponentB component) {
            component.valueA = value;
        }
    }

    @Test
    public void testAPISimple() {
        World world = new World();
        Entity entity = world.createEntity();

        EntityStateMachine machine = entity.getEntityStateMachine();
        ComponentProviderA providerA = machine.createComponentProvider(ComponentProviderA.class);
        providerA.initialValue = 1;

        ComponentProviderB providerB = machine.createComponentProvider(ComponentProviderB.class);
        providerB.initialValue = 2;

        machine.createState("State A").add(providerA);
        machine.createState("State B").add(providerA).add(providerB);

        entity.activateFiniteState("State B");
        world.process();
        ComponentA componentAFromStateB = entity.getComponent(ComponentA.class);
        // Assert initialValue was set.
        Assert.assertEquals(1,componentAFromStateB.valueA);
        // Set a value to test restore functionality given by providerB.onRemove(component) and providerB.onAdd(component);
        entity.getComponent(ComponentB.class).valueA=-1;

        entity.activateFiniteState("State A");
        world.process();
        // Assert ComponentB was removed
        Assert.assertNull(entity.getComponent(ComponentB.class));
        // Assert ComponentA was untouched as State A and State B uses the same provider instance for ComponentA
        Assert.assertEquals(componentAFromStateB,entity.getComponent(ComponentA.class));

        entity.activateFiniteState("State B");
        world.process();
        // Assert value was transferred back to component from provider.
        Assert.assertEquals(-1,entity.getComponent(ComponentB.class).valueA);
    }
}
