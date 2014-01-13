package com.artemis.systems;

import junit.framework.Assert;

import org.junit.Test;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;

public class DelayedProcessingEntitySystemTest {

    static class Expires implements Component {
        public float delay;

        @Override
        public void reset() {
            delay = 0;
        }
    }

    static class ExpiringSystem extends DelayedEntityProcessingSystem {

        ComponentMapper<Expires> em;

        @SuppressWarnings("unchecked")
        public ExpiringSystem() {
            super(Aspect.getAspectForAll(Expires.class));
        }

        @Override
        public void initialize() {
            em = world.getMapper(Expires.class);
        }

        @Override
        protected float getRemainingDelay(Entity e) {
            Expires expires = em.get(e);
            return expires.delay;
        }

        @Override
        protected void processDelta(Entity e, float accumulatedDelta) {
            Expires expires = em.get(e);
            expires.delay -= accumulatedDelta;
        }

        @Override
        protected void processExpired(Entity e) {
            e.deleteFromWorld();
        }
    }

    @Test
    public void testDelayedSystem() {
        World world = new World();
        world.setSystem(new ExpiringSystem());
        world.initialize();

        Entity e = world.createEntity();
        Expires ex = world.createComponent(Expires.class);
        ex.delay = 1;
        e.addComponent(ex);
        world.addEntity(e);
        int id = e.id;

        world.setDelta(0.5f);
        world.process();
        world.setDelta(0.7f);
        world.process();

        // the entity actually gets deleted on the second iteration of process.
        world.process();
        Assert.assertEquals(null, world.getEntity(id));
    }
}
