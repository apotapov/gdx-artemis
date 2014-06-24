package com.artemis.fsm;

import com.artemis.Entity;
import com.artemis.World;

import com.artemis.fsm.testclasses.*;
import com.artemis.systems.event.SystemEvent;
import com.artemis.testComponents.ComponentA;
import com.artemis.testComponents.ComponentB;
import com.artemis.testComponents.ComponentC;
import com.artemis.testComponents.ComponentD;
import junit.framework.Assert;
import org.junit.Test;


public class FiniteStateTest {

    @Test
    public void testAPI(){
        World world = new World();
        world.initialize();

        Entity entity = world.createEntity();
        world.addEntity(entity);

        FiniteStateMachine stateMachineTest = entity.getFiniteStateMachine();

        ComponentProviderComponentA stateAComponentAProvider = stateMachineTest.createComponentProvider(ComponentProviderComponentA.class);
        stateAComponentAProvider.valueA = 10;
        stateAComponentAProvider.valueB = 10;

        ComponentProviderComponentD stateAComponentDProvider = stateMachineTest.createComponentProvider(ComponentProviderComponentD.class);

        ComponentProviderComponentA stateBComponentAProvider = stateMachineTest.createComponentProvider(ComponentProviderComponentA.class);
        stateBComponentAProvider.valueA = 20;
        stateBComponentAProvider.valueB = 20;

        ComponentProviderComponentB stateBComponentBProvider = stateMachineTest.createComponentProvider(ComponentProviderComponentB.class);

        ComponentProviderComponentC sharedComponentCProvider = stateMachineTest.createComponentProvider(ComponentProviderComponentC.class);

        FiniteState state = stateMachineTest.createState(STATE.A).add(stateAComponentAProvider).add(sharedComponentCProvider);

        state.add(stateAComponentDProvider);
/*
        for(int i=0;i<11;i++) {
            ComponentProvider componentProvider;
            if(i%2==0) {
                componentProvider = stateMachineTest.createComponentProvider(ComponentProviderComponentD.class);
            }else{
                componentProvider = stateMachineTest.createComponentProvider(ComponentProviderComponentD2.class);
            }

            stateMachineTest.createState("State"+i).add(componentProvider).add(stateBComponentBProvider).add(sharedComponentCProvider);
        }
        */
        stateMachineTest.createState("State"+0).add(stateBComponentAProvider).add(sharedComponentCProvider);

        System.out.println("\nSet A state");
        entity.getFiniteStateMachine().activateState(STATE.A);
        world.process();

        ComponentA componentA = entity.getComponent(ComponentA.class);
        ComponentC componentC = entity.getComponent(ComponentC.class);
        int valueA = componentA.valueA;
        Assert.assertNotNull(componentA);
        Assert.assertNotNull(entity.getComponent(ComponentD.class));

        System.out.println("\nSet B state");
        entity.getFiniteStateMachine().activateState("State0");
        world.process();

        ComponentA newComponentA = entity.getComponent(ComponentA.class);
        ComponentC newComponentC = entity.getComponent(ComponentC.class);

        // ComponentC should not have been replaced, it has the same componentprovider in each state.
        Assert.assertEquals(componentC,newComponentC);

//        Assert.assertNotNull(newComponentA);
        // ComponentA can not have been reused, since it wasn't freed at the time the new component was created.
  //      Assert.assertNotSame(newComponentA, componentA);
   //     Assert.assertEquals(1,Pools.get(ComponentA.class).getFree());

   //     Assert.assertNotSame(valueA,newComponentA.valueA);
   //     Assert.assertNotNull(entity.getComponent(ComponentB.class));

        System.out.println("\nRepeat");

        for(int i=100000;i>0;i--) {

            //System.out.println("\nSet A state");
            entity.activateFiniteState(STATE.A);
            world.process();
            //System.out.println("\nSet B state");
            //Assert.assertEquals(1, Pools.get(ComponentA.class).getFree());
            //for(int j=0;j<11;j++) {
                //System.out.println("State"+j);
                entity.activateFiniteState("State0");
                world.process();
            //}
            //Assert.assertEquals(1, Pools.get(ComponentA.class).getFree());
        }

        System.out.println("\nStop");
        world.deleteEntity(entity);
        world.process();
     }

    public enum STATE{
        A;
    }

}
