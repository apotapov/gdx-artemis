package com.artemis.managers;

import junit.framework.Assert;

import org.junit.Test;

import com.artemis.Entity;
import com.artemis.World;

public class GroupManagerTest {

    private static final String TEST_GROUP = "test";
    private static final String TEST_GROUP_2 = "test2";
    private static final String TEST_GROUP_3 = "test3";

    @Test
    public void testInGroup() {
        World world = new World();
        GroupManager manager = new GroupManager();
        world.setManager(manager);
        world.initialize();

        Entity e = world.createEntity();
        manager.add(e, TEST_GROUP);
        world.addEntity(e);

        Assert.assertTrue(manager.isInAnyGroup(e));
        Assert.assertTrue(manager.isInGroup(e, TEST_GROUP));

        world.process();

        Assert.assertTrue(manager.isInAnyGroup(e));
        Assert.assertTrue(manager.isInGroup(e, TEST_GROUP));

        manager.remove(e, TEST_GROUP);

        Assert.assertFalse(manager.isInAnyGroup(e));
        Assert.assertFalse(manager.isInGroup(e, TEST_GROUP));

        world.process();
    }

    @Test
    public void testRemoveAll() {
        World world = new World();
        GroupManager manager = new GroupManager();
        world.setManager(manager);
        world.initialize();

        Entity e = world.createEntity();
        manager.add(e, TEST_GROUP);
        manager.add(e, TEST_GROUP_2);
        manager.add(e, TEST_GROUP_3);
        world.addEntity(e);

        Assert.assertTrue(manager.isInAnyGroup(e));
        Assert.assertTrue(manager.isInGroup(e, TEST_GROUP));
        Assert.assertTrue(manager.isInGroup(e, TEST_GROUP_2));
        Assert.assertTrue(manager.isInGroup(e, TEST_GROUP_3));

        world.process();

        Assert.assertTrue(manager.isInAnyGroup(e));
        Assert.assertTrue(manager.isInGroup(e, TEST_GROUP));
        Assert.assertTrue(manager.isInGroup(e, TEST_GROUP_2));
        Assert.assertTrue(manager.isInGroup(e, TEST_GROUP_3));

        manager.removeFromAllGroups(e);

        Assert.assertFalse(manager.isInAnyGroup(e));
        Assert.assertFalse(manager.isInGroup(e, TEST_GROUP));
        Assert.assertFalse(manager.isInGroup(e, TEST_GROUP_2));
        Assert.assertFalse(manager.isInGroup(e, TEST_GROUP_3));


        Entity e2 = world.createEntity();
        manager.add(e2, TEST_GROUP);
        manager.add(e2, TEST_GROUP_2);
        manager.add(e2, TEST_GROUP_3);

        Assert.assertFalse(manager.isInAnyGroup(e));
        Assert.assertFalse(manager.isInGroup(e, TEST_GROUP));
        Assert.assertFalse(manager.isInGroup(e, TEST_GROUP_2));
        Assert.assertFalse(manager.isInGroup(e, TEST_GROUP_3));

        Assert.assertTrue(manager.isInAnyGroup(e2));
        Assert.assertTrue(manager.isInGroup(e2, TEST_GROUP));
        Assert.assertTrue(manager.isInGroup(e2, TEST_GROUP_2));
        Assert.assertTrue(manager.isInGroup(e2, TEST_GROUP_3));

        manager.removeFromAllGroups(e2);

        Assert.assertFalse(manager.isInAnyGroup(e));
        Assert.assertFalse(manager.isInGroup(e, TEST_GROUP));
        Assert.assertFalse(manager.isInGroup(e, TEST_GROUP_2));
        Assert.assertFalse(manager.isInGroup(e, TEST_GROUP_3));

        Assert.assertFalse(manager.isInAnyGroup(e2));
        Assert.assertFalse(manager.isInGroup(e2, TEST_GROUP));
        Assert.assertFalse(manager.isInGroup(e2, TEST_GROUP_2));
        Assert.assertFalse(manager.isInGroup(e2, TEST_GROUP_3));

        manager.add(e, TEST_GROUP);
        manager.add(e2, TEST_GROUP_2);
        manager.add(e, TEST_GROUP_3);

        Assert.assertTrue(manager.isInAnyGroup(e));
        Assert.assertTrue(manager.isInGroup(e, TEST_GROUP));
        Assert.assertFalse(manager.isInGroup(e, TEST_GROUP_2));
        Assert.assertTrue(manager.isInGroup(e, TEST_GROUP_3));

        Assert.assertTrue(manager.isInAnyGroup(e2));
        Assert.assertFalse(manager.isInGroup(e2, TEST_GROUP));
        Assert.assertTrue(manager.isInGroup(e2, TEST_GROUP_2));
        Assert.assertFalse(manager.isInGroup(e2, TEST_GROUP_3));

        manager.removeFromAllGroups(e);

        Assert.assertFalse(manager.isInAnyGroup(e));
        Assert.assertFalse(manager.isInGroup(e, TEST_GROUP));
        Assert.assertFalse(manager.isInGroup(e, TEST_GROUP_2));
        Assert.assertFalse(manager.isInGroup(e, TEST_GROUP_3));

        Assert.assertTrue(manager.isInAnyGroup(e2));
        Assert.assertFalse(manager.isInGroup(e2, TEST_GROUP));
        Assert.assertTrue(manager.isInGroup(e2, TEST_GROUP_2));
        Assert.assertFalse(manager.isInGroup(e2, TEST_GROUP_3));

        manager.removeFromAllGroups(e2);

        Assert.assertFalse(manager.isInAnyGroup(e));
        Assert.assertFalse(manager.isInGroup(e, TEST_GROUP));
        Assert.assertFalse(manager.isInGroup(e, TEST_GROUP_2));
        Assert.assertFalse(manager.isInGroup(e, TEST_GROUP_3));

        Assert.assertFalse(manager.isInAnyGroup(e2));
        Assert.assertFalse(manager.isInGroup(e2, TEST_GROUP));
        Assert.assertFalse(manager.isInGroup(e2, TEST_GROUP_2));
        Assert.assertFalse(manager.isInGroup(e2, TEST_GROUP_3));
    }

}
