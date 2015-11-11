package com.artemis.managers;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import junit.framework.Assert;
import org.junit.Test;

public class GenericGroupManagerTest {

    private static enum Group {
        GROUP_1,
        GROUP_2,
        GROUP_3
    }

    private static class TestGroupManager extends GenericGroupManager<Group> {

    }

    @Test
    public void testInGroup() {
        World world = new World();
        world.setManager(new TestGroupManager());
        world.initialize();

        TestGroupManager manager = world.getManager(TestGroupManager.class);

        Entity e = world.createEntity();
        manager.add(e, Group.GROUP_1);
        world.addEntity(e);

        Assert.assertTrue(manager.isInAnyGroup(e));
        Assert.assertTrue(manager.isInGroup(e, Group.GROUP_1));

        world.process();

        Assert.assertTrue(manager.isInAnyGroup(e));
        Assert.assertTrue(manager.isInGroup(e, Group.GROUP_1));

        manager.remove(e, Group.GROUP_1);

        Assert.assertFalse(manager.isInAnyGroup(e));
        Assert.assertFalse(manager.isInGroup(e, Group.GROUP_1));

        world.process();
    }

    @Test
    public void testRemoveAll() {
        World world = new World();
        world.setManager(new TestGroupManager());
        world.initialize();

        TestGroupManager manager = world.getManager(TestGroupManager.class);

        Entity e = world.createEntity();
        manager.add(e, Group.GROUP_1);
        manager.add(e, Group.GROUP_2);
        manager.add(e, Group.GROUP_3);
        world.addEntity(e);

        Assert.assertTrue(manager.isInAnyGroup(e));
        Assert.assertTrue(manager.isInGroup(e, Group.GROUP_1));
        Assert.assertTrue(manager.isInGroup(e, Group.GROUP_2));
        Assert.assertTrue(manager.isInGroup(e, Group.GROUP_3));

        world.process();

        Assert.assertTrue(manager.isInAnyGroup(e));
        Assert.assertTrue(manager.isInGroup(e, Group.GROUP_1));
        Assert.assertTrue(manager.isInGroup(e, Group.GROUP_2));
        Assert.assertTrue(manager.isInGroup(e, Group.GROUP_3));

        manager.removeFromAllGroups(e);

        Assert.assertFalse(manager.isInAnyGroup(e));
        Assert.assertFalse(manager.isInGroup(e, Group.GROUP_1));
        Assert.assertFalse(manager.isInGroup(e, Group.GROUP_2));
        Assert.assertFalse(manager.isInGroup(e, Group.GROUP_3));


        Entity e2 = world.createEntity();
        manager.add(e2, Group.GROUP_1);
        manager.add(e2, Group.GROUP_2);
        manager.add(e2, Group.GROUP_3);

        Assert.assertFalse(manager.isInAnyGroup(e));
        Assert.assertFalse(manager.isInGroup(e, Group.GROUP_1));
        Assert.assertFalse(manager.isInGroup(e, Group.GROUP_2));
        Assert.assertFalse(manager.isInGroup(e, Group.GROUP_3));

        Assert.assertTrue(manager.isInAnyGroup(e2));
        Assert.assertTrue(manager.isInGroup(e2, Group.GROUP_1));
        Assert.assertTrue(manager.isInGroup(e2, Group.GROUP_2));
        Assert.assertTrue(manager.isInGroup(e2, Group.GROUP_3));

        manager.removeFromAllGroups(e2);

        Assert.assertFalse(manager.isInAnyGroup(e));
        Assert.assertFalse(manager.isInGroup(e, Group.GROUP_1));
        Assert.assertFalse(manager.isInGroup(e, Group.GROUP_2));
        Assert.assertFalse(manager.isInGroup(e, Group.GROUP_3));

        Assert.assertFalse(manager.isInAnyGroup(e2));
        Assert.assertFalse(manager.isInGroup(e2, Group.GROUP_1));
        Assert.assertFalse(manager.isInGroup(e2, Group.GROUP_2));
        Assert.assertFalse(manager.isInGroup(e2, Group.GROUP_3));

        manager.add(e, Group.GROUP_1);
        manager.add(e2, Group.GROUP_2);
        manager.add(e, Group.GROUP_3);

        Assert.assertTrue(manager.isInAnyGroup(e));
        Assert.assertTrue(manager.isInGroup(e, Group.GROUP_1));
        Assert.assertFalse(manager.isInGroup(e, Group.GROUP_2));
        Assert.assertTrue(manager.isInGroup(e, Group.GROUP_3));

        Assert.assertTrue(manager.isInAnyGroup(e2));
        Assert.assertFalse(manager.isInGroup(e2, Group.GROUP_1));
        Assert.assertTrue(manager.isInGroup(e2, Group.GROUP_2));
        Assert.assertFalse(manager.isInGroup(e2, Group.GROUP_3));

        manager.removeFromAllGroups(e);

        Assert.assertFalse(manager.isInAnyGroup(e));
        Assert.assertFalse(manager.isInGroup(e, Group.GROUP_1));
        Assert.assertFalse(manager.isInGroup(e, Group.GROUP_2));
        Assert.assertFalse(manager.isInGroup(e, Group.GROUP_3));

        Assert.assertTrue(manager.isInAnyGroup(e2));
        Assert.assertFalse(manager.isInGroup(e2, Group.GROUP_1));
        Assert.assertTrue(manager.isInGroup(e2, Group.GROUP_2));
        Assert.assertFalse(manager.isInGroup(e2, Group.GROUP_3));

        manager.removeFromAllGroups(e2);

        Assert.assertFalse(manager.isInAnyGroup(e));
        Assert.assertFalse(manager.isInGroup(e, Group.GROUP_1));
        Assert.assertFalse(manager.isInGroup(e, Group.GROUP_2));
        Assert.assertFalse(manager.isInGroup(e, Group.GROUP_3));

        Assert.assertFalse(manager.isInAnyGroup(e2));
        Assert.assertFalse(manager.isInGroup(e2, Group.GROUP_1));
        Assert.assertFalse(manager.isInGroup(e2, Group.GROUP_2));
        Assert.assertFalse(manager.isInGroup(e2, Group.GROUP_3));
    }

    @Test
    public void testGetGroups() {
        World world = new World();
        world.setManager(new TestGroupManager());
        world.initialize();

        Entity e = world.createEntity();

        TestGroupManager manager = world.getManager(TestGroupManager.class);

        Assert.assertTrue(manager.getGroups(e).size == 0);

        manager.add(e, Group.GROUP_1);
        manager.add(e, Group.GROUP_2);
        manager.add(e, Group.GROUP_3);
        world.addEntity(e);

        Array<Group> groups = manager.getGroups(e);
        Assert.assertTrue(groups.size == 3);
        Assert.assertTrue(groups.contains(Group.GROUP_1, false));
        Assert.assertTrue(groups.contains(Group.GROUP_2, false));
        Assert.assertTrue(groups.contains(Group.GROUP_3, false));

        manager.remove(e, Group.GROUP_1);
        groups = manager.getGroups(e);
        Assert.assertTrue(groups.size == 2);
        Assert.assertFalse(groups.contains(Group.GROUP_1, false));
        Assert.assertTrue(groups.contains(Group.GROUP_2, false));
        Assert.assertTrue(groups.contains(Group.GROUP_3, false));

        manager.removeFromAllGroups(e);

        groups = manager.getGroups(e);
        Assert.assertTrue(groups.size == 0);
    }

    @Test
    public void testGetEntities() {
        World world = new World();
        world.setManager(new TestGroupManager());
        world.initialize();

        TestGroupManager manager = world.getManager(TestGroupManager.class);

        Assert.assertTrue(manager.getEntities(Group.GROUP_1).size == 0);

        Entity e = world.createEntity();
        manager.add(e, Group.GROUP_1);
        manager.add(e, Group.GROUP_2);
        manager.add(e, Group.GROUP_3);
        world.addEntity(e);

        Entity e2 = world.createEntity();
        manager.add(e2, Group.GROUP_2);
        world.addEntity(e2);

        Array<Entity> groups = manager.getEntities(Group.GROUP_1);
        Assert.assertTrue(groups.size == 1);
        Assert.assertTrue(groups.contains(e, true));
        Assert.assertFalse(groups.contains(e2, true));

        groups = manager.getEntities(Group.GROUP_2);
        Assert.assertTrue(groups.size == 2);
        Assert.assertTrue(groups.contains(e, true));
        Assert.assertTrue(groups.contains(e2, true));

        manager.remove(e, Group.GROUP_1);
        groups = manager.getEntities(Group.GROUP_1);
        Assert.assertTrue(groups.size == 0);

        manager.remove(e2, Group.GROUP_2);
        groups = manager.getEntities(Group.GROUP_2);
        Assert.assertTrue(groups.size == 1);
        Assert.assertTrue(groups.contains(e, true));
        Assert.assertFalse(groups.contains(e2, true));

        manager.removeFromAllGroups(e);

        groups = manager.getEntities(Group.GROUP_2);
        Assert.assertTrue(groups.size == 0);

        groups = manager.getEntities(Group.GROUP_1);
        Assert.assertTrue(groups.size == 0);

        groups = manager.getEntities(Group.GROUP_3);
        Assert.assertTrue(groups.size == 0);
    }

}
