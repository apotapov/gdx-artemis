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
        ComponentMapper<ComponentA> mapper1 = world.getMapper(ComponentA.class);
        ComponentMapper<ComponentA> mapper2 = world.getMapper(ComponentA.class);
        Assert.assertTrue(mapper1 == mapper2);
    }

}
