package org.teneted.neotenet.injection.server.level;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.chunk.LevelChunk;

public interface ChunkHolderInjection {

    default LevelChunk getFullChunkNow() {
        throw new IllegalArgumentException("Not implemented");
    }

    default LevelChunk getFullChunkNowUnchecked() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void callEventIfUnloading(ChunkMap chunkmap) {
        throw new IllegalArgumentException("Not implemented");
    }
}
