package com.artemis;

import junit.framework.Assert;

import org.junit.Test;

import com.artemis.systems.EntityProcessingSystem;

public class EntityTest {
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

    static class ComponentC implements Component {
        @Override
        public void reset() {
        }
    }

    static class SystemA extends EntityProcessingSystem {
        ComponentMapper<ComponentA> aMapper;

        @SuppressWarnings("unchecked")
        public SystemA() {
            super(Filter.allComponents(ComponentA.class));
        }

        @Override
        public void initialize() {
            aMapper = world.getMapper(ComponentA.class);
        }

        @Override
        protected void process(Entity e) {
            Assert.assertEquals(2, e.getComponents().size);
            for (Component c : e.getComponents()) {
                Assert.assertTrue(c instanceof ComponentA || c instanceof ComponentB);
            }
        }
    }

    @Test
    public void testGetComponents() {
        World world = new World();
        world.setSystem(new SystemA());
        world.initialize();

        Entity e = world.createEntity();
        ComponentA a = world.createComponent(ComponentA.class);
        e.addComponent(a);
        ComponentB b = world.createComponent(ComponentB.class);
        e.addComponent(b);
        world.addEntity(e);

        world.process();
    }

}
