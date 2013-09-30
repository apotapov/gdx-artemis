package com.badlogic.gdx.artemis;

import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * A tag class. All Components in the system must implement this interface.
 * 
 * DO NOT CREATE COMPONENTS USING A CONSTRUCTOR.
 * Use World.createComponent(Class<T extends Component> type) instead
 * This will allow Components to be pooled.
 * 
 * @author Arni Arent
 */
public interface Component extends Poolable {

}
