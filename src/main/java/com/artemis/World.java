package com.artemis;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import com.artemis.annotations.IgnoreMapper;
import com.artemis.managers.ComponentManager;
import com.artemis.managers.EntityManager;
import com.artemis.managers.Manager;
import com.artemis.systems.EntitySystem;
import com.artemis.systems.event.EventSystem;
import com.artemis.systems.event.SystemEvent;
import com.artemis.utils.SafeArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

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
public class World implements Disposable {
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
    protected Array<EventSystem> eventSystems;
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

        this.eventSystems = new Array<EventSystem>();
    }


    /**
     * Makes sure all managers systems are initialized in the order they were added.
     */
    public void initialize() {
        for (int i = 0; i < managersArray.size; i++) {
            managersArray.get(i).initialize();
        }

        for (int i = 0; i < systemsArray.size; i++) {
            assignComponentMappers(systemsArray.get(i));
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
     * Post event to all event systems.
     * 
     * @param sendingSystem
     * @param event
     */
    public void postEvent(EntitySystem sendingSystem, SystemEvent event) {
        for (EventSystem eventSystem : eventSystems) {
            eventSystem.postEvent(sendingSystem, event);
        }
    }

    /**
     * Retrieve events from all systems. The set ensures that events are not repeated.
     * 
     * @param pollingSystem System that is requesting the events.
     * @param type Type of events requested.
     * @param events Event set to populate with events
     */
    public <T extends SystemEvent> void getEvents(EntitySystem pollingSystem, Class<T> type, ObjectSet<T> events) {
        for (EventSystem eventSystem : eventSystems) {
            eventSystem.getEvents(pollingSystem, type, events);
        }
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
        system.setWorld(this);

        systems.put(system.getClass(), system);
        systemsArray.add(system);
        if (system instanceof EventSystem) {
            eventSystems.add((EventSystem)system);
        }

        return system;
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
        if (system instanceof EventSystem) {
            eventSystems.add((EventSystem)system);
        }

        return system;
    }

    /**
     * Removed the specified system from the world.
     * @param system to be deleted from world.
     */
    public void deleteSystem(EntitySystem system) {
        systems.remove(system.getClass());
        systemsArray.removeValue(system, true);
        if (system instanceof EventSystem) {
            eventSystems.removeValue((EventSystem)system, true);
        }
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

    @SuppressWarnings("unchecked")
    protected void assignComponentMappers(EntitySystem system) {
        try {

            Class<?> clazz = system.getClass();
            while (clazz != null) {
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.getType().isAssignableFrom(ComponentMapper.class)) {

                        // check whether the field is marked to be ignored
                        IgnoreMapper annotation = field.getAnnotation(IgnoreMapper.class);
                        if (annotation == null) {
                            ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                            @SuppressWarnings("rawtypes")
                            Class componentType = (Class) genericType.getActualTypeArguments()[0];

                            field.setAccessible(true);
                            field.set(system, getMapper(componentType));
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while setting component mappers for system: " + system, e);
        }
    }

    @Override
    public void dispose() {
        em.dispose();
        cm.dispose();

        added.clear();
        changed.clear();
        deleted.clear();
        enable.clear();
        disable.clear();

        for (Manager manager : managersArray) {
            manager.dispose();
        }
        managers.clear();
        managersArray.clear();

        systems.clear();
        systemsArray.clear();

        for (EventSystem eventSystem : eventSystems) {
            eventSystem.dispose();
        }
        eventSystems.clear();
    }

}
