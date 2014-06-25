package com.artemis.utils;

import com.badlogic.gdx.utils.Array;
import org.junit.*;

public class SafeArrayTest {

    @BeforeClass
    public static void beforeClass() {
    }

    @AfterClass
    public static void afterClass() {
    }

    @Before
    public void before() {
    }

    @After
    public void after() {
    }

    @Test
    public void testGetSet() {
        Array<String> array = new SafeArray<String>();
        Assert.assertEquals(null, array.get(0));
        array.add("first");
        Assert.assertEquals("first", array.get(0));
        Assert.assertEquals(null, array.get(1));
        array.set(1, "second");
        Assert.assertEquals("second", array.get(1));
    }

}
