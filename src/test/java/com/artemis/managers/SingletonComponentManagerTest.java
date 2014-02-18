package com.artemis.managers;

import junit.framework.Assert;

import org.junit.Test;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Filter;
import com.artemis.World;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;

public class SingletonComponentManagerTest {

    static class ComponentA implements Component {
        @Override
        public void reset() {
        }
    }

    static class ComponentB implements Component {
        @Override
        public void reset() {
        }
    }

    static class TestSystem extends EntitySystem {

        ComponentMapper<ComponentA> aMapper;
        SingletonComponentManager manager;

        int regularEntities;
        ComponentA componentA;
        ComponentB componentB;

        @SuppressWarnings("unchecked")
        public TestSystem() {
            super(Filter.allComponents(ComponentA.class));
        }

        @Override
        public void initialize() {
            aMapper = world.getMapper(ComponentA.class);
            manager = world.getManager(SingletonComponentManager.class);
        }

        @Override
        protected void processEntities(Array<Entity> entities) {
            regularEntities = entities.size;
            for (Entity entity : entities) {
                componentA = aMapper.get(entity);
            }
            componentB = manager.getSingletonComponent(ComponentB.class);
        }
    }

    @Test
    public void testStorageRetrieval() {
        World world = new World();
        SingletonComponentManager manager = new SingletonComponentManager();
        world.setManager(manager);
        world.initialize();

        manager.addSingletonComponent(new ComponentA());
        world.process();

        Assert.assertNotNull(manager.getSingletonComponent(ComponentA.class));
        Assert.assertNull(manager.getSingletonComponent(ComponentB.class));

        manager.addSingletonComponent(new ComponentB());
        world.process();

        Assert.assertNotNull(manager.getSingletonComponent(ComponentA.class));
        Assert.assertNotNull(manager.getSingletonComponent(ComponentB.class));
    }

    @Test
    public void testSystemAccess() {
        World world = new World();
        SingletonComponentManager manager = new SingletonComponentManager();
        world.setManager(manager);
        TestSystem system = new TestSystem();
        world.setSystem(system);
        world.initialize();

        world.process();
        Assert.assertEquals(0, system.regularEntities);
        Assert.assertNull(system.componentA);
        Assert.assertNull(system.componentB);

        ComponentA a = new ComponentA();
        ComponentB b = new ComponentB();

        manager.addSingletonComponent(a);
        manager.addSingletonComponent(b);

        world.process();
        Assert.assertEquals(1, system.regularEntities);
        Assert.assertNotNull(system.componentB);
        Assert.assertNotNull(system.componentA);

        Assert.assertTrue(a == system.componentA);
        Assert.assertTrue(b == system.componentB);
    }



}
