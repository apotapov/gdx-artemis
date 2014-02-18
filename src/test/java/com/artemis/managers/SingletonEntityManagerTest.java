package com.artemis.managers;

import junit.framework.Assert;

import org.junit.Test;

import com.artemis.Entity;
import com.artemis.World;

public class SingletonEntityManagerTest {

    private static final String PLAYER = "player";
    private static final String BOSS = "boss";

    @Test
    public void testSimple() {
        World world = new World();
        world.setManager(new SingletonEntityManager());
        world.initialize();

        SingletonEntityManager manager = world.getManager(SingletonEntityManager.class);


        Entity entity = world.createEntity();
        world.addEntity(entity);

        manager.setName(entity, PLAYER);

        Assert.assertTrue(manager.isSet(PLAYER));
        Assert.assertFalse(manager.isSet(BOSS));
        Assert.assertEquals(entity, manager.getEntity(PLAYER));
        Assert.assertNull(manager.getEntity(BOSS));
        Assert.assertEquals(PLAYER, manager.getNames().next());

        manager.setName(entity, BOSS);

        Assert.assertFalse(manager.isSet(PLAYER));
        Assert.assertTrue(manager.isSet(BOSS));
        Assert.assertEquals(entity, manager.getEntity(BOSS));
        Assert.assertNull(manager.getEntity(PLAYER));
        Assert.assertEquals(BOSS, manager.getNames().next());

        manager.remove(BOSS);

        Assert.assertFalse(manager.isSet(PLAYER));
        Assert.assertFalse(manager.isSet(BOSS));
        Assert.assertNull(manager.getEntity(BOSS));
        Assert.assertNull(manager.getEntity(PLAYER));
        Assert.assertFalse(manager.getNames().hasNext());

        manager.setName(entity, PLAYER);
        manager.remove(entity);

        Assert.assertFalse(manager.isSet(PLAYER));
        Assert.assertFalse(manager.isSet(BOSS));
        Assert.assertNull(manager.getEntity(BOSS));
        Assert.assertNull(manager.getEntity(PLAYER));
        Assert.assertFalse(manager.getNames().hasNext());
    }

    @Test
    public void testDeleteEntity() {
        World world = new World();
        world.setManager(new SingletonEntityManager());
        world.initialize();

        SingletonEntityManager manager = world.getManager(SingletonEntityManager.class);

        Entity entity = world.createEntity();
        world.addEntity(entity);

        manager.setName(entity, PLAYER);

        Assert.assertTrue(manager.isSet(PLAYER));

        world.process();

        Assert.assertTrue(manager.isSet(PLAYER));

        entity.deleteFromWorld();

        world.process();

        Assert.assertFalse(manager.isSet(PLAYER));
    }

}
