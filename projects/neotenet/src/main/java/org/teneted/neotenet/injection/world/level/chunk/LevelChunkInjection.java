package org.teneted.neotenet.injection.world.level.chunk;

public interface LevelChunkInjection {

    default void loadCallback() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void unloadCallback() {
        throw new IllegalArgumentException("Not implemented");
    }
}
