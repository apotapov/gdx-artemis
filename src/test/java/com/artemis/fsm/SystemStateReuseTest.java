package com.artemis.fsm;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.fsm.componentproviders.ComponentProviderComponentA;
import com.artemis.fsm.componentproviders.ComponentProviderComponentB;
import com.artemis.fsm.componentproviders.ComponentA;
import com.artemis.fsm.componentproviders.ComponentB;
import com.artemis.fsm.EntityStateMachine;
import com.badlogic.gdx.utils.Pools;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;


public class SystemStateReuseTest {

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
    public void testComponentPooling(){

        EntityStateMachine entityStateMachine = entity.getEntityStateMachine();
        ComponentProviderComponentA providerA = entityStateMachine.createComponentProvider(ComponentProviderComponentA.class);
        ComponentProviderComponentB providerB = entityStateMachine.createComponentProvider(ComponentProviderComponentB.class);

        entity.getEntityStateMachine().createState(STATEID.A).add(providerA);
        entity.getEntityStateMachine().createState(STATEID.B).add(providerA).add(providerB);
        entity.activateFiniteState(STATEID.A); // Component A instance created.
        world.process();
        entity.activateFiniteState(STATEID.B); // Component A instance is same, Component B instance created
        world.process();
        entity.activateFiniteState(STATEID.A); // Component B instance pooled. Component A instance is same.
        world.process();
        verifyPooling(0,0,0,1);
        world.deleteEntity(entity);
        world.process();
        verifyPooling(1,1,1,1); // Verify both providers and their produced components where Pooled.

        entity = world.createEntity();
        world.addEntity(entity);
        world.process();
        entityStateMachine = entity.getEntityStateMachine();
        providerA = entityStateMachine.createComponentProvider(ComponentProviderComponentA.class);
        providerB = entityStateMachine.createComponentProvider(ComponentProviderComponentB.class);
        verifyPooling(0, 0, 1, 1); // Verify providers where reused.

        entityStateMachine.createState(STATEID.A).add(providerA);
        entityStateMachine.createState(STATEID.B).add(providerB);

        for(int i=0;i<4;i++) {
            entity.activateFiniteState(STATEID.B);
            world.process();
            verifyPooling(0, 0, 1, 0);  // Pool component A, Use componentB from Pool

            entity.activateFiniteState(STATEID.A); // Pool component B, Use componentA from Pool
            world.process();
            verifyPooling(0, 0, 0, 1);
        }

        // Free providers and components by deleting states instead of the whole entity.
        entity.getEntityStateMachine().deleteState(STATEID.A);
        world.process();
        verifyPooling(1,0,1,1);

        entity.getEntityStateMachine().deleteState(STATEID.B);
        world.process();

        verifyPooling(1,1,1,1);

        world.deleteEntity(entity);
        world.process();
        verifyPooling(1,1,1,1); // Deleting the entity should not free providers and components again.

    }

    private void verifyPooling(int pA,int pB,int cA, int cB){
        int providerACount = Pools.get(ComponentProviderComponentA.class).getFree();
        int providerBCount = Pools.get(ComponentProviderComponentB.class).getFree();
        int componentACount = Pools.get(ComponentA.class).getFree();
        int componentBCount = Pools.get(ComponentB.class).getFree();

        Assert.assertEquals(pA, providerACount);
        Assert.assertEquals(pB,providerBCount);
        Assert.assertEquals(cA,componentACount);
        Assert.assertEquals(cB,componentBCount);
    }

    public enum STATEID{
        A,
        B
    }

}
