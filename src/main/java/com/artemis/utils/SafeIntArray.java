package com.artemis.utils;

import com.badlogic.gdx.utils.IntArray;


/**
 * A non-throwing version of a libgx Array. I is used in many places
 * internally to return a null instead of throwing an index out
 * of bounds exception.
 * 
 * The SafeArray was introduced after a transition from original
 * Artemis's Bag implementation which behaved slightly differently
 * from libgdx Array.
 * 
 * The hope is to eventually refactor the code of gdx-artemis
 * such that it no longer has to rely on this implementation.
 * Seems like a scary project...
 * 
 * @author apotapov
 *
 */
public class SafeIntArray extends IntArray {

    public SafeIntArray() {
        super();
    }

    public SafeIntArray(boolean ordered, int capacity) {
        super(ordered, capacity);
    }

    public SafeIntArray(int capacity) {
        super(capacity);
    }

    /**
     * Works similar to Array.set() but grows the Array if necessary.
     */
    @Override
    public void set(int index, int value) {
        if (index >= items.length) {
            resize(Math.max(index + 1, items.length * 2));
        }
        if (index >= size) {
            size = index + 1;
        }
        items[index] = value;
    }

    /**
     * Works similar to Array.get(), but returns null instead of throwing
     * an index out of bounds exception.
     */
    @Override
    public int get(int index) {
        if (index < size) {
            return items[index];
        }
        return -1;
    }
}
