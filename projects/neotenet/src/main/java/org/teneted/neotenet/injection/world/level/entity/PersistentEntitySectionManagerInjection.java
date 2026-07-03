package org.teneted.neotenet.injection.world.level.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

import java.io.IOException;
import java.util.List;

public interface PersistentEntitySectionManagerInjection {

    default List<Entity> getEntities(ChunkPos chunkCoordIntPair) {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean isPending(long pair) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void close(boolean save) throws IOException {
        throw new IllegalArgumentException("Not implemented");
    }
}
