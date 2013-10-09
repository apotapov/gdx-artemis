package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;

/**
 * This system has an empty aspect so it processes no entities, but it still gets invoked.
 * You can use this system if you need to execute some game logic and not have to concern
 * yourself about aspects or entities.
 * 
 * @author Arni Arent
 *
 */
public abstract class VoidEntitySystem extends EntitySystem {

    public VoidEntitySystem() {
        super(Aspect.getEmpty());
    }

    @Override
    protected final void processEntities(Array<Entity> entities) {
        processSystem();
    }

    protected abstract void processSystem();
}
