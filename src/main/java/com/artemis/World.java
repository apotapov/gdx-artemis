package com.artemis;

import com.artemis.managers.ComponentManager;
import com.artemis.managers.EntityManager;
import com.artemis.managers.Manager;
import com.artemis.systems.EntitySystem;
import com.artemis.systems.event.EventDeliverySystem;
import com.artemis.systems.event.SystemEvent;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
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

    /**
     * Only used internally to maintain clean code.
     */
    protected static interface Performer {
        void perform(EntityObserver observer, Entity e);
    }

    protected EntityManager em;
    protected ComponentManager cm;

    protected float delta;
    protected ObjectSet<Entity> added;
    protected ObjectSet<Entity> changed;
    protected ObjectSet<Entity> deleted;
    protected ObjectSet<Entity> enable;
    protected ObjectSet<Entity> disable;

    protected Performer addedPerformer;
    protected Performer changedPerformer;
    protected Performer deletedPerformer;
    protected Performer enablePerformer;
    protected Performer disablePerformer;

    protected Array<Manager> managers;

    protected EventDeliverySystem eventSystem;
    protected Array<EntitySystem> systems;

    public World() {
        this(new ComponentManager(), new EntityManager());
    }

    /**
     * Create a world with a specified component and entity
     * manager.
     * 
     * @param cm ComponentManager to use.
     * @param em EntityManager to use.
     */
    public World(ComponentManager cm, EntityManager em) {
        managers = new Array<Manager>();
        systems = new Array<EntitySystem>();

        added = new ObjectSet<Entity>();
        changed = new ObjectSet<Entity>();
        deleted = new ObjectSet<Entity>();
        enable = new ObjectSet<Entity>();
        disable = new ObjectSet<Entity>();

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
     * Makes sure all managers systems are initialized
     * in the order they were added.
     */
    public void initialize() {
        // Can't use iterators here because initialize often calls
        // getSystem or getManager
        for (int i = 0; i < managers.size; i++) {
            managers.get(i).initialize();
        }

        if (eventSystem != null) {
            eventSystem.initialize();
        }

        for (int i = 0; i < systems.size; i++) {
            systems.get(i).initialize();
        }
    }


    /**
     * Returns a manager that takes care of all the entities in the world.
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
     * @param <T> Manager type
     * @param manager to be added
     * @return the manager that was added
     */
    public <T extends Manager> T setManager(T manager) {
        managers.add(manager);
        manager.setWorld(this);
        return manager;
    }

    /**
     * Returns a manager of the specified type.
     * 
     * @param <T> Manager type
     * @param managerType class type of the manager
     * @return the manager
     */
    @SuppressWarnings("unchecked")
    public <T extends Manager> T getManager(Class<T> managerType) {
        for (Manager manager : managers) {
            if (manager.getClass().equals(managerType)) {
                return (T) manager;
            }
        }
        return null;
    }

    /**
     * Deletes the manager from this world.
     * @param manager to delete.
     */
    public void deleteManager(Manager manager) {
        managers.removeValue(manager, true);
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
     * @param e Entity to remove
     */
    public void deleteEntity(Entity e) {
        if (!deleted.contains(e)) {
            deleted.add(e);
        }

        if (added.contains(e)) {
            added.remove(e);
        }
    }

    /**
     * (Re)enable the entity in the world, after it having being disabled.
     * Won't do anything unless it was already disabled.
     * 
     * @param e entity to enable
     */
    public void enable(Entity e) {
        enable.add(e);
    }

    /**
     * Disable the entity from being processed. Won't delete it, it will
     * continue to exist but won't get processed.
     * 
     * @param e entity to disable
     */
    public void disable(Entity e) {
        disable.add(e);
    }


    /**
     * Create and return a new or reused entity instance.
     * Will NOT add the entity to the world, use World.addEntity(Entity)
     * for that.
     * 
     * @return created entity
     */
    public Entity createEntity() {
        return em.createEntityInstance();
    }

    /**
     * Create and return a new or reused component instance of specified type.
     * 
     * @param <T> Type of component
     * @param type Type of component to return
     * @return Created component
     */
    public <T extends Component> T createComponent(Class<T> type) {
        return cm.createComponent(type);
    }

    /**
     * Creates an instance of an event of a specified type. The event
     * needs to be posted to the world in order to be propagated to listeners.
     * 
     * @param <T> Type of event
     * @param type Type of event to create.
     * @return Event of specified type.
     */
    public <T extends SystemEvent> T createEvent(Class<T> type) {
        return SystemEvent.createEvent(type);
    }

    /**
     * Get a entity having the specified id.
     * 
     * @param entityId id of the entity to retrieve.
     * @return entity Entity or null.
     */
    public Entity getEntity(int entityId) {
        return em.getEntity(entityId);
    }

    /**
     * Post event to all event systems.
     * 
     * @param sendingSystem System that is sending the event.
     * @param event Event being sent
     */
    public void postEvent(EntitySystem sendingSystem, SystemEvent event) {
        if (eventSystem != null) {
            eventSystem.postEvent(sendingSystem, event);
        }
    }

    /**
     * Retrieve events from all systems. The set ensures that events are not repeated.
     * 
     * @param <T> Type of event
     * @param pollingSystem System that is requesting the events.
     * @param type Type of events requested.
     * @param events Event set to populate with events
     */
    public <T extends SystemEvent> void getEvents(EntitySystem pollingSystem, Class<T> type, Array<T> events) {
        events.clear();
        if (eventSystem != null) {
            eventSystem.getEvents(pollingSystem, type, events);
        }
    }

    /**
     * Gives you all the systems in this world for possible iteration.
     * 
     * @return all entity systems in world.
     */
    public Array<EntitySystem> getSystems() {
        return systems;
    }

    /**
     * Returns the event system, or null if not set.
     * 
     * @return Event System for the world.
     */
    public EventDeliverySystem getEventDeliverySystem() {
        return eventSystem;
    }

    /**
     * Set the system for event delivery.
     * 
     * @param eventSystem Event delivery system
     */
    public void setEventDeliverySystem(EventDeliverySystem eventSystem) {
        this.eventSystem = eventSystem;
    }

    /**
     * Adds a system to this world that will be processed by World.process()
     * 
     * @param <T> Type of entity system
     * @param system the system to add.
     * @return the added system.
     */
    public <T extends EntitySystem> T setSystem(T system) {
        system.setWorld(this);

        if (system instanceof EventDeliverySystem) {
            eventSystem = (EventDeliverySystem) system;
        } else {
            systems.add(system);
        }

        return system;
    }

    /**
     * Will add a system to this world.
     * 
     * @param <T> Type of entity system
     * @param system the system to add.
     * @param passive whether or not this system will be processed by World.process()
     * @return the added system.
     */
    public <T extends EntitySystem> T setSystem(T system, boolean passive) {
        setSystem(system);
        system.setPassive(passive);
        return system;
    }

    /**
     * Removed the specified system from the world.
     * @param system to be deleted from world.
     */
    public void deleteSystem(EntitySystem system) {
        if (system instanceof EventDeliverySystem) {
            eventSystem = null;
        } else {
            systems.removeValue(system, true);
        }
    }

    /**
     * Notify systems of changes to the specified entity.
     * 
     * @param performer The performer that notifies the systems.
     * @param e Entity that has been affected.
     */
    protected void notifySystems(Performer performer, Entity e) {
        for(int i = 0; i < systems.size; i++) {
            performer.perform(systems.get(i), e);
        }
    }

    /**
     * Notify managers of changes to the specified entity.
     * 
     * @param performer The performer that notifies the managers.
     * @param e Entity that has been affected.
     */
    protected void notifyManagers(Performer performer, Entity e) {
        for(Manager manager : managers) {
            performer.perform(manager, e);
        }
    }

    /**
     * Retrieve a system for specified system type.
     * 
     * @param <T> Type of entity system
     * @param type type of system.
     * @return instance of the system in this world.
     */
    @SuppressWarnings("unchecked")
    public <T extends EntitySystem> T getSystem(Class<T> type) {
        for (int i = 0; i < systems.size; i++) {
            EntitySystem system = systems.get(i);
            if (system.getClass().equals(type)) {
                return (T) system;
            }
        }
        return null;
    }


    /**
     * Performs an action on each entity.
     * 
     * @param entities
     * @param performer
     */
    protected void check(ObjectSet<Entity> entities, Performer performer) {
        if (entities.size > 0) {
            for (Entity e : entities) {
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

        if (eventSystem != null) {
            eventSystem.update();
        }

        for(int i = 0; i < systems.size; i++) {
            EntitySystem system = systems.get(i);
            if(!system.isPassive()) {
                system.process();
            }
        }
    }


    /**
     * Retrieves a ComponentMapper instance for fast retrieval of
     * components from entities.
     * 
     * @param <T> Type of component
     * @param type of component to get mapper for.
     * @return mapper for specified component type.
     */
    public <T extends Component> ComponentMapper<T> getMapper(Class<T> type) {
        return cm.getMapper(type);
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

        for (Manager manager : managers) {
            manager.dispose();
        }

        managers.clear();
        systems.clear();

        if (eventSystem != null) {
            eventSystem.dispose();
            eventSystem = null;
        }
    }

}
