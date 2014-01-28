package com.artemis.managers;

import org.junit.Assert;
import org.junit.Test;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.systems.EntityProcessingSystem;

public class ComponentManagerTest {

    static class ComponentA implements Component {
        @Override
        public void reset() {
        }
    }

    static class SystemA extends EntityProcessingSystem {
        ComponentMapper<ComponentA> aMapper;

        @SuppressWarnings("unchecked")
        public SystemA() {
            super(Aspect.getAspectForAll(ComponentA.class));
        }

        @Override
        public void initialize() {
            aMapper = world.getMapper(ComponentA.class);
        }

        @Override
        protected void process(Entity e) {
            Assert.assertNotNull(aMapper.get(e));
            e.removeComponent(ComponentA.class);
        }
    }

    static class SystemB extends EntityProcessingSystem {
        ComponentMapper<ComponentA> aMapper;

        @SuppressWarnings("unchecked")
        public SystemB() {
            super(Aspect.getAspectForAll(ComponentA.class));
        }

        @Override
        public void initialize() {
            aMapper = world.getMapper(ComponentA.class);
        }

        @Override
        protected void process(Entity e) {
            Assert.assertNotNull(aMapper.get(e));
        }
    }


    @Test
    public void testRemoveComponent() {
        World world = new World();
        world.setSystem(new SystemA());
        world.setSystem(new SystemB());
        world.initialize();

        Entity e = world.createEntity();
        ComponentA c = world.createComponent(ComponentA.class);
        e.addComponent(c);
        world.addEntity(e);

        world.process();
        world.process();
    }
}
