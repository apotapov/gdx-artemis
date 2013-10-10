# Preamble

This is a [fork](https://code.google.com/p/artemis-framework/) of [Artemis Entity System Framework](http://gamadu.com/artemis/).
The major difference is that the code base has been refactored to use libgdx containers and pooling. An event (messaging) system
has been added to version 0.2. This is still very much a work in progress. Feedback is appreciated.

# Version 0.2
 - Created an event system to allow inter-system communication.

# Version 0.1

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


# Alternative Artemis forks

 - [junkdog](https://github.com/junkdog/artemis-odb)
 - [gemserker](https://github.com/gemserk/artemis)
