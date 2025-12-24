package org.teneted.neotenet.injection.world.level.storage;

import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.LevelStem;

public interface InjectionPrimaryLevelData {

    default void checkName(String name) {
        throw new IllegalStateException("Not implemented");
    }

    default void setWorld(ServerLevel world) {
        throw new IllegalStateException("Not implemented");
    }
}
