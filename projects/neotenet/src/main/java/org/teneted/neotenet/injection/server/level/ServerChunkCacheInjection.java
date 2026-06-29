package org.teneted.neotenet.injection.server.level;

import java.io.IOException;

public interface ServerChunkCacheInjection {

    default boolean isChunkLoaded(int chunkX, int chunkZ) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void close(boolean save) throws IOException {
        throw new IllegalArgumentException("Not implemented");
    }

    default void purgeUnload() {
        throw new IllegalArgumentException("Not implemented");
    }
}
