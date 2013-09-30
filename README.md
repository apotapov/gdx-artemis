# Preamble

This is a [fork](https://code.google.com/p/artemis-framework/) of [Artemis Entity System Framework](http://gamadu.com/artemis/).
The major difference is that the code base has been refactored to use libgdx containers and pooling.
The hope is to eventually add it to libgdx extensions.

# What's changed

 - The packages have been renamed to follow the libgdx standard.
 - Most private fields and methods have been changed to protected to allow for easier extension.
 - Component is now an interface.
 - ComponentType has been removed. The indexing done in the ComponentManager.
 - Bag and ImmutableBag have been removed, a slightly modified com.badlogic.gdx.utils.Array.
 - HashMaps have been replaced by com.badlogic.gdx.utils.ObjectMap. ObjectIntMap and IntArray are used where appropriate.
 - Starting to add more Javadoc to classes.
 - No more classes instantiated every time World.process is run.

# What's left to be done

 - Get rid of the SafeArray class. (needed because of the difference between Bag and Array get/set implementation)
 - Javadoc the rest of the classes.
 - Do some profiling and optimization to determine bottle necks.

# Alternative Artemis forks

 - [junkdog](https://github.com/junkdog/artemis-odb)
 - [gemserker](https://github.com/gemserk/artemis)
