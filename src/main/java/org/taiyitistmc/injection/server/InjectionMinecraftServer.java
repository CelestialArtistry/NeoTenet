package org.taiyitistmc.injection.server;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;

public interface InjectionMinecraftServer {

    default void bridge$drainQueuedTasks() {
        throw new IllegalStateException("Not implemented");
    }

    default boolean isDebugging() {
        throw new IllegalStateException("Not implemented");
    }

    default boolean hasStopped() {
        throw new IllegalStateException("Not implemented");
    }

    default void initWorld(ServerLevel serverWorld, ServerLevelData worldInfo, WorldData saveData, WorldOptions worldOptions) {
        throw new IllegalStateException("Not implemented");
    }

    default void prepareLevels(ChunkProgressListener listener, ServerLevel serverWorld) {
        throw new IllegalStateException("Not implemented");
    }

    default void addLevel(ServerLevel level) {
        throw new IllegalStateException("Not implemented");
    }

    default void removeLevel(ServerLevel level) {
        throw new IllegalStateException("Not implemented");
    }

    default void executeModerately() {
        throw new IllegalStateException("Not implemented");
    }
}
