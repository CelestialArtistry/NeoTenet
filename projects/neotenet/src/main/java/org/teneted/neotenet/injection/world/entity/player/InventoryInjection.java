package org.teneted.neotenet.injection.world.entity.player;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface InventoryInjection {

    default List<ItemStack> getArmorContents() {
        throw new IllegalArgumentException("Not implemented");
    }

    default int canHold(ItemStack itemstack) {
        throw new IllegalArgumentException("Not implemented");
    }
}
