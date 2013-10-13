package com.artemis.systems;

import junit.framework.Assert;

import org.junit.Test;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;

public class EntitySystemTest {

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

    static class TestSytem extends EntitySystem {

        ComponentMapper<ComponentA> aMapper;
        ComponentMapper<ComponentB> bMapper;

        int numEntities;

        @SuppressWarnings("unchecked")
        public TestSytem() {
            super(Aspect.getAspectForAll(ComponentA.class).one(ComponentB.class));
        }

        @Override
        protected void processEntities(Array<Entity> entities) {
            numEntities = entities.size;
        }

    }

    @Test
    public void testCheck() {
        World world = new World();
        TestSytem system = new TestSytem();
        world.setSystem(system);
        world.initialize();

        Entity e = world.createEntity();
        e.addComponent(world.createComponent(ComponentB.class));
        e.addToWorld();

        world.process();

        Assert.assertEquals(0, system.numEntities);

        e = world.createEntity();
        e.addComponent(world.createComponent(ComponentB.class));
        e.addComponent(world.createComponent(ComponentA.class));
        e.addToWorld();

        world.process();

        Assert.assertEquals(1, system.numEntities);
    }

}
