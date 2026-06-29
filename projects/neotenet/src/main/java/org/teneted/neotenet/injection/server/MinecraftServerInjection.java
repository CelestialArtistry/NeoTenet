package org.teneted.neotenet.injection.server;

import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;

import java.util.Optional;

public interface MinecraftServerInjection {

    default void initWorld(ServerLevel serverlevel, ServerLevelData serverleveldata, WorldData saveData, WorldOptions worldoptions) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void prepareLevels(ServerLevel serverlevel) {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean hasStopped() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void addLevel(ServerLevel level) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void removeLevel(ServerLevel level) {
        throw new IllegalArgumentException("Not implemented");
    }

    default ServerLevel findRespawnDimension(ServerLevel world) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void setRespawnData(LevelData.RespawnData respawnData, ServerLevel world) {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean isDebugging() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void handleCustomClickAction(Identifier identifier, Optional<Tag> optional, ServerPlayer player) {
        throw new IllegalArgumentException("Not implemented");
    }
}
