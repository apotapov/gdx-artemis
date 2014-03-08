package com.artemis;


/**
 * DEPRECATED in favor of Aspect, which is much clearer description of what an Aspect is.
 *
 * @author Arni Arent
 *
 */
@Deprecated
public class Aspect extends Filter {

    /**
     * Access Aspect creation through static factory methods.
     */
    protected Aspect() {
    }

    /**
     * Creates an filter where an entity must possess all of the specified component types.
     * 
     * @param type a required component type
     * @param types a required component type
     * @return an filter that can be matched against entities
     */
    @SuppressWarnings("unchecked")
    public static Aspect getAspectForAll(Class<? extends Component> type, Class<? extends Component>... types) {
        Aspect filter = new Aspect();
        filter.all(type, types);
        return filter;
    }

    /**
     * Creates an filter where an entity must possess one of the specified component types.
     * 
     * @param type one of the types the entity must possess
     * @param types one of the types the entity must possess
     * @return an filter that can be matched against entities
     */
    @SuppressWarnings("unchecked")
    public static Aspect getAspectForOne(Class<? extends Component> type, Class<? extends Component>... types) {
        Aspect filter = new Aspect();
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
     * Aspect.getEmpty().one(A,B,C);
     * 
     * @return an empty Aspect that will reject all entities.
     */
    public static Aspect getEmpty() {
        return new Aspect();
    }

}
