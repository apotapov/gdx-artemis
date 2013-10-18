package com.artemis;

import junit.framework.Assert;

import org.junit.Test;

import com.artemis.annotations.IgnoreMapper;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;

public class IgnoreMapperTest {

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

        @IgnoreMapper
        ComponentMapper<ComponentB> bMapper;

        int numEntities;

        @SuppressWarnings("unchecked")
        public TestSytem() {
            super(Aspect.getAspectForAll(ComponentA.class).one(ComponentB.class));
        }

        @Override
        protected void processEntities(Array<Entity> entities) {
        }
    }

    @Test
    public void testIgnore() {
        World world = new World();
        TestSytem system = new TestSytem();
        world.setSystem(system);
        world.initialize();

        Assert.assertNotNull(system.aMapper);
        Assert.assertNull(system.bMapper);
    }



}
