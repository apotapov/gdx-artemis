package com.artemis.utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.utils.Array;

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
