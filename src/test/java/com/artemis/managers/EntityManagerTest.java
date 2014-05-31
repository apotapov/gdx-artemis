package com.artemis.managers;

import com.artemis.*;
import com.badlogic.gdx.utils.Array;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EntityManagerTest {

    private World world;
    private Array<Entity> entityArray;
    private static int ENTITIES_LENGTH = 3;

    @Before
    public void before(){
        world = new World();
        entityArray = new Array<Entity>();
        // Add initial entities
        for(int i=0;i<ENTITIES_LENGTH;i++) {
            entityArray.add(world.getEntityManager().createEntityInstance());
        }
    }

    @Test
    public void testCreateNewEntityInstance() {

        assertIdIncrement();

        // Assert that the expected amount of entities were created.
        Assert.assertEquals(ENTITIES_LENGTH,entityArray.size);

        // Assert that entities are not null and not the same.
        assertEntitiesDifferent();
    }

    @Test
    public void testReuseEntityInstance(){

        // Delete all entities to allow for reuse.
        for(Entity entity:entityArray){
            world.getEntityManager().deleted(entity);
        }

        world.process();

        // Assert that entities were added to entityPool
        Assert.assertEquals(ENTITIES_LENGTH,world.getEntityManager().entityPool.getFree());

        // Entity id should not be reused from entityPool, so set id to a value that will fail tests if it is.
        for(Entity entity:entityArray){
            entity.id = -1;
        }

        entityArray.clear();

        // Add new entities from entity pool.
        for(int i=0;i<ENTITIES_LENGTH;i++) {
            entityArray.add(world.getEntityManager().createEntityInstance());
        }

        // Assert that the expected amount of entities were created.
        Assert.assertEquals(ENTITIES_LENGTH,entityArray.size);

        // Assert that entities were reused
        Assert.assertEquals(0,world.getEntityManager().entityPool.getFree());

        assertIdIncrement();
        assertEntitiesDifferent();

        world.process();
    }

    private void assertIdIncrement(){
        // Find last used id
        int lastId = -1;
        for(Entity entity:entityArray){
            if(entity.id>lastId){
                lastId=entity.id;
            }
        }
        // Assert that ids were incremented correctly
        Assert.assertEquals(ENTITIES_LENGTH-1,lastId);
    }

    private void assertEntitiesDifferent(){
        for(int i=0;i<entityArray.size;i++){
            Entity entityA = entityArray.get(i);
            Assert.assertNotSame(null,entityA);
            for(int j=i+1;j<entityArray.size;j++){
                Entity entityB = entityArray.get(j);
                Assert.assertNotSame(entityA.id, entityB.id);
                Assert.assertNotSame(entityA, entityB);
            }
        }
    }

}
