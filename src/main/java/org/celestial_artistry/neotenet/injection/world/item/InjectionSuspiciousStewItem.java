package org.celestial_artistry.neotenet.injection.world.item;

import net.minecraft.world.item.ItemStack;

public interface InjectionSuspiciousStewItem {

    default void cancelUsingItem(net.minecraft.server.level.ServerPlayer entityplayer, ItemStack itemstack) {
        throw new IllegalStateException("Not implemented");
    }
}
