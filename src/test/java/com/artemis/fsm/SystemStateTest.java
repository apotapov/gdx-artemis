package com.artemis.fsm;

import com.artemis.Entity;
import com.artemis.Filter;
import com.artemis.World;

import com.artemis.fsm.componentproviders.*;
import com.artemis.fsm.entity.EntityStateMachine;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.fsm.componentproviders.ComponentA;
import com.artemis.fsm.componentproviders.ComponentB;
import com.artemis.fsm.componentproviders.ComponentC;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;


public class SystemStateTest {

    private World world;
    private Entity entity;

    @Before
    public void init(){
        world = new World();
        world.initialize();

        entity = world.createEntity();
        world.addEntity(entity);
    }

    @Test
    public void testWithSystem(){
        EntityProcessingSystem systemForA = new EntityProcessingSystem(Filter.allComponents(ComponentA.class)) {
            @Override
            protected void process(Entity e) {
                e.getComponent(ComponentA.class).valueA = 1;
            }
        };

        EntityProcessingSystem systemForB = new EntityProcessingSystem(Filter.allComponents(ComponentB.class)) {
            @Override
            protected void process(Entity e) {
                e.getComponent(ComponentB.class).valueA = 1;
            }
        };
        world.setSystem(systemForA);
        world.setSystem(systemForB);

        EntityStateMachine machine = entity.getEntityStateMachine();
        machine.createState("A").add(machine.createComponentProvider(ComponentProviderComponentA.class));
        machine.createState("B").add(machine.createComponentProvider(ComponentProviderComponentB.class));

        entity.activateFiniteState("A");
        world.process();
        Assert.assertEquals(1, entity.getComponent(ComponentA.class).valueA);
        Assert.assertNull(entity.getComponent(ComponentB.class));

        entity.activateFiniteState("B");
        world.process();
        Assert.assertNull(entity.getComponent(ComponentA.class));
        Assert.assertEquals(1,entity.getComponent(ComponentB.class).valueA);

    }

    @Test
    public void testAll(){
        EntityStateMachine machine = entity.getEntityStateMachine();

        ComponentProviderComponentA componentProviderComponentA = machine.createComponentProvider(ComponentProviderComponentA.class);
        ComponentProviderComponentB componentProviderComponentB = machine.createComponentProvider(ComponentProviderComponentB.class);
        ComponentProviderComponentC componentProviderComponentC = machine.createComponentProvider(ComponentProviderComponentC.class);

        machine.createState(STATEID.A).add(componentProviderComponentA).add(componentProviderComponentB);
        machine.createState(STATEID.B).add(componentProviderComponentA).add(componentProviderComponentC);

        entity.getEntityStateMachine().activateState(STATEID.A);
        world.process();
        Assert.assertNotNull(entity.getComponent(ComponentA.class));
        Assert.assertNotNull(entity.getComponent(ComponentB.class));
        Assert.assertNull(entity.getComponent(ComponentC.class));

        entity.getEntityStateMachine().activateState(STATEID.B);
        world.process();
        Assert.assertNotNull(entity.getComponent(ComponentA.class));
        Assert.assertNull(entity.getComponent(ComponentB.class));
        Assert.assertNotNull(entity.getComponent(ComponentC.class));
    }

    @Test
    public void testKeepComponentMadeWithSameProvider(){
        EntityStateMachine machine = entity.getEntityStateMachine();

        ComponentProviderComponentA componentProviderComponentA = machine.createComponentProvider(ComponentProviderComponentA.class);

        machine.createState(STATEID.A).add(componentProviderComponentA);
        machine.createState(STATEID.B).add(componentProviderComponentA);

        entity.activateFiniteState(STATEID.A);
        world.process();
        ComponentA componentAStateA = entity.getComponent(ComponentA.class);

        entity.activateFiniteState(STATEID.B);
        world.process();
        ComponentA componentAStateB = entity.getComponent(ComponentA.class);
        Assert.assertEquals(componentAStateA,componentAStateB);
    }

    @Test
    public void testSwapComponentMadeWithDifferentProviders(){
        EntityStateMachine machine = entity.getEntityStateMachine();

        ComponentProviderComponentA componentProviderComponentAStateA = machine.createComponentProvider(ComponentProviderComponentA.class);
        ComponentProviderComponentA componentProviderComponentAStateB = machine.createComponentProvider(ComponentProviderComponentA.class);

        machine.createState(STATEID.A).add(componentProviderComponentAStateA);
        machine.createState(STATEID.B).add(componentProviderComponentAStateB);

        entity.getEntityStateMachine().activateState(STATEID.A);
        world.process();
        ComponentA componentAStateA = entity.getComponent(ComponentA.class);

        entity.getEntityStateMachine().activateState(STATEID.B);
        world.process();
        ComponentA componentAStateB = entity.getComponent(ComponentA.class);
        Assert.assertNotSame(componentAStateA, componentAStateB);
    }

    public enum STATEID{
        A,
        B
    }

}
