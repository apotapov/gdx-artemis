package com.artemis.systems;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.Filter;
import com.artemis.World;
import junit.framework.Assert;
import org.junit.Test;

public class EntityProcessingSystemTest {

    static class ComponentA implements Component {
        @Override
        public void reset() {
        }
    }

    static class TestSystem1 extends EntityProcessingSystem {

        @SuppressWarnings("unchecked")
        public TestSystem1() {
            super(Filter.allComponents(ComponentA.class));
        }

        @Override
        protected void process(Entity e) {
            e.deleteFromWorld();
        }
    }

    static class TestSystem2 extends EntityProcessingSystem {

        int count;

        @SuppressWarnings("unchecked")
        public TestSystem2() {
            super(Filter.allComponents(ComponentA.class));
        }

        @Override
        protected void process(Entity e) {
            count++;
        }
    }

    @Test
    public void testDelete() {
        World world = new World();
        world.setSystem(new TestSystem1());
        world.setSystem(new TestSystem2());
        world.initialize();

        Entity e = world.createEntity();
        e.addComponent(world.createComponent(ComponentA.class));
        e.addToWorld();

        world.process();

        Assert.assertEquals(1, world.getSystem(TestSystem2.class).count);
    }

}
