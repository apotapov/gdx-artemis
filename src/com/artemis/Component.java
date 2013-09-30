package com.artemis;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * A tag class. All components in the system must extend this class.
 * 
 * @author Arni Arent
 */
public abstract class Component implements Poolable {
    private static int INDEX = 0;
    private final int index;

    public Component() {
        index = getIndexFor(this.getClass());
    }

    public int getTypeIndex() {
        return index;
    }

    private static ObjectIntMap<Class<? extends Component>> componentTypes =
            new ObjectIntMap<Class<? extends Component>>();

    public static int getIndexFor(Class<? extends Component> c) {
        if (componentTypes.containsKey(c)) {
            return componentTypes.get(c, -1);
        } else {
            componentTypes.put(c, INDEX);
            return INDEX++;
        }
    }
}
