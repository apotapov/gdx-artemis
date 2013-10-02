package com.badlogic.gdx.artemis;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import com.badlogic.gdx.artemis.annotations.Mapper;
import com.badlogic.gdx.artemis.managers.ComponentManager;
import com.badlogic.gdx.artemis.managers.EntityManager;
import com.badlogic.gdx.artemis.managers.Manager;
import com.badlogic.gdx.artemis.systems.EntitySystem;
import com.badlogic.gdx.artemis.utils.SafeArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * The primary instance for the framework. It contains all the managers.
 * 
 * You must use this to create, delete and retrieve entities.
 * 
 * It is also important to set the delta each game loop iteration, and initialize before game loop.
 * 
 * @author Arni Arent
 * 
 */
public class World {
    protected EntityManager em;
    protected ComponentManager cm;

    protected float delta;
    protected Array<Entity> added;
    protected Array<Entity> changed;
    protected Array<Entity> deleted;
    protected Array<Entity> enable;
    protected Array<Entity> disable;

    protected Performer addedPerformer;
    protected Performer changedPerformer;
    protected Performer deletedPerformer;
    protected Performer enablePerformer;
    protected Performer disablePerformer;

    protected ObjectMap<Class<? extends Manager>, Manager> managers;
    protected Array<Manager> managersArray;

    protected ObjectMap<Class<?>, EntitySystem> systems;
    protected Array<EntitySystem> systemsArray;

    public World() {
        this(new ComponentManager(), new EntityManager());
    }

    public World(ComponentManager cm, EntityManager em) {
        managers = new ObjectMap<Class<? extends Manager>, Manager>();
        managersArray = new SafeArray<Manager>();

        systems = new ObjectMap<Class<?>, EntitySystem>();
        systemsArray = new SafeArray<EntitySystem>();

        added = new SafeArray<Entity>();
        changed = new SafeArray<Entity>();
        deleted = new SafeArray<Entity>();
        enable = new SafeArray<Entity>();
        disable = new SafeArray<Entity>();

        addedPerformer = new Performer() {
            @Override
            public void perform(EntityObserver observer, Entity e) {
                observer.added(e);
            }
        };
        changedPerformer = new Performer() {
            @Override
            public void perform(EntityObserver observer, Entity e) {
                observer.changed(e);
            }
        };
        deletedPerformer = new Performer() {
            @Override
            public void perform(EntityObserver observer, Entity e) {
                observer.deleted(e);
            }
        };
        enablePerformer = new Performer() {
            @Override
            public void perform(EntityObserver observer, Entity e) {
                observer.enabled(e);
            }
        };
        disablePerformer = new Performer() {
            @Override
            public void perform(EntityObserver observer, Entity e) {
                observer.disabled(e);
            }
        };

        this.cm = cm;
        setManager(cm);

        this.em = em;
        setManager(em);
    }


    /**
     * Makes sure all managers systems are initialized in the order they were added.
     */
    public void initialize() {
        for (int i = 0; i < managersArray.size; i++) {
            managersArray.get(i).initialize();
        }

        for (int i = 0; i < systemsArray.size; i++) {
            ComponentMapperInitHelper.config(systemsArray.get(i), this);
            systemsArray.get(i).initialize();
        }
    }


    /**
     * Returns a manager that takes care of all the entities in the world.
     * entities of this world.
     * 
     * @return entity manager.
     */
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Returns a manager that takes care of all the components in the world.
     * 
     * @return component manager.
     */
    public ComponentManager getComponentManager() {
        return cm;
    }

    /**
     * Add a manager into this world. It can be retrieved later.
     * World will notify this manager of changes to entity.
     * 
     * @param manager to be added
     */
    public <T extends Manager> T setManager(T manager) {
        managers.put(manager.getClass(), manager);
        managersArray.add(manager);
        manager.setWorld(this);
        return manager;
    }

    /**
     * Returns a manager of the specified type.
     * 
     * @param <T>
     * @param managerType
     *            class type of the manager
     * @return the manager
     */
    public <T extends Manager> T getManager(Class<T> managerType) {
        return managerType.cast(managers.get(managerType));
    }

    /**
     * Deletes the manager from this world.
     * @param manager to delete.
     */
    public void deleteManager(Manager manager) {
        managers.remove(manager.getClass());
        managersArray.removeValue(manager, true);
    }




    /**
     * Time since last game loop.
     * 
     * @return delta time since last game loop.
     */
    public float getDelta() {
        return delta;
    }

    /**
     * You must specify the delta for the game here.
     * 
     * @param delta time since last game loop.
     */
    public void setDelta(float delta) {
        this.delta = delta;
    }



    /**
     * Adds a entity to this world.
     * 
     * @param e entity
     */
    public void addEntity(Entity e) {
        added.add(e);
    }

    /**
     * Ensure all systems are notified of changes to this entity.
     * If you're adding a component to an entity after it's been
     * added to the world, then you need to invoke this method.
     * 
     * @param e entity
     */
    public void changedEntity(Entity e) {
        changed.add(e);
    }

    /**
     * Delete the entity from the world.
     * 
     * @param e entity
     */
    public void deleteEntity(Entity e) {
        if (!deleted.contains(e, true)) {
            deleted.add(e);
        }
    }

    /**
     * (Re)enable the entity in the world, after it having being disabled.
     * Won't do anything unless it was already disabled.
     */
    public void enable(Entity e) {
        enable.add(e);
    }

    /**
     * Disable the entity from being processed. Won't delete it, it will
     * continue to exist but won't get processed.
     */
    public void disable(Entity e) {
        disable.add(e);
    }


    /**
     * Create and return a new or reused entity instance.
     * Will NOT add the entity to the world, use World.addEntity(Entity) for that.
     * 
     * @return entity
     */
    public Entity createEntity() {
        return em.createEntityInstance();
    }

    public <T extends Component> T createComponent(Class<T> type) {
        return cm.createComponent(type);
    }

    /**
     * Get a entity having the specified id.
     * 
     * @param entityId
     * @return entity
     */
    public Entity getEntity(int entityId) {
        return em.getEntity(entityId);
    }




    /**
     * Gives you all the systems in this world for possible iteration.
     * 
     * @return all entity systems in world.
     */
    public Array<EntitySystem> getSystems() {
        return systemsArray;
    }

    /**
     * Adds a system to this world that will be processed by World.process()
     * 
     * @param system the system to add.
     * @return the added system.
     */
    public <T extends EntitySystem> T setSystem(T system) {
        return setSystem(system, false);
    }

    /**
     * Will add a system to this world.
     * 
     * @param system the system to add.
     * @param passive wether or not this system will be processed by World.process()
     * @return the added system.
     */
    public <T extends EntitySystem> T setSystem(T system, boolean passive) {
        system.setWorld(this);
        system.setPassive(passive);

        systems.put(system.getClass(), system);
        systemsArray.add(system);

        return system;
    }

    /**
     * Removed the specified system from the world.
     * @param system to be deleted from world.
     */
    public void deleteSystem(EntitySystem system) {
        systems.remove(system.getClass());
        systemsArray.removeValue(system, true);
    }

    protected void notifySystems(Performer performer, Entity e) {
        for(int i = 0, s=systemsArray.size; s > i; i++) {
            performer.perform(systemsArray.get(i), e);
        }
    }

    protected void notifyManagers(Performer performer, Entity e) {
        for(int a = 0; managersArray.size > a; a++) {
            performer.perform(managersArray.get(a), e);
        }
    }

    /**
     * Retrieve a system for specified system type.
     * 
     * @param type type of system.
     * @return instance of the system in this world.
     */
    public <T extends EntitySystem> T getSystem(Class<T> type) {
        return type.cast(systems.get(type));
    }


    /**
     * Performs an action on each entity.
     * @param entities
     * @param performer
     */
    protected void check(Array<Entity> entities, Performer performer) {
        if (entities.size > 0) {
            for (int i = 0; entities.size > i; i++) {
                Entity e = entities.get(i);
                notifyManagers(performer, e);
                notifySystems(performer, e);
            }
            entities.clear();
        }
    }


    /**
     * Process all non-passive systems.
     */
    public void process() {
        check(added, addedPerformer);
        check(changed, changedPerformer);
        check(disable, disablePerformer);
        check(enable, enablePerformer);
        check(deleted, deletedPerformer);

        cm.clean();
        em.clean();

        for(int i = 0; systemsArray.size > i; i++) {
            EntitySystem system = systemsArray.get(i);
            if(!system.isPassive()) {
                system.process();
            }
        }
    }


    /**
     * Retrieves a ComponentMapper instance for fast retrieval of components from entities.
     * 
     * @param type of component to get mapper for.
     * @return mapper for specified component type.
     */
    public <T extends Component> ComponentMapper<T> getMapper(Class<T> type) {
        return ComponentMapper.getFor(type, this);
    }


    /*
     * Only used internally to maintain clean code.
     */
    protected interface Performer {
        void perform(EntityObserver observer, Entity e);
    }



    protected static class ComponentMapperInitHelper {

        @SuppressWarnings("unchecked")
        public static void config(Object target, World world) {
            try {
                Class<?> clazz = target.getClass();
                for (Field field : clazz.getDeclaredFields()) {
                    Mapper annotation = field.getAnnotation(Mapper.class);
                    if (annotation != null && Mapper.class.isAssignableFrom(Mapper.class)) {
                        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                        @SuppressWarnings("rawtypes")
                        Class componentType = (Class) genericType.getActualTypeArguments()[0];

                        field.setAccessible(true);
                        field.set(target, world.getMapper(componentType));
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error while setting component mappers", e);
            }
        }

    }

}
