package com.artemis.utils;

import com.badlogic.gdx.utils.Array;


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
public class SafeArray<T> extends Array<T> {

    public SafeArray() {
        super();
    }

    public SafeArray(Array<? extends T> array) {
        super(array);
    }

    public SafeArray(boolean ordered, int capacity, Class<?> arrayType) {
        super(ordered, capacity, arrayType);
    }

    public SafeArray(boolean ordered, int capacity) {
        super(ordered, capacity);
    }

    public SafeArray(boolean ordered, T[] array, int start, int count) {
        super(ordered, array, start, count);
    }

    public SafeArray(Class<?> arrayType) {
        super(arrayType);
    }

    public SafeArray(int capacity) {
        super(capacity);
    }

    public SafeArray(T[] array) {
        super(array);
    }

    /**
     * Works similar to Array.set() but grows the Array if necessary.
     */
    @Override
    public void set(int index, T value) {
        if (index >= size) {
            resize(Math.max(index + 1, items.length * 2));
            size = index + 1;
        }
        items[index] = value;
    }

    /**
     * Works similar to Array.get(), but returns null instead of throwing
     * an index out of bounds exception.
     */
    @Override
    public T get(int index) {
        if (index < size) {
            return items[index];
        }
        return null;
    }
}
