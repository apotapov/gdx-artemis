package com.artemis.systems.event;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.artemis.World;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;

/**
 * Tests out the basic event system, with simple send/receive mechanic
 * @author apotapov
 *
 */
public class BasicEventSystemTest {

    private static class TestEvent extends SystemEvent {

        @Override
        protected void resetForPooling() {
        }

    }

    private static class SendingSystem extends VoidEntitySystem {
        @Override
        protected void processSystem() {
            TestEvent event = SystemEvent.createEvent(TestEvent.class);
            world.postEvent(this, event);
        }
    }

    private static class ReceivingSystem extends VoidEntitySystem {

        IntArray array = new IntArray();

        @Override
        protected void processSystem() {
            Array<TestEvent> events = new Array<TestEvent>();
            world.getEvents(this, TestEvent.class, events);
            for (TestEvent event : events) {
                array.add(event.eventId);
            }
        }
    }

    World world;

    @Before
    public void before() {
        world = new World();
        world.setSystem(new BasicEventSystem());
        world.setSystem(new SendingSystem());
        world.setSystem(new ReceivingSystem());
        world.initialize();
        world.setDelta(1);
    }

    /**
     * Test whether the event system sends in events correctly.
     */
    @Test
    public void testSimple() {
        Assert.assertEquals(0, world.getSystem(ReceivingSystem.class).array.size);

        // after the first process the first test event is buffered
        world.process();

        Assert.assertEquals(0, world.getSystem(ReceivingSystem.class).array.size);

        // after the second it should be received by the receiving system
        world.process();

        Assert.assertEquals(1, world.getSystem(ReceivingSystem.class).array.size);
        Assert.assertEquals(0, world.getSystem(ReceivingSystem.class).array.get(0));

        // after the third two events should be in the system and one in the buffer
        world.process();

        Assert.assertEquals(2, world.getSystem(ReceivingSystem.class).array.size);
        Assert.assertEquals(0, world.getSystem(ReceivingSystem.class).array.get(0));
        Assert.assertEquals(1, world.getSystem(ReceivingSystem.class).array.get(1));
    }

}
