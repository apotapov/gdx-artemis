## Preamble

This is a [fork](https://code.google.com/p/artemis-framework/) of [Artemis Entity System Framework](http://gamadu.com/artemis/) which uses [libdx](http://libgdx.badlogicgames.com/) for containers and pooling. There are some major changes made to make Artemis more robust, memory efficient and intuitive. 

This is still very much a work in progress. Feedback is appreciated.

Major new features:
 - Uses libgdx containers and pooling.
 - Event (messaging) system was added (version 0.2)
 - Better GWT and HTML 5 support. ComponentMappers need manual instantiation.
 - Battle tested, with many major bugs fixed
 - Optimized for memory consumption and speed of execution using [Java profiler](http://www.ej-technologies.com/products/jprofiler/overview.html)

## Demo

There is a separate [Demo Project](https://github.com/apotapov/gdx-artemis-demo) to help you get started. It is documented [here](https://github.com/apotapov/gdx-artemis/wiki/Quick-tutorial).

## Documentation

See [Wiki](https://github.com/apotapov/gdx-artemis/wiki/)

## Version 0.4
 - Deprecated DelayedEntityProcessingSystem.
 - Fixed some bugs and added pooling to GroupManager + unit tests.
 - Fixed a bug in ComponentManager that was causing NPE's when a component was removed from an entity.
 - Fixed an entity removal bug which was causing NPE's.
 - Used JProfiler to identify a few CPU intensive hotspots and made performance improving changes.
 - Created GenericGroupManager.
 - Deprecated Aspect in favor of Filter. (better clarity of purpose)

### Previous changes
See [CHANGELOG.md](https://github.com/apotapov/gdx-artemis/blob/master/CHANGELOG.md)

## Alternative Artemis forks

 - [Original Implementation by Arni Arent](https://code.google.com/p/artemis-framework/)
 - [junkdog](https://github.com/junkdog/artemis-odb)
 - [gemserker](https://github.com/gemserk/commons-gdx)