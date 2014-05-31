package com.artemis.utils;

import com.badlogic.gdx.utils.Array;
import org.junit.*;

public class IdentifierTest {

    private IdentifierPool identifierPool;
    private static int ID_COUNT = 3;

    @BeforeClass
    public static void beforeClass() {

    }

    @AfterClass
    public static void afterClass() {
    }

    @Before
    public void before() {
        identifierPool = new IdentifierPool();
        // Check in some id's
        for(int i=0;i< ID_COUNT;i++){
            identifierPool.checkIn(i);
        }
    }

    @After
    public void after() {
    }

    @Test
    public void testCheckIn() {
        // Check out all id's
        for(int i=0;i< ID_COUNT;i++){
            identifierPool.checkOut();
        }
        // Check that id pool is empty
        Assert.assertEquals(identifierPool.ids.size,0);

        // Check in some new id's
        for(int i=1;i< ID_COUNT+1;i++){
            identifierPool.checkIn(i);
        }
        // Check that id pool has expected size
        Assert.assertEquals(identifierPool.ids.size,ID_COUNT);
    }

    @Test
    public void testCheckOut() {
        Array<Integer> idArray = new Array<Integer>();
        // Add all id's created in before() to an integer array for comparison.
        for(int i=0;i<ID_COUNT;i++) {
            idArray.add(identifierPool.checkOut());
        }
        // Assert that pool is empty
        Assert.assertEquals(identifierPool.ids.size,0);

        // Add some new id's to array that have not been pooled
        idArray.add(identifierPool.checkOut());
        idArray.add(identifierPool.checkOut());

        Assert.assertEquals(ID_COUNT+2,idArray.size);

        // Assert that all ids are different.
        for(int i=0;i<ID_COUNT;i++){
            for(int j=i+1;j<ID_COUNT;j++){
                Assert.assertNotSame(idArray.get(i), idArray.get(j));
            }
        }
    }

    @Test
    public void testDispose() {
        identifierPool.dispose();
        Assert.assertEquals(identifierPool.ids.size,0);
    }

}