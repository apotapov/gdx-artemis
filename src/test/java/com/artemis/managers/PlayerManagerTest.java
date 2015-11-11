package com.artemis.managers;

import com.artemis.Entity;
import com.artemis.World;
import junit.framework.Assert;
import org.junit.Test;

public class PlayerManagerTest {

    private static final String PLAYER_1 = "p1";
    private static final String PLAYER_2 = "p2";
    private static final String PLAYER_3 = "p3";

    @Test
    public void testSimple() {
        World world = new World();
        world.setManager(new PlayerManager());
        world.initialize();

        PlayerManager manager = world.getManager(PlayerManager.class);

        Entity entity1 = world.createEntity();
        world.addEntity(entity1);

        Assert.assertNull(manager.getPlayer(entity1));
        Assert.assertEquals(0, manager.getEntitiesOfPlayer(PLAYER_1).size);

        manager.setPlayer(entity1, PLAYER_1);

        Assert.assertEquals(PLAYER_1, manager.getPlayer(entity1));
        Assert.assertEquals(1, manager.getEntitiesOfPlayer(PLAYER_1).size);
        Assert.assertEquals(0, manager.getEntitiesOfPlayer(PLAYER_2).size);

        manager.setPlayer(entity1, PLAYER_2);

        Assert.assertEquals(PLAYER_2, manager.getPlayer(entity1));
        Assert.assertEquals(1, manager.getEntitiesOfPlayer(PLAYER_2).size);
        Assert.assertEquals(0, manager.getEntitiesOfPlayer(PLAYER_1).size);

        manager.removeFromPlayer(entity1);

        Assert.assertNull(manager.getPlayer(entity1));
        Assert.assertEquals(0, manager.getEntitiesOfPlayer(PLAYER_1).size);
        Assert.assertEquals(0, manager.getEntitiesOfPlayer(PLAYER_2).size);

        manager.setPlayer(entity1, PLAYER_1);
        manager.setPlayer(entity1, PLAYER_1);
        Assert.assertEquals(PLAYER_1, manager.getPlayer(entity1));
        Assert.assertEquals(1, manager.getEntitiesOfPlayer(PLAYER_1).size);
        Assert.assertEquals(0, manager.getEntitiesOfPlayer(PLAYER_2).size);


        Entity entity2 = world.createEntity();
        world.addEntity(entity2);

        manager.setPlayer(entity2, PLAYER_1);
        manager.setPlayer(entity1, PLAYER_3);
        Assert.assertEquals(PLAYER_3, manager.getPlayer(entity1));
        Assert.assertEquals(PLAYER_1, manager.getPlayer(entity2));
        Assert.assertEquals(1, manager.getEntitiesOfPlayer(PLAYER_3).size);
        Assert.assertEquals(1, manager.getEntitiesOfPlayer(PLAYER_1).size);
    }

    @Test
    public void testDeleteEntity() {
        World world = new World();
        world.setManager(new PlayerManager());
        world.initialize();

        PlayerManager manager = world.getManager(PlayerManager.class);

        Entity entity = world.createEntity();
        world.addEntity(entity);

        manager.setPlayer(entity, PLAYER_1);

        Assert.assertEquals(PLAYER_1, manager.getPlayer(entity));
        Assert.assertEquals(1, manager.getEntitiesOfPlayer(PLAYER_1).size);

        world.process();

        Assert.assertEquals(PLAYER_1, manager.getPlayer(entity));
        Assert.assertEquals(1, manager.getEntitiesOfPlayer(PLAYER_1).size);

        entity.deleteFromWorld();

        world.process();

        Assert.assertNull(manager.getPlayer(entity));
        Assert.assertEquals(0, manager.getEntitiesOfPlayer(PLAYER_1).size);
    }

}
