package com.artemis;

import junit.framework.Assert;

import org.junit.Test;

import com.artemis.systems.EntityProcessingSystem;

public class ParentMapperTest {

    static class TestComponent implements Component {

        int test;

        @Override
        public void reset() {
        }

    }

    static class TestParentSystem extends EntityProcessingSystem {

        ComponentMapper<TestComponent> mapper;

        @SuppressWarnings("unchecked")
        public TestParentSystem() {
            super(Aspect.getAspectForAll(TestComponent.class));
            // TODO Auto-generated constructor stub
        }

        @Override
        public void initialize() {
            mapper = world.getMapper(TestComponent.class);
        }

        @Override
        protected void process(Entity e) {
            Assert.assertNotNull(mapper);
            mapper.get(e);
        }
    }

    static class TestChildSystem extends TestParentSystem {
    }

    @Test
    public void testParentMapper() {
        World world = new World();
        world.setSystem(new TestParentSystem());
        world.setSystem(new TestChildSystem());
        world.initialize();

        Entity e = world.createEntity();
        e.addComponent(world.createComponent(TestComponent.class));
        e.addToWorld();

        world.process();
    }


}
