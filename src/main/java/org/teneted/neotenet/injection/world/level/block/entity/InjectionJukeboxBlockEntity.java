package org.teneted.neotenet.injection.world.level.block.entity;

import net.minecraft.world.item.ItemStack;

public interface InjectionJukeboxBlockEntity {

    default void setSongItemWithoutPlaying(ItemStack itemstack, long ticksSinceSongStarted) {
        throw new IllegalArgumentException("Not Implemented");
    }
}
