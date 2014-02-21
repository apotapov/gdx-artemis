## Version 0.4
 - Deprecated DelayedEntityProcessingSystem.
 - Fixed some bugs and added pooling to GroupManager + unit tests.
 - Fixed a bug in ComponentManager that was causing NPE's when a component was removed from an entity.
 - Fixed an entity removal bug which was causing NPE's.
 - Used JProfiler to identify a few CPU intensive hotspots and made performance improving changes.
 - Created GenericGroupManager.
 - Deprecated Aspect in favor of Filter. (better clarity of purpose)
 - PlayerManager and TeamManager have been cleaned up. 
 - TagManager is deprecated in favor or SingletonEntityManager.
 - Added SkipEntityProcessingSystem to replace DelayedEntityProcessingSystem. Added ExpirationEntitySystem.
 - Cleaning up Event System. Constraining World to only one Event System. Removed some unused classes, added classes for handling events.

## Version 0.3
 - Adding the ability to dispose of the world.
 - For better support of GWT and HTML5 games with libgdx, removed automatic ComponentMapper initialization. ComponentMappers should be initialized manually in EntitySystem.initialize() method by calling mapper = world.getMapper(Component.class).
 - Changed the libgdx dependency to 0.9.9 instead of the nightly build.
 - Changed the EventSystem.getEvents() to take an Array instead of an ObjectSet to preserve the order of the events occuring.
 - Added a SingletonComponentManager to manage singleton components in the world.
 - Bug fix in DelayedEntityProcessingSystem.
 
## Version 0.2
 - Created an event system to allow inter-system communication.
 - Speed improvement: Removed some unnecessary accessor calls.
 - Deprecated Mapper annotation and imporved ComoponentMapper intialization
 - Allow inheritance for EntitySystems (component mappers are initialized in parent objects as well)
 - Fixed a bug in EntitySystem.check(e) and made it more performant.
 - Adding ComponentMapper reuse. No need to create new instances for all the systems.
 - Adding @IgnoreMapper annotation to allow bypassing ComponentMapper initialization during world initialization. (useful for generic systems with generic mappers, where reflection won't work)
 - Adding a notion of stopping event propagation by marking event handled.
 - Making a dent in unit testing.

## Version 0.1

 - Some files have been reshuffled in the packages for better organization.
 - Most private fields and methods have been changed to protected to allow for easier extension.
 - Component is now an interface.
 - ComponentType has been removed. The indexing done in the ComponentManager.
 - Bag and ImmutableBag have been removed, in favor of a slightly modified com.badlogic.gdx.utils.Array.
 - HashMaps have been replaced by com.badlogic.gdx.utils.ObjectMap. ObjectIntMap and IntArray are used where appropriate.
 - Starting to add more Javadoc to classes.
 - No more classes instantiated every time World.process is run.
 - IdentifierPool has been moved to a separate class.
 - Removed all the unused utils classes.
 - Added a check to EntitySystem to prune activeEntities before processing. (in case of removal by a different system earlier on in the process cycle)
 - Cleaned up some compiler warnings.
 - Mavenize the project
