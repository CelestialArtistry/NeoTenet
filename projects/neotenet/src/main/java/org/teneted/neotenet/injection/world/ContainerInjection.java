package org.teneted.neotenet.injection.world;

import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;

public interface ContainerInjection {

    // CraftBukkit start
    default java.util.List<ItemStack> getContents() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void onOpen(CraftHumanEntity who) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void onClose(CraftHumanEntity who) {
        throw new IllegalArgumentException("Not implemented");
    }

    default java.util.List<org.bukkit.entity.HumanEntity> getViewers() {
        throw new IllegalArgumentException("Not implemented");
    }

    default org.bukkit.inventory.InventoryHolder getOwner() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void setMaxStackSize(int size) {
        throw new IllegalArgumentException("Not implemented");
    }

    default org.bukkit.Location getLocation() {
        throw new IllegalArgumentException("Not implemented");
    }
    // CraftBukkit end
}