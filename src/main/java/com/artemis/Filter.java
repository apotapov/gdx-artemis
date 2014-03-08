package com.artemis;

import java.util.BitSet;

import com.artemis.managers.ComponentManager;

/**
 * A Filter is used by systems as a matcher against entities, to check if a
 * system is interested in an entity. Filters define what sort of component
 * types an entity must possess, or not possess.
 * 
 * Exclusion takes priority. So if a filter defines an entity's component
 * as an exclusion, the entity will not be processed whether even if the same
 * component appears in "all" or "any" definition of the filter.
 * 
 * Examples
 * 
 * This creates a filter where an entity must possess A and B and C:
 * 
 * Filter.allComponents(A.class, B.class, C.class)
 * 
 * This creates a filter where an entity must possess A and B and C,
 * but must not possess U or V:
 * 
 * Filter.allComponents(A.class, B.class, C.class).exclude(U.class, V.class)
 * 
 * This creates a filter where an entity must possess A and B and C,
 * but must not possess U or V, but must possess one of X or Y or Z:
 * 
 * Filter.allComponents(A.class, B.class, C.class).exclude(U.class, V.class).one(X.class, Y.class, Z.class)
 *
 * You can create and compose filters in many ways:
 * 
 * Filter.getEmpty().one(X.class, Y.class, Z.class).all(A.class, B.class, C.class).exclude(U.class, V.class)
 * 
 * is the same as:
 * 
 * Filter.allComponents(A.class, B.class, C.class).exclude(U.class, V.class).one(X.class, Y.class, Z.class)
 *
 * @author Arni Arent
 *
 */
public class Filter {

    public BitSet allSet;
    public BitSet exclusionSet;
    public BitSet anySet;

    /**
     * Access Filter creation through static factory methods.
     */
    protected Filter() {
        this.allSet = new BitSet();
        this.exclusionSet = new BitSet();
        this.anySet = new BitSet();
    }

    /**
     * Returns a filter where an entity must possess all of the specified component types.
     * @param type a required component type
     * @param types a required component type
     * @return a filter that can be matched against entities
     */
    @SuppressWarnings("unchecked")
    public Filter all(Class<? extends Component> type, Class<? extends Component>... types) {
        allSet.set(ComponentManager.getComponentClassIndex(type));

        for (Class<? extends Component> t : types) {
            allSet.set(ComponentManager.getComponentClassIndex(t));
        }

        return this;
    }

    /**
     * Excludes all of the specified component types from the filter. A system will not be
     * interested in an entity that possesses one of the specified exclusion component types.
     * 
     * @param type component type to exclude
     * @param types component type to exclude
     * @return a filter that can be matched against entities
     */
    @SuppressWarnings("unchecked")
    public Filter exclude(Class<? extends Component> type, Class<? extends Component>... types) {
        exclusionSet.set(ComponentManager.getComponentClassIndex(type));

        for (Class<? extends Component> t : types) {
            exclusionSet.set(ComponentManager.getComponentClassIndex(t));
        }
        return this;
    }

    /**
     * Deprecated Use any() instead.
     * 
     * Returns a filter where an entity must possess one of the specified component types.
     * 
     * @param type one of the types the entity must possess
     * @param types one of the types the entity must possess
     * @return a filter that can be matched against entities
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public Filter one(Class<? extends Component> type, Class<? extends Component>... types) {
        return any(type, types);
    }

    /**
     * Returns a filter where an entity must possess one of the specified component types.
     * @param type one of the types the entity must possess
     * @param types one of the types the entity must possess
     * @return a filter that can be matched against entities
     */
    @SuppressWarnings("unchecked")
    public Filter any(Class<? extends Component> type, Class<? extends Component>... types) {
        anySet.set(ComponentManager.getComponentClassIndex(type));

        for (Class<? extends Component> t : types) {
            anySet.set(ComponentManager.getComponentClassIndex(t));
        }
        return this;
    }

    /**
     * Creates a filter where an entity must possess all of the specified component types.
     * 
     * @param type a required component type
     * @param types a required component type
     * @return a filter that can be matched against entities
     */
    @SuppressWarnings("unchecked")
    public static Filter allComponents(Class<? extends Component> type, Class<? extends Component>... types) {
        Filter filter = new Filter();
        filter.all(type, types);
        return filter;
    }

    /**
     * Creates a filter where an entity must possess one of the specified component types.
     * 
     * @param type one of the types the entity must possess
     * @param types one of the types the entity must possess
     * @return a filter that can be matched against entities
     */
    @SuppressWarnings("unchecked")
    public static Filter anyComponents(Class<? extends Component> type, Class<? extends Component>... types) {
        Filter filter = new Filter();
        filter.one(type, types);
        return filter;
    }

    /**
     * Creates and returns an empty filter. This can be used if you want a system that processes no entities, but
     * still gets invoked. Typical usages is when you need to create special purpose systems for debug rendering,
     * like rendering FPS, how many entities are active in the world, etc.
     * 
     * You can also use the all, one and exclude methods on this filter, so if you wanted to create a system that
     * processes only entities possessing just one of the components A or B or C, then you can do:
     * Filter.getEmpty().one(A,B,C);
     * 
     * @return an empty Filter that will reject all entities.
     */
    public static Filter getEmpty() {
        return new Filter();
    }

}
