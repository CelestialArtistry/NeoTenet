package org.teneted.neotenet.injection.world.level.block.entity;

import net.minecraft.world.item.ItemStack;

public interface JukeboxBlockEntityInjection {

    default void setSongItemWithoutPlaying(ItemStack itemStack, long ticksSinceSongStarted) {
        throw new IllegalArgumentException("Not implemented");
    }
}
