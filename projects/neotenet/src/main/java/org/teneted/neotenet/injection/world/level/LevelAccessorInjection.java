package org.teneted.neotenet.injection.world.level;

import net.minecraft.server.level.ServerLevel;

public interface LevelAccessorInjection {

    default ServerLevel getMinecraftWorld() {
        throw new IllegalArgumentException("Not implemented");
    }
}
