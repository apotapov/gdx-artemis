package com.artemis.systems;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.Filter;
import com.artemis.World;
import junit.framework.Assert;
import org.junit.Test;

public class ExpirationSystemTest {

    private static final int DELAY = 1000;

    static class ComponentA implements Component {
        @Override
        public void reset() {
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExpiration() {
        World world = new World();
        world.setSystem(new ExpirationEntitySystem(Filter.allComponents(ComponentA.class), DELAY));
        world.initialize();

        Entity e = world.createEntity();
        e.addComponent(world.createComponent(ComponentA.class));
        e.addToWorld();

        world.process();

        Assert.assertTrue(e.isActive());

        world.setDelta(DELAY - 1);
        world.process();

        Entity e2 = world.createEntity();
        e2.addComponent(world.createComponent(ComponentA.class));
        e2.addToWorld();

        Assert.assertTrue(e.isActive());

        world.setDelta(DELAY - 1);
        world.process();

        // entity deleted here, but it's technically still active
        Assert.assertTrue(e.isActive());
        Assert.assertTrue(e2.isActive());

        world.setDelta(DELAY - 1);
        world.process();

        // everyone is notified
        Assert.assertFalse(e.isActive());
        // e2 is still active, but is getting deleted.
        Assert.assertTrue(e2.isActive());

        world.setDelta(DELAY - 1);
        world.process();

        // e2 is also deleted.
        Assert.assertFalse(e2.isActive());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExpirationWithTimerReuse() {
        World world = new World();
        world.setSystem(new ExpirationEntitySystem(Filter.allComponents(ComponentA.class), DELAY));
        world.initialize();

        Entity e = world.createEntity();
        e.addComponent(world.createComponent(ComponentA.class));
        e.addToWorld();

        world.process();

        Assert.assertTrue(e.isActive());

        world.setDelta(DELAY + 1);
        world.process();

        // entity deleted here, but it's technically still active
        Assert.assertTrue(e.isActive());

        world.setDelta(DELAY - 1);
        world.process();

        // everyone is notified
        Assert.assertFalse(e.isActive());

        Entity e2 = world.createEntity();
        e2.addComponent(world.createComponent(ComponentA.class));
        e2.addToWorld();


        world.setDelta(DELAY + 1);
        world.process();

        // entity deleted here, but it's technically still active
        Assert.assertTrue(e2.isActive());


        world.setDelta(DELAY - 1);
        world.process();

        // everyone is notified
        Assert.assertFalse(e2.isActive());
    }
}
