package org.teneted.neotenet.injection.world.entity.decoration;

import net.minecraft.world.item.ItemStack;

public interface ItemFrameInjection {

    default void setItem(ItemStack itemStack, boolean updateNeighbours, boolean playSound) {
        throw new IllegalArgumentException("Not implemented");
    }
}
