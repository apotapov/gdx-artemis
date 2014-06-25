package com.artemis.systems;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.Filter;
import com.artemis.World;
import com.badlogic.gdx.utils.ObjectIntMap;
import junit.framework.Assert;
import org.junit.Test;

public class SkipEntityProcessingSystemTest {

    private static final int DELAY = 1000;

    static class ComponentA implements Component {
        @Override
        public void reset() {
        }
    }

    static class DelaySystem extends SkipEntityProcessingSystem {

        ObjectIntMap<Entity> processCount;

        @SuppressWarnings("unchecked")
        public DelaySystem() {
            super(Filter.allComponents(ComponentA.class), DELAY, true);

            processCount = new ObjectIntMap<Entity>();
        }

        @Override
        protected void processDelayed(Entity e) {
            processCount.getAndIncrement(e, 0, 1);
        }
    }

    @Test
    public void testExpiration() {
        World world = new World();
        world.setSystem(new DelaySystem());
        world.initialize();

        Entity e = world.createEntity();
        e.addComponent(world.createComponent(ComponentA.class));
        e.addToWorld();

        world.process();

        Assert.assertEquals(-1, world.getSystem(DelaySystem.class).processCount.get(e, -1));

        world.setDelta(DELAY / 2);
        world.process();

        Assert.assertEquals(-1, world.getSystem(DelaySystem.class).processCount.get(e, -1));

        world.setDelta(DELAY / 2);
        world.process();

        Assert.assertEquals(1, world.getSystem(DelaySystem.class).processCount.get(e, -1));

        world.setDelta(DELAY / 2);
        world.process();

        Assert.assertEquals(1, world.getSystem(DelaySystem.class).processCount.get(e, -1));

        world.setDelta(DELAY / 2);
        world.process();

        Assert.assertEquals(2, world.getSystem(DelaySystem.class).processCount.get(e, -1));

        world.setDelta(DELAY / 2);
        world.process();

        Assert.assertEquals(2, world.getSystem(DelaySystem.class).processCount.get(e, -1));

        world.setDelta(DELAY);
        world.process();

        Assert.assertEquals(3, world.getSystem(DelaySystem.class).processCount.get(e, -1));

        world.setDelta(DELAY / 2);
        world.process();

        Assert.assertEquals(4, world.getSystem(DelaySystem.class).processCount.get(e, -1));
    }

}
