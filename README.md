## Preamble

This is a [fork](https://code.google.com/p/artemis-framework/) of [Artemis Entity System Framework](http://gamadu.com/artemis/) which uses [libdx](http://libgdx.badlogicgames.com/) for containers and pooling. There are some major changes made to make Artemis more robust, memory efficient and intuitive. 

This is still very much a work in progress. Feedback is appreciated.

Major new features:
 - Uses libgdx containers and pooling.
 - Event (messaging) system was added (version 0.2)
 - Better GWT and HTML 5 support. ComponentMappers need manual instantiation.

## Demo

There is a separate [Demo Project](https://github.com/apotapov/gdx-artemis-demo) to help you get started. It is documented [here](https://github.com/apotapov/gdx-artemis/wiki/Quick-tutorial).

## Documentation

See [Wiki](https://github.com/apotapov/gdx-artemis/wiki/)

## Version 0.4
 - Deprecated DelayedEntityProcessingSystem.
 - Fixed some bugs and added pooling to GroupManager + unit tests.
 - Fixed a bug in ComponentManager that was causing NPE's when a component was removed from an entity.

### Previous changes
See [CHANGELOG.md](https://github.com/apotapov/gdx-artemis/blob/master/CHANGELOG.md)

## Alternative Artemis forks

 - [Original Implementation by Arni Arent*](https://code.google.com/p/artemis-framework/)
 - [junkdog](https://github.com/junkdog/artemis-odb)
 - [gemserker](https://github.com/gemserk/commons-gdx)

 * There are quite a few bugs in the original implementation. If you want to stay true to it, I would suggest looking at [junkdog's fork](https://github.com/junkdog/artemis-odb).
 