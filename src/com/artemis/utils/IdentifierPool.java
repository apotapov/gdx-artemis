package com.artemis.utils;

import com.badlogic.gdx.utils.IntArray;

/*
 * Used to generate distinct ids for entities and reuse them.
 */
public class IdentifierPool {
    protected IntArray ids;
    protected int nextAvailableId;

    public IdentifierPool() {
        ids = new IntArray();
    }

    public int checkOut() {
        if(ids.size > 0) {
            return ids.pop();
        }
        return nextAvailableId++;
    }

    public void checkIn(int id) {
        ids.add(id);
    }
}
