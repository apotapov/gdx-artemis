package com.badlogic.gdx.artemis.managers;

import com.badlogic.gdx.artemis.Entity;
import com.badlogic.gdx.artemis.EntityObserver;
import com.badlogic.gdx.artemis.World;


/**
 * Manager.
 * 
 * @author Arni Arent
 * 
 */
public abstract class Manager implements EntityObserver {
    protected World world;

    public void initialize() {
    }

    public void setWorld(World world) {
        this.world = world;
    }

    protected World getWorld() {
        return world;
    }

    @Override
    public void added(Entity e) {
    }

    @Override
    public void changed(Entity e) {
    }

    @Override
    public void deleted(Entity e) {
    }

    @Override
    public void disabled(Entity e) {
    }

    @Override
    public void enabled(Entity e) {
    }
}
