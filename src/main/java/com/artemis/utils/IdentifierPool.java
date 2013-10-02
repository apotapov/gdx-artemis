package com.artemis.utils;

import com.badlogic.gdx.utils.IntArray;

/**
 * Used to generate distinct ids for entities and reuse them.
 */
public class IdentifierPool {
    protected IntArray ids;
    protected int nextAvailableId;

    /**
     * Default constructor
     */
    public IdentifierPool() {
        ids = new IntArray();
    }

    /**
     * Return an available id to use.
     * @return Available id.
     */
    public int checkOut() {
        if(ids.size > 0) {
            return ids.pop();
        }
        return nextAvailableId++;
    }

    /**
     * Recycles the specified id.
     * @param id Id to return back to the pool.
     */
    public void checkIn(int id) {
        ids.add(id);
    }
}
