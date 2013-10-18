package com.artemis;

import junit.framework.Assert;

import org.junit.Test;

public class ComponentMapperTest {

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

    @Test
    public void testComponentCaching() {
        World world = new World();
        world.initialize();
        ComponentMapper<ComponentA> mapper1 = ComponentMapper.getFor(ComponentA.class, world);
        ComponentMapper<ComponentA> mapper2 = ComponentMapper.getFor(ComponentA.class, world);
        Assert.assertTrue(mapper1 == mapper2);
    }

}
