package org.teneted.neotenet.injection.world.level.storage;

import net.minecraft.server.level.ServerLevel;

public interface PrimaryLevelDataInjection {

    default void setWorld(ServerLevel world) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void checkName(String name) {
        throw new IllegalArgumentException("Not implemented");
    }
}
