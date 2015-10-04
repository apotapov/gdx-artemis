## Summary

This is a [fork](https://code.google.com/p/artemis-framework/) of [Artemis Entity System Framework](http://gamadu.com/artemis/) which uses [libgdx](http://libgdx.badlogicgames.com/) for containers and pooling. There are some major changes made to make Artemis more robust, memory efficient and intuitive. 

### Note

This library is no longer actively maintained. I might come back to it at some point in the future if/when I get back into Android game development.

For now I recommend you checkout an excelent library [artemis-odb](https://github.com/junkdog/artemis-odb). It has some nice extensions for events and libgdx systems: [artemis-odb-contrib](https://github.com/DaanVanYperen/artemis-odb-contrib). I personally haven't played with it but judging by the [benchmarks](https://github.com/junkdog/entity-system-benchmarks), it's very performant and under active development.

## Maven

```xml
<dependency>
    <groupId>com.roundtriangles.games</groupId>
    <artifactId>gdx-artemis</artifactId>
    <version>0.5</version>
</dependency>
```

## Snapshot

### Enable snapshot repository
```xml
<repositories>
    <repository>
        <id>snapshots-repo</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <releases><enabled>false</enabled></releases>
        <snapshots><enabled>true</enabled></snapshots>
    </repository>
</repositories>
```

### Add dependency
```xml
<dependency>
    <groupId>com.roundtriangles.games</groupId>
    <artifactId>gdx-artemis</artifactId>
    <version>0.6-SNAPSHOT</version>
</dependency>
```

## Description

[Artemis](http://gamadu.com/artemis/) is an [Entity Component System](http://en.wikipedia.org/wiki/Entity_component_system) written in Java as a framework to manage entities in a game world. This library has been built on top of [libdx](http://libgdx.badlogicgames.com/), which is a Java library for developing cross-platform games for Android, iOS, Desktop and HTML5. For more information on gdx-artemis see the [Wiki](https://github.com/apotapov/gdx-artemis/wiki).

The library is in active development. Feedback is much appreciated.

Major new features:
 - Uses libgdx containers and pooling.
 - Event (messaging) system was added (version 0.2)
 - Better GWT and HTML 5 support. ComponentMappers need manual instantiation.
 - Battle tested, with many major bugs fixed
 - Optimized for memory consumption and speed of execution using [Java profiler](http://www.ej-technologies.com/products/jprofiler/overview.html)
 - deployed to Maven Central repository.

## Demo

There is a separate [Demo Project](https://github.com/apotapov/gdx-artemis-demo) to help you get started. It is documented [here](https://github.com/apotapov/gdx-artemis/wiki/Quick-tutorial).

## Documentation

See [Wiki](https://github.com/apotapov/gdx-artemis/wiki/)

## Latest Changes (0.6-SNAPSHOT)

### Previous changes
See [CHANGELOG.md](https://github.com/apotapov/gdx-artemis/blob/master/CHANGELOG.md)

## Games Using gdx-artemis

* [Viking Chess](https://play.google.com/store/apps/details?id=com.captstudios.games.tafl)
