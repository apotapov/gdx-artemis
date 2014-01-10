# Preamble

This is a [fork](https://code.google.com/p/artemis-framework/) of [Artemis Entity System Framework](http://gamadu.com/artemis/) which uses [libdx](http://libgdx.badlogicgames.com/) for containers and pooling. There are some major changes made to make Artemis more robust, memory efficient and intuitive. 

This is still very much a work in progress. Feedback is appreciated.

Major new features:
 - Uses libgdx containers and pooling.
 - Event (messaging) system was added (version 0.2)
 - Better GWT and HTML 5 support. ComponentMappers need manual instantiation.

# Documentation

See [Wiki](https://github.com/apotapov/gdx-artemis/wiki/)

# Version 0.3
 - Adding the ability to dispose of the world.
 - For better support of GWT and HTML5 games with libgdx, removed automatic ComponentMapper initialization. ComponentMappers should be initialized manually in EntitySystem.initialize() method by calling mapper = world.getMapper(Component.class).
 - Changed the libgdx dependency to 0.9.9 instead of the nightly build.
 - Changed the EventSystem.getEvents() to take an Array instead of an ObjectSet to preserve the order of the events occuring.
 - Added a SingletonComponentManager to manage singleton components in the world.
 - Bug fix in DelayedEntityProcessingSystem.

# Previous versions
See CHANGELOG.md

# Alternative Artemis forks

 - [junkdog](https://github.com/junkdog/artemis-odb)
 - [gemserker](https://github.com/gemserk/commons-gdx)
