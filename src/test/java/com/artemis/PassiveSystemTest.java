package com.artemis;

import junit.framework.Assert;

import org.junit.Test;

import com.artemis.systems.EntitySystem;
import com.artemis.systems.VoidEntitySystem;

public class PassiveSystemTest {

    static class TestActiveSystem extends VoidEntitySystem {
        @Override
        protected void processSystem() {
            Assert.assertFalse(isPassive());
        }
    }

    static class TestPassiveSystem extends VoidEntitySystem {
        @Override
        protected void processSystem() {
            // this should not execute since the system is passive
            Assert.fail();
        }
    }

    @Test
    public void testPassive() {
        World world = new World();

        world.setSystem(new TestActiveSystem());

        EntitySystem passive = new TestPassiveSystem();
        passive.setPassive(true);
        world.setSystem(passive);

        world.initialize();
        world.process();
    }
}
