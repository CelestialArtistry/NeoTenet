package org.teneted.neotenet.injection.server.level;

import net.minecraft.core.BlockPos;

public interface ServerPlayerGameModeInjection {

    default boolean destroyBlock(BlockPos pos, int ack) {
        throw new IllegalArgumentException("Not implemented");
    }
}
