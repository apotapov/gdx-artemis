package com.artemis.systems.event;

import junit.framework.Assert;

import org.junit.Test;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.Filter;
import com.artemis.World;
import com.artemis.systems.VoidEntitySystem;

public class EventProcessingSystemTest {

    private static class TestEvent1 extends SystemEvent {

        @Override
        protected void resetForPooling() {
        }

    }
    private static class TestEvent2 extends SystemEvent {

        @Override
        protected void resetForPooling() {
        }

    }
    private static class TestEvent3 extends SystemEvent {

        @Override
        protected void resetForPooling() {
        }

    }

    static class ComponentA implements Component {
        @Override
        public void reset() {
        }
    }

    static class PublishingSystem extends VoidEntitySystem {

        @Override
        protected void processSystem() {
            world.postEvent(this, world.createEvent(TestEvent1.class));
            world.postEvent(this, world.createEvent(TestEvent2.class));
            world.postEvent(this, world.createEvent(TestEvent3.class));
        }
    }

    static class ReceivingSystem1 extends EventProcessingSystem<TestEvent1> {

        int count = 0;

        @SuppressWarnings("unchecked")
        public ReceivingSystem1() {
            super(Filter.allComponents(ComponentA.class), TestEvent1.class);
        }

        @Override
        protected void processEvent(Entity e, TestEvent1 event) {
            count++;
        }

    }

    static class ReceivingSystem2 extends EventProcessingSystem2<TestEvent1, TestEvent2> {

        int count = 0;

        @SuppressWarnings("unchecked")
        public ReceivingSystem2() {
            super(Filter.allComponents(ComponentA.class), TestEvent1.class, TestEvent2.class);
        }

        @Override
        protected void processEvent(Entity e, TestEvent1 event) {
            count++;
        }

        @Override
        protected void processEvent2(Entity e, TestEvent2 event) {
            count++;
        }

    }

    static class ReceivingSystem3 extends EventProcessingSystem3<TestEvent1, TestEvent2, TestEvent3> {

        int count = 0;

        @SuppressWarnings("unchecked")
        public ReceivingSystem3() {
            super(Filter.allComponents(ComponentA.class), TestEvent1.class, TestEvent2.class, TestEvent3.class);
        }

        @Override
        protected void processEvent(Entity e, TestEvent1 event) {
            count++;
        }

        @Override
        protected void processEvent2(Entity e, TestEvent2 event) {
            count++;
        }

        @Override
        protected void processEvent3(Entity e, TestEvent3 event) {
            count++;
        }
    }

    static class VoidReceivingSystem1 extends EventVoidSystem<TestEvent1> {
        int count = 0;

        public VoidReceivingSystem1() {
            super(TestEvent1.class);
        }
        @Override
        protected void processEvent(TestEvent1 event) {
            count++;
        }
    }

    static class VoidReceivingSystem2 extends EventVoidSystem2<TestEvent1, TestEvent2> {
        int count = 0;

        public VoidReceivingSystem2() {
            super(TestEvent1.class, TestEvent2.class);
        }
        @Override
        protected void processEvent(TestEvent1 event) {
            count++;
        }
        @Override
        protected void processEvent2(TestEvent2 event) {
            count++;
        }
    }

    static class VoidReceivingSystem3 extends EventVoidSystem3<TestEvent1, TestEvent2, TestEvent3> {
        int count = 0;

        public VoidReceivingSystem3() {
            super(TestEvent1.class, TestEvent2.class, TestEvent3.class);
        }
        @Override
        protected void processEvent(TestEvent1 event) {
            count++;
        }
        @Override
        protected void processEvent2(TestEvent2 event) {
            count++;
        }
        @Override
        protected void processEvent3(TestEvent3 event) {
            count++;
        }
    }

    @Test
    public void testEventProcessingSystems() {
        World world = new World();
        world.setEventDeliverySystem(new BasicEventDeliverySystem());
        world.setSystem(new PublishingSystem());
        world.setSystem(new ReceivingSystem1());
        world.setSystem(new ReceivingSystem2());
        world.setSystem(new ReceivingSystem3());
        world.setSystem(new VoidReceivingSystem1());
        world.setSystem(new VoidReceivingSystem2());
        world.setSystem(new VoidReceivingSystem3());
        world.initialize();

        Entity e = world.createEntity();
        e.addComponent(world.createComponent(ComponentA.class));
        world.addEntity(e);

        Assert.assertEquals(0, world.getSystem(ReceivingSystem1.class).count);
        Assert.assertEquals(0, world.getSystem(ReceivingSystem2.class).count);
        Assert.assertEquals(0, world.getSystem(ReceivingSystem3.class).count);
        Assert.assertEquals(0, world.getSystem(VoidReceivingSystem1.class).count);
        Assert.assertEquals(0, world.getSystem(VoidReceivingSystem2.class).count);
        Assert.assertEquals(0, world.getSystem(VoidReceivingSystem3.class).count);

        world.process();

        Assert.assertEquals(0, world.getSystem(ReceivingSystem1.class).count);
        Assert.assertEquals(0, world.getSystem(ReceivingSystem2.class).count);
        Assert.assertEquals(0, world.getSystem(ReceivingSystem3.class).count);
        Assert.assertEquals(0, world.getSystem(VoidReceivingSystem1.class).count);
        Assert.assertEquals(0, world.getSystem(VoidReceivingSystem2.class).count);
        Assert.assertEquals(0, world.getSystem(VoidReceivingSystem3.class).count);

        world.process();

        Assert.assertEquals(1, world.getSystem(ReceivingSystem1.class).count);
        Assert.assertEquals(2, world.getSystem(ReceivingSystem2.class).count);
        Assert.assertEquals(3, world.getSystem(ReceivingSystem3.class).count);
        Assert.assertEquals(1, world.getSystem(VoidReceivingSystem1.class).count);
        Assert.assertEquals(2, world.getSystem(VoidReceivingSystem2.class).count);
        Assert.assertEquals(3, world.getSystem(VoidReceivingSystem3.class).count);

        world.process();

        Assert.assertEquals(2, world.getSystem(ReceivingSystem1.class).count);
        Assert.assertEquals(4, world.getSystem(ReceivingSystem2.class).count);
        Assert.assertEquals(6, world.getSystem(ReceivingSystem3.class).count);
        Assert.assertEquals(2, world.getSystem(VoidReceivingSystem1.class).count);
        Assert.assertEquals(4, world.getSystem(VoidReceivingSystem2.class).count);
        Assert.assertEquals(6, world.getSystem(VoidReceivingSystem3.class).count);
    }
}
